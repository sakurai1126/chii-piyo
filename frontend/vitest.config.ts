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
        // OpenAPI自動生成コード
        "src/lib/api-client/gen/**",
        // 型定義ファイル
        "**/*.d.ts",
        // ビルドツール等の設定ファイル
        "*.config.*",
      ],
    },
  },
});
