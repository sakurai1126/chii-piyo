import path from "path";

import { defineConfig, devices } from "@playwright/test";

// .env.local から環境変数をテスト実行プロセスへロード
// Node.js 標準組み込み機能で .env.local をロード
process.loadEnvFile(path.resolve(__dirname, ".env.local"));

const storageState = "frontend/e2e/.auth/user.json";

export default defineConfig({
  testDir: "./e2e",
  // DBのデータ競合や同一ユーザーでのセッション競合を防ぐため直列化
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: "html",

  use: {
    baseURL: process.env.PLAYWRIGHT_TEST_BASE_URL || "http://localhost:3000",
    trace: "on-first-retry",
    screenshot: "only-on-failure",
  },

  projects: [
    // 1. まず認証セットアップを実行
    {
      name: "setup",
      testMatch: /.*\.setup\.ts/,
    },
    // 2. セットアップ完了後に各ブラウザでテストを実行
    {
      name: "chromium",
      use: {
        ...devices["Desktop Chrome"],
        storageState: storageState,
      },
      dependencies: ["setup"],
    },
  ],

  webServer: {
    // CIではプロダクションビルド、ローカルでは開発サーバーを利用
    command: process.env.CI ? "npm run build && npm run start" : "npm run dev",
    url: "http://localhost:3000",
    reuseExistingServer: !process.env.CI,
    timeout: 120 * 1000, // App Routerのビルド/起動時間を考慮し長めに設定
  },
});
