# ドメインの名前解決に使用するRoute 53の設定

# s-repo.link の名前解決を担うホストゾーン
resource "aws_route53_zone" "main" {
  name = "s-repo.link"

  # ドメイン購入時に Route 53 が設定した値に合わせる
  comment = "HostedZone created by Route53 Registrar"

  # 作り直すとネームサーバーが変わり、登録先を直して反映されるまで全サブドメインの名前解決が止まるため削除を防ぐ
  lifecycle {
    prevent_destroy = true
  }
}

# バックエンドAPIを Lightsail 上の Caddy に向けるレコード
# Caddy が Let's Encrypt で証明書を取得する際もこの名前で到達できる必要がある
resource "aws_route53_record" "api" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "chii-piyo-api.s-repo.link"
  type    = "A"
  ttl     = 300
  # IP の二重管理を避けるため Lightsail の静的 IP を参照する
  records = [aws_lightsail_static_ip.prod.ip_address]
}
