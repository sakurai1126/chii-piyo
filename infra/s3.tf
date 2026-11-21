# 写真・動画を保存するS3バケットの設定

locals {
  # 本番用と開発・E2E用で同じ構成を持つため、差分の CORS 許可オリジンだけを持たせてまとめて定義する
  media_buckets = {
    storage = {
      name = "chii-piyo-storage-569946568026-ap-northeast-1-an"
      # 本番フロントに加え、ローカル開発から本番データを確認する場合に備えて localhost も許可する
      allowed_origins = ["https://chii-piyo.s-repo.link", "http://localhost:3000"]
    }
    test = {
      name            = "chii-piyo-test-569946568026-ap-northeast-1-an"
      allowed_origins = ["http://localhost:3000"]
    }
  }

  # 共通化のためメディアとログ用をまとめて all_buckets に格納
  all_buckets = merge(aws_s3_bucket.media, { logs = aws_s3_bucket.logs })
}

# S3バケットを作成
resource "aws_s3_bucket" "media" {
  for_each = local.media_buckets
  bucket   = each.value.name
  # アカウント専用の名前空間に作成
  bucket_namespace = "account-regional"

  # 誤操作によるバケット削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# バージョニングを有効化して、誤って上書き・削除したメディアを復元できるようにする
resource "aws_s3_bucket_versioning" "media" {
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

  # バージョニングを有効化して、オブジェクトの過去バージョンを保持
  versioning_configuration {
    status = "Enabled"
  }
}

# サーバーサイド暗号化を有効化して、保存データの安全性を確保する
resource "aws_s3_bucket_server_side_encryption_configuration" "media" {
  for_each = local.all_buckets
  bucket   = local.all_buckets[each.key].id

  # バケットに対する暗号化ルールを設定する
  rule {
    # デフォルトでサーバーサイド暗号化を適用する
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
    # バケットキーを有効化（AWS規定値）
    bucket_key_enabled = true
    # SSE-C (顧客管理の暗号化キー) は使用しない
    blocked_encryption_types = ["SSE-C"]
  }
}

# ACLを無効化し、アクセス制御をIAMポリシーに一本化する（AWS規定値）
resource "aws_s3_bucket_ownership_controls" "media" {
  for_each = local.all_buckets
  bucket   = local.all_buckets[each.key].id

  # バケットの所有権を強制し、ACLによるアクセス制御を無効化する
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

# メディアは署名付きURLでのみ配信するため公開設定を全て拒否する（AWS規定値）
resource "aws_s3_bucket_public_access_block" "media" {
  for_each = local.all_buckets
  bucket   = local.all_buckets[each.key].id

  # 公開アクセスを全てブロック
  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
}

# HTTPでのアクセスを全て拒否する
data "aws_iam_policy_document" "media_https_only" {
  for_each = local.all_buckets

  # 暗号化されていない通信からの全操作を拒否する
  statement {
    sid     = "DenyInsecureTransport"
    effect  = "Deny"
    actions = ["s3:*"]
    # 誰からのリクエストであってもHTTPなら拒否する
    principals {
      type        = "*"
      identifiers = ["*"]
    }
    # バケット自体とバケット内の全オブジェクトが対象
    resources = [
      local.all_buckets[each.key].arn,
      "${local.all_buckets[each.key].arn}/*",
    ]
    # HTTPS以外のリクエストが対象
    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }
}

# HTTPS限定のポリシーをバケットに付与する
resource "aws_s3_bucket_policy" "media" {
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id
  policy   = data.aws_iam_policy_document.media_https_only[each.key].json

  # パブリックアクセスブロック適用前にポリシーを付与すると拒否される場合があるため順序を固定する
  depends_on = [aws_s3_bucket_public_access_block.media]
}

# 写真への不正アクセスや流出を後から追えるよう、全リクエストをログ用バケットへ記録する
resource "aws_s3_bucket_logging" "media" {
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

  # 出力先のログ用バケット
  target_bucket = aws_s3_bucket.logs.id
  # 本番と開発のログが混ざらないようバケットごとにプレフィックスを分ける
  target_prefix = "${each.key}/"

  # 配信許可のポリシーが無い状態で設定すると初回のログが捨てられるため順序を固定する
  depends_on = [aws_s3_bucket_policy.logs]
}

# ブラウザから直接アップロード・表示するためにフロントのオリジンを許可する
resource "aws_s3_bucket_cors_configuration" "media" {
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

  # CORS設定を追加して、ブラウザからの直接アクセスを許可する
  cors_rule {
    # 許可するヘッダーを設定
    allowed_headers = ["*"]
    # 許可するHTTPメソッドを設定
    allowed_methods = ["GET", "PUT", "POST"]
    # 許可するオリジンを設定
    allowed_origins = each.value.allowed_origins
    # アップロード完了の確認にETagを読むため公開する
    expose_headers = ["ETag"]
    # キャッシュの有効期限を設定
    max_age_seconds = 3000
  }
}

# ライフサイクルルールを設定
resource "aws_s3_bucket_lifecycle_configuration" "storage" {
  bucket = aws_s3_bucket.media["storage"].id

  # ゴミ箱のメディアは30日で完全に削除する
  rule {
    id     = "delete-trash-objects"
    status = "Enabled"
    # ゴミ箱内のオブジェクトが対象
    filter {
      prefix = "trash/"
    }
    # 30日経過したらオブジェクトを削除する
    expiration {
      days = 30
    }
    # 旧バージョンのオブジェクトも1日経過したら削除する
    noncurrent_version_expiration {
      noncurrent_days = 1
    }
  }

  # 中断されたマルチパートアップロードの断片は課金対象のまま見えないため掃除する
  rule {
    id     = "cleanup-incomplete-multipart"
    status = "Enabled"

    # 期限切れのオブジェクト削除マーカーの設定
    expiration {
      expired_object_delete_marker = false
    }

    # 中断されたマルチパートアップロードの断片を7日後に削除する
    abort_incomplete_multipart_upload {
      days_after_initiation = 7
    }
  }

  # サーバーのcronが保存するDBバックアップは保持期間の30日を過ぎたら削除する
  rule {
    id     = "delete-backups-after-30days"
    status = "Enabled"

    # バックアップ対象のオブジェクトが対象
    filter {
      prefix = "backups/postgres/"
    }
    # 30日経過したらオブジェクトを削除する
    expiration {
      days = 30
    }
    # バージョニング有効下では実体が旧バージョンとして残り続けるため削除する
    noncurrent_version_expiration {
      noncurrent_days = 1
    }
  }

  # アプリが削除したメディアは旧バージョンとして無期限に残り課金され続けるため、復元期間を過ぎたら削除する
  # DBバックアップの保持期間30日以内のダンプを戻しても参照先のメディアを復元できるよう同じ30日とする
  dynamic "rule" {
    # アプリが書き込む写真・動画、サムネイル、プロフィール画像が対象
    for_each = ["media/", "thumbnails/", "profile/"]
    content {
      id     = "expire-noncurrent-${trimsuffix(rule.value, "/")}"
      status = "Enabled"

      # 対象プレフィックス配下のオブジェクトが対象
      filter {
        prefix = rule.value
      }
      # 現行バージョンは消さず、旧バージョンが無くなった削除マーカーだけを削除する
      expiration {
        expired_object_delete_marker = true
      }
      # 旧バージョンになってから30日経過したら削除する
      noncurrent_version_expiration {
        noncurrent_days = 30
      }
    }
  }

  # 旧バージョンが消えた後に残る削除マーカーを掃除する
  rule {
    id     = "cleanup-backup-delete-markers"
    status = "Enabled"

    # バックアップ対象のオブジェクトが対象
    filter {
      prefix = "backups/postgres/"
    }
    # 旧バージョンが無くなった削除マーカーを削除する
    expiration {
      expired_object_delete_marker = true
    }
  }
}

# メディアバケットのアクセスログを保存するS3バケットの設定
# ログ配信元をこのアカウントのバケットに限定するためアカウントIDを参照する
data "aws_caller_identity" "current" {}

# ログ用バケットを作成
# 自身のログを取る警告をNOSONARで抑制
resource "aws_s3_bucket" "logs" { # NOSONAR
  bucket = "chii-piyo-logs-569946568026-ap-northeast-1-an"
  # アカウント専用の名前空間に作成
  bucket_namespace = "account-regional"
}

# HTTPS限定のポリシーにログ配信の許可を加える
data "aws_iam_policy_document" "logs" {
  # メディアバケットと共通のHTTPS限定の文を引き継ぐ
  source_policy_documents = [data.aws_iam_policy_document.media_https_only["logs"].json]

  # S3のログ配信サービスにメディアバケットのログの書き込みだけを許可する
  statement {
    sid     = "AllowS3ServerAccessLogs"
    effect  = "Allow"
    actions = ["s3:PutObject"]
    # S3のログ配信サービスが対象
    principals {
      type        = "Service"
      identifiers = ["logging.s3.amazonaws.com"]
    }
    # ログ用バケット内の全オブジェクトが対象
    resources = ["${aws_s3_bucket.logs.arn}/*"]
    # 他アカウントのバケットからログを書き込まれないよう配信元を自アカウントに限定する
    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [data.aws_caller_identity.current.account_id]
    }
    # 自アカウント内でもメディアバケット以外からの配信は受け付けない
    condition {
      test     = "ArnLike"
      variable = "aws:SourceArn"
      values   = [for b in aws_s3_bucket.media : b.arn]
    }
  }
}

# ログ配信の許可とHTTPS限定のポリシーをバケットに付与する
resource "aws_s3_bucket_policy" "logs" {
  bucket = aws_s3_bucket.logs.id
  policy = data.aws_iam_policy_document.logs.json

  # パブリックアクセスブロック適用前にポリシーを付与すると拒否される場合があるため順序を固定する
  depends_on = [aws_s3_bucket_public_access_block.media]
}

# 問題に気付くまでの猶予として90日分のログを残し、それ以降は削除して費用を抑える
resource "aws_s3_bucket_lifecycle_configuration" "logs" {
  bucket = aws_s3_bucket.logs.id

  # 90日経過したログを削除する
  rule {
    id     = "delete-logs-after-90days"
    status = "Enabled"
    # バケット内の全オブジェクトが対象
    filter {}
    # 90日経過したらオブジェクトを削除する
    expiration {
      days = 90
    }
  }
}
