import type { NextConfig } from "next";

const securityHeaders = [
  // HTTPS接続を強制
  {
    key: "Strict-Transport-Security",
    value: "max-age=31536000; includeSubDomains",
  },
  // MIMEスニッフィング対策
  // アップロードしたファイルの中に悪意あるスクリプトが含まれていた場合にブラウザがそれを実行してしまうことを防止
  {
    key: "X-Content-Type-Options",
    value: "nosniff",
  },
  // クリックジャッキング攻撃対策
  // 他のサイトのiframeやframeの中にこのサイトの画面を埋め込むことを禁止して、クリックジャッキング攻撃を防止する
  {
    key: "X-Frame-Options",
    value: "DENY",
  },
  // 外部サイトへの遷移時にリファラ情報の送信を制御する
  {
    key: "Referrer-Policy",
    value: "strict-origin-when-cross-origin",
  },
  // ブラウザの機能へのアクセスを制御する
  {
    key: "Permissions-Policy",
    value: "camera=(self), microphone=(), geolocation=(), payment=(), usb=()",
  },
  // クロスオリジンのリソースとの分離を強化する
  {
    key: "Cross-Origin-Opener-Policy",
    value: "same-origin",
  },
];

const nextConfig: NextConfig = {
  // リポジトリルートにてnode_moduleを使用しているため Turbopack のワークスペースルートは現在のディレクトリであることを明示
  turbopack: {
    root: __dirname,
  },
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "*.s3.ap-northeast-1.amazonaws.com",
      },
    ],
  },

  // セキュリティヘッダー設定
  async headers() {
    return [
      {
        source: "/:path*",
        headers: securityHeaders,
      },
    ];
  },
};

export default nextConfig;
