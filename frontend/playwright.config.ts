import path from "node:path";

import { defineConfig, devices } from "@playwright/test";

// .env.localから環境変数を読み込み
process.loadEnvFile(path.resolve(__dirname, ".env.local"));

// 認証情報を保存するファイル
const storageState = "frontend/e2e/.auth/user.json";

export default defineConfig({
  // テストファイルの配置ディレクトリ
  testDir: "./e2e",

  // DBのデータ競合や同一ユーザーでのセッション競合を防ぐため直列化
  fullyParallel: false,

  forbidOnly: !!process.env.CI,

  // リトライ回数 - CI環境では不安定な可能性を考慮し2回まで許容
  retries: process.env.CI ? 2 : 0,

  // 並列実行数 - DB競合等を考慮し1で固定
  workers: 1,

  // レポート形式
  reporter: "html",

  use: {
    baseURL: process.env.PLAYWRIGHT_TEST_BASE_URL || "http://localhost:3000",
    trace: "on-first-retry",
    screenshot: "only-on-failure",
  },

  // テストを実行するプロジェクトの設定
  projects: [
    // まず認証セットアップを実行
    {
      name: "setup",
      testMatch: "**/*.setup.ts",
    },

    // セットアップ完了後に各ブラウザでテストを実行
    {
      name: "chromium",
      testMatch: "**/*.spec.ts",
      use: {
        ...devices["Desktop Chrome"],
        storageState,
      },
      // auth.setup.ts で作成した認証情報を利用
      dependencies: ["setup"],
    },
    {
      name: "firefox",
      testMatch: "**/*.spec.ts",
      use: {
        ...devices["Desktop Firefox"],
        storageState,
      },
      dependencies: ["setup"],
    },
    {
      name: "webkit",
      testMatch: "**/*.spec.ts",
      use: {
        ...devices["Desktop Safari"],
        storageState,
      },
      dependencies: ["setup"],
    },
  ],

  webServer: {
    // CIではプロダクションビルド、ローカルでは開発サーバーを利用
    command: process.env.CI ? "npm run build && npm run start" : "npm run dev",

    // サーバーの起動完了を検証するためのヘルスチェック先URL
    url: "http://localhost:3000",

    // ローカル開発で既に localhost:3000 で npm run dev が動いている場合はそれを再利用する
    reuseExistingServer: !process.env.CI,

    // App Routerのビルド/起動時間を考慮し長めに設定
    timeout: 120 * 1000,
  },
});
