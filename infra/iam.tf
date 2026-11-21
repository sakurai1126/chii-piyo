# バックエンドがS3へアクセスするためのIAM設定

# S3へアクセスするためのIAMユーザーとポリシーを作成する
resource "aws_iam_user" "app" {
  name = "chii-piyo-s3-user"
}

# 作成したIAMユーザーにアタッチするポリシーを定義する
resource "aws_iam_policy" "app_s3" {
  name = "ChiiPiyoS3Policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "StorageFullAccess"
        Effect = "Allow"
        # アプリの読み書き・削除とDBバックアップの保存に必要な操作のみ許可する
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:ListBucket",
        ]
        # バケット自体と配下のオブジェクトが対象のため両方を取得しつつ1つの配列にまとめる
        Resource = flatten([
          for b in aws_s3_bucket.media : [b.arn, "${b.arn}/*"]
        ])
      }
    ]
  })
}

# 作成したIAMユーザーにポリシーをアタッチする
resource "aws_iam_user_policy_attachment" "app_s3" {
  user       = aws_iam_user.app.name
  policy_arn = aws_iam_policy.app_s3.arn
}
