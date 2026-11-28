# フロントエンドをホスティングするAmplifyの設定

# ビルド手順はリポジトリで変更できるよう amplify.yml を正とし、build_spec は Terraform で持たない
resource "aws_amplify_app" "frontend" {
  name       = "chii-piyo"
  repository = "https://github.com/sakurai1126/chii-piyo"
  # SSR を使うため静的配信用の WEB ではなく WEB_COMPUTE とする
  platform = "WEB_COMPUTE"
  # SSR の実行ログを CloudWatch Logs に出力するため、アプリ作成時にコンソールが作ったロールを使う
  iam_service_role_arn = "arn:aws:iam::569946568026:role/service-role/AmplifySSRLoggingRole-f2c651c5-46c1-4e4c-9b53-8b3ffa2b2f3e"

  environment_variables = {
    # amplify.yml の appRoot と同じく、モノレポ内の frontend 配下をアプリとしてビルドさせる
    AMPLIFY_MONOREPO_APP_ROOT = "frontend"
    # 変更のあったファイルだけをデプロイする機能はコンソールの既定どおり無効のままとする
    AMPLIFY_DIFF_DEPLOY = "false"

    # 以降は amplify.yml のビルドで .env.production へ書き出す値
    API_BASE_URL         = "https://${aws_route53_record.api.name}/api/v1"
    COGNITO_REGION       = data.aws_region.current.region
    COGNITO_USER_POOL_ID = aws_cognito_user_pool.main.id
    COGNITO_CLIENT_ID    = aws_cognito_user_pool_client.app.id
    # シークレットはアプリクライアントの state にすでに含まれているため、参照しても state に新しく増えることはない
    COGNITO_CLIENT_SECRET = aws_cognito_user_pool_client.app.client_secret
    COGNITO_ISSUER        = "https://${aws_cognito_user_pool.main.endpoint}"
    COGNITO_JWKS_URI      = "https://${aws_cognito_user_pool.main.endpoint}/.well-known/jwks.json"
    # ブラウザから直接アップロードと表示を行うため CSP で許可するオリジン
    S3_ORIGIN = "https://${aws_s3_bucket.media["storage"].bucket_regional_domain_name}"
  }

  # Amplify の既定ドメインへのアクセスを独自ドメインへ恒久的にリダイレクトする
  # 検索結果やブックマークに既定ドメインが残らないよう、middleware より手前の配信側でも転送する
  custom_rule {
    # 自分自身の属性は参照できないため、既定ドメインは値で指定する
    source = "https://main.dnakxm0y9trz3.amplifyapp.com"
    status = "301"
    target = "https://chii-piyo.s-repo.link"
  }

  # アプリ作成時にコンソールが設定した既定のルール
  # 取り込み時点の動作を変えないよう、そのまま残す
  custom_rule {
    source = "/<*>"
    status = "404-200"
    target = "/index.html"
  }

  # 作り直すと既定ドメインが変わり、middleware の独自ドメインへのリダイレクトが効かなくなるため削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# リージョン名を環境変数に渡すために参照する
data "aws_region" "current" {}

# 本番として配信する main ブランチ
resource "aws_amplify_branch" "main" {
  app_id      = aws_amplify_app.frontend.id
  branch_name = "main"
  # main へのマージをそのまま本番に反映するため、push を検知して自動ビルドする
  enable_auto_build = true
}

# 独自ドメインを main ブランチに割り当てる
# S3 の CORS と middleware のリダイレクトがこのドメインを前提にしているため Terraform で管理する
# 検証用の CNAME レコードは Amplify が Route 53 に自動で作成するため、route53.tf では定義しない
resource "aws_amplify_domain_association" "main" {
  app_id = aws_amplify_app.frontend.id
  # ホストゾーン全体ではなくサブドメインそのものを登録しているため、prefix を空にして直下に割り当てる
  domain_name = "chii-piyo.${aws_route53_zone.main.name}"
  # 証明書の検証は Amplify が非同期に行うため、apply が完了を待って止まらないようにする
  wait_for_verification = false

  sub_domain {
    branch_name = aws_amplify_branch.main.branch_name
    prefix      = ""
  }
}
