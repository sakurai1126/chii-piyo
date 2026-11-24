# バックエンドを稼働させるLightsailの設定

# Spring Boot と PostgreSQL を Docker Compose で動かす本番サーバー
resource "aws_lightsail_instance" "prod" {
  name              = "chii-piyo-prod"
  availability_zone = "ap-northeast-1a"
  # OSとプラン指定 - プランは小規模インスタンスを使用
  blueprint_id = "ubuntu_24_04"
  bundle_id    = "small_3_0"
  # キーペアは秘密鍵を state に持たせないため Terraform では管理せず、既存のものを名前で指定する
  key_pair_name = "chii-piyo-prod-key"
  # IPv4 のみを使用する - 利用想定として IPv6 は不要
  ip_address_type = "ipv4"

  # DB を同じインスタンスで動かしており、障害時にサーバーごと戻せるよう毎日スナップショットを取る
  add_on {
    type = "AutoSnapshot"
    # 利用の少ない日本時間の深夜2時に取得する
    snapshot_time = "17:00" # UTC で指定するため日本時間の深夜2時は UTC 17:00 に相当
    status        = "Enabled"
  }

  # 削除すると DB のデータと Caddy が取得した証明書が失われるため Terraform からの削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# インスタンスを作り直しても API の IP が変わらないよう静的 IP を使う
resource "aws_lightsail_static_ip" "prod" {
  name = "chii-piyo-prod-ip"

  # 解放すると同じ IP は二度と取得できず DNS の更新が反映されるまで API に到達できなくなるため削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# 静的 IP をインスタンスに紐付ける
resource "aws_lightsail_static_ip_attachment" "prod" {
  static_ip_name = aws_lightsail_static_ip.prod.name
  instance_name  = aws_lightsail_instance.prod.name
}

# インスタンスのファイアウォールで開放するポート
resource "aws_lightsail_instance_public_ports" "prod" {
  instance_name = aws_lightsail_instance.prod.name

  # Caddy が Let's Encrypt の HTTP チャレンジに応答し、HTTP を HTTPS へリダイレクトするために開放する
  port_info {
    protocol  = "tcp"
    from_port = 80
    to_port   = 80
    cidrs     = ["0.0.0.0/0"]
  }

  # API を Caddy 経由の HTTPS で公開する
  port_info {
    protocol  = "tcp"
    from_port = 443
    to_port   = 443
    cidrs     = ["0.0.0.0/0"]
  }

  # GitHub Actions のデプロイは接続元 IP が固定されないため、送信元を絞らず鍵認証のみで守る
  port_info {
    protocol  = "tcp"
    from_port = 22
    to_port   = 22
    cidrs     = ["0.0.0.0/0"]
  }
}
