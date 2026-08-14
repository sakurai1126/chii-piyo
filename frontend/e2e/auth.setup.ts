import { test as setup } from "@playwright/test";

import { createLoginPage } from "./pages/login.page";
import { createTopPage } from "./pages/top.page";

const authFile = "frontend/e2e/.auth/user.json";
const viewerAuthFile = "frontend/e2e/.auth/viewer.json";

setup("authenticate", async ({ page }) => {
  const email = process.env.TEST_USER_EMAIL;
  const password = process.env.TEST_USER_PASSWORD;

  if (!email || !password) {
    throw new Error("環境変数に認証情報が設定されていません。");
  }

  // ページ操作関数の初期化
  const loginPage = createLoginPage({ page });
  const topPage = createTopPage({ page });

  // ログイン画面にアクセスしてログイン実行
  await loginPage.goto();
  await loginPage.login({ email, password });

  // ホーム画面のURLおよびサマリー要素が表示されていることを検証
  await topPage.expectSummaryLoaded();

  // 認証状態を保存
  await page.context().storageState({ path: authFile });
});

// 閲覧者権限のユーザー情報の保存
setup("authenticate viewer", async ({ page }) => {
  const email = process.env.TEST_VIEWER_EMAIL;
  const password = process.env.TEST_VIEWER_PASSWORD;

  if (!email || !password) {
    throw new Error("環境変数に閲覧者認証情報が設定されていません。");
  }

  // ページ操作関数の初期化
  const loginPage = createLoginPage({ page });
  const topPage = createTopPage({ page });

  // ログイン画面にアクセスしてログイン実行
  await loginPage.goto();
  await loginPage.login({ email, password });

  // ホーム画面のURLおよびサマリー要素が表示されていることを検証
  await topPage.expectSummaryLoaded();

  // 認証状態を保存
  await page.context().storageState({ path: viewerAuthFile });
});
