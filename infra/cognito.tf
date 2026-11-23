# 認証に使用するCognitoの設定

# 家族のアカウントを管理するユーザープール
# ユーザー自体は個人情報を state に持たせないため Terraform では管理せずコンソールで作成する
resource "aws_cognito_user_pool" "main" {
  name = "s-repo-user-pool"

  # 基本機能のみ使用するため Essentials プランとする
  user_pool_tier = "ESSENTIALS"
  # コンソールやCLIなど Terraform 以外の操作による誤削除も防ぐため AWS 側の削除保護を有効化する
  deletion_protection = "ACTIVE"

  # メールアドレスをユーザー名としてログインさせる
  username_attributes = ["email"]
  # 大文字小文字の違いでログインできない事態を避ける
  username_configuration {
    case_sensitive = false
  }

  # メールアドレス変更時などに検証コードを送れるようにする
  # 管理者が作成したユーザーには効かないため、パスワード再設定のメールを受け取れるよう、コンソールで作成時に検証済みとして手動で設定する
  # 利用者は少人数の想定で作成時に確認できるため、検証フローは作らず手動の運用としている
  auto_verified_attributes = ["email"]

  # 家族以外が登録できないよう、ユーザーは管理者のみが作成できるようにする
  admin_create_user_config {
    allow_admin_create_user_only = true
  }

  # 多要素認証は家族が使う手間を優先して無効にする
  mfa_configuration = "OFF"

  # ログインはパスワードのみで行う
  sign_in_policy {
    allowed_first_auth_factors = ["PASSWORD"]
  }

  # 推測されにくいよう長さと文字種を要求する
  password_policy {
    minimum_length                   = 12
    password_history_size            = 0
    require_lowercase                = true
    require_numbers                  = true
    require_symbols                  = true
    require_uppercase                = true
    temporary_password_validity_days = 7
  }

  # パスワードを忘れた時の再設定手段をメール優先で設定する
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
    recovery_mechanism {
      name     = "verified_phone_number"
      priority = 2
    }
  }

  # 送信量が少ないため SES は使わず Cognito 標準のメール送信を使う
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }

  # 検証メールはリンクではなくコードで送る
  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
  }

  # ログインに使うメールアドレスを必須属性とする
  # スキーマは変更するとプールの作り直しになるため既存の定義に合わせている
  schema {
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    name                     = "email"
    required                 = true
    string_attribute_constraints {
      max_length = "2048"
      min_length = "0"
    }
  }

  # 削除するとユーザーと DB の cognito_user_id の対応が復元できなくなるため Terraform からの削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# Next.js のサーバー側からログインするためのアプリクライアント
resource "aws_cognito_user_pool_client" "app" {
  name         = "chii-piyo-client"
  user_pool_id = aws_cognito_user_pool.main.id

  # Next.js はサーバー側でシークレットを保持し認証時に SECRET_HASH を送るため、シークレット付きで作成する
  generate_secret = true

  # ログイン画面からのメールとパスワード認証と、リフレッシュトークンによる再発行のみ許可する
  explicit_auth_flows = ["ALLOW_REFRESH_TOKEN_AUTH", "ALLOW_USER_PASSWORD_AUTH"]
  # ユーザーの有無を推測されないよう、存在しないユーザーでも同じエラーを返す
  prevent_user_existence_errors = "ENABLED"
  # ログアウト時にリフレッシュトークンを無効化できるようにする
  enable_token_revocation                       = true
  enable_propagate_additional_user_context_data = false
  # ログイン処理中のセッションの有効期限
  auth_session_validity = 3

  # トークンの有効期限
  # ID トークンは漏れた時の影響を抑えるため短くし、リフレッシュトークンで再発行する
  access_token_validity  = 60
  id_token_validity      = 60
  refresh_token_validity = 30
  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }

  # ホストされた UI 向けの OAuth 設定
  # ログインは Next.js のサーバー側から InitiateAuth で行い使っていないため、不要な入口を残さないよう無効化する
  allowed_oauth_flows_user_pool_client = false
  allowed_oauth_flows                  = []
  allowed_oauth_scopes                 = []
  callback_urls                        = []
  logout_urls                          = []
  supported_identity_providers         = []

  # 属性の読み書き権限は既定のままとする
  read_attributes  = []
  write_attributes = []

  # 作り直すとクライアントIDとシークレットが変わり全端末でログインできなくなるため削除を防ぐ
  # generate_secret はインポート時に AWS から読み戻されず差分となり作り直しが計画されるため、変更を無視する
  lifecycle {
    prevent_destroy = true
    ignore_changes  = [generate_secret]
  }
}
