# Terraform本体と使用するプロバイダーのバージョン制約
terraform {
  # Terraform本体のバージョン制約 (1.17 以上 2.0 未満)
  required_version = "~> 1.17"

  required_providers {
    # AWSリソースを操作するためのプロバイダー (6.x 系の範囲内で更新を許可)
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  # state の保存先
  backend "s3" {
    bucket = "chii-piyo-tfstate-569946568026-ap-northeast-1-an"
    # state を分割しても同じバケットに並べられるようプロジェクト名の配下に配置
    key    = "chii-piyo/terraform.tfstate"
    region = "ap-northeast-1"
    # 同時実行による state の破損を防ぐため排他制御する
    use_lockfile = true
  }
}
