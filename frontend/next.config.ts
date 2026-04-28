import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // リポジトリルートにてnode_moduleを使用しているため Turbopack のワークスペースルートは現在のディレクトリであることを明示
  turbopack: {
    root: __dirname,
  },
};

export default nextConfig;
