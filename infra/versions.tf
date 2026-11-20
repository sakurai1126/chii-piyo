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
}
