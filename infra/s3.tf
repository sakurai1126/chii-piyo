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
}

# S3バケットを作成
resource "aws_s3_bucket" "media" {
  for_each = local.media_buckets
  bucket   = each.value.name

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
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

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
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

  # バケットの所有権を強制し、ACLによるアクセス制御を無効化する
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

# メディアは署名付きURLでのみ配信するため公開設定を全て拒否する（AWS規定値）
resource "aws_s3_bucket_public_access_block" "media" {
  for_each = local.media_buckets
  bucket   = aws_s3_bucket.media[each.key].id

  # 公開アクセスを全てブロック
  block_public_acls       = true
  ignore_public_acls      = true
  block_public_policy     = true
  restrict_public_buckets = true
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
  }
}
