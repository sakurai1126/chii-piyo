# AWSプロバイダーの設定
provider "aws" {
  region = "ap-northeast-1"

  # このプロバイダーで作成する全リソースに自動で付与するタグ
  default_tags {
    tags = {
      # コンソールで手動作成したリソースと区別するためのタグ
      ManagedBy = "terraform"
    }
  }
}
