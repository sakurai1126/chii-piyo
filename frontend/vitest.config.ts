import path from "node:path";

import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig } from "vitest/config";

// Vitestの設定ファイル
export default defineConfig({
  plugins: [
    // Reactの読み込み設定
    react(),
    // @/ などのエイリアスインポートを有効化
    tsconfigPaths(),
  ],
  resolve: {
    alias: {
      "server-only": path.resolve(__dirname, "./test/mocks/server-only.ts"),
    },
  },
  test: {
    // Reactコンポーネントのテストを行うための実行環境
    environment: "jsdom",
    // 各テスト開始前にモック履歴をクリア
    clearMocks: true,
    // セットアップファイルの指定
    setupFiles: ["./vitest.setup.ts"],
    // vitestのテスト対象外のファイル
    exclude: ["**/node_modules/**", "**/dist/**", "e2e/**"],
    // カバレッジ設定
    coverage: {
      // カバレッジ計測エンジン
      provider: "v8",
      // カバレッジ取得フォーマットの指定
      // "text"はターミナルに出力、"html"は目視用にHTMLファイルとして出力、"lcov"はCIツール等で読み込める形式で出力
      reporter: ["text", "html", "lcov"],
      // カバレッジ計測対象ファイル
      include: ["src/**/*.{ts,tsx}"],
      // カバレッジ対象外のファイル
      exclude: [
        // 自動生成・型定義・設定
        "src/lib/api-client/gen/**",
        "**/*.d.ts",
        "*.config.*",
        "**/types/**",
        // エントリポイントファイル
        "**/index.ts",
        "**/server.ts",
        "**/actions.ts",
        // UIコンポーネント・スタイル・ページ
        "src/app/**",
        "src/components/**",
        "src/features/**/components/**",
        "src/styles/**",
        // API通信ラッパー・Server Actions
        "src/features/**/api/**",
        "src/features/**/actions/**",
        // インフラ・認証基盤・Proxy
        "src/proxy.ts",
        "src/lib/auth/**",
        // テストファイル自身・モック
        "**/*.test.{ts,tsx}",
        "src/test/**",
        // UIスタイル関連
        "src/hooks/**",
        // サーバー側API呼び出し関数
        "src/features/auth/utils/**",
        // React Queryのフェッチフック
        "src/**/hooks/useGet*.ts",
        "src/**/hooks/use*List.ts",
        // 通信・UI補助ユーティリティ
        "src/utils/fetcher.ts",
        "src/utils/api.ts",
        "src/utils/cn.ts",
        // 画面固有の合成フック・設定画面フック
        "src/features/settings/**",
        "src/features/upload/hooks/useMultipleSettings.ts",
        "src/features/upload/hooks/useUploadPage.ts",
        "src/features/care/hooks/useCareRecord.ts",
        // 補助ユーティリティ
        "src/utils/getTheme.ts",
        "src/utils/action.ts",
      ],
    },
  },
});
