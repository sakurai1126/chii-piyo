import { test } from "@playwright/test";

import { createLoginPage } from "./pages/login.page";
import { createTopPage } from "./pages/top.page";

// 認証保存セッションを使わず、未ログイン状態でテストを開始する設定
test.use({ storageState: { cookies: [], origins: [] } });

test.describe("認証シナリオ", () => {
  test("E2E-01: 正しい認証情報でログインしホームのサマリーが表示されること", async ({ page }) => {
    const email = process.env.TEST_USER_EMAIL;
    const password = process.env.TEST_USER_PASSWORD;

    if (!email || !password) {
      throw new Error("環境変数 TEST_USER_EMAIL または TEST_USER_PASSWORD が設定されていません。");
    }

    // ページ操作関数の初期化
    const loginPage = createLoginPage(page);
    const topPage = createTopPage(page);

    // ログイン画面にアクセスしてログイン実行
    await loginPage.goto();
    await loginPage.login(email, password);

    // ホーム画面へ遷移しサマリーが表示されたことを検証
    await topPage.expectSummaryLoaded();
  });

  test("E2E-02: 認証なしで保護ページへ遷移するとログイン画面へリダイレクトされること", async ({
    page,
  }) => {
    // ページ操作関数の初期化
    const loginPage = createLoginPage(page);
    const topPage = createTopPage(page);

    // トップページにアクセス
    await topPage.goto();

    // ログインページにいることを検証
    await loginPage.expectLoginPageLanding();
  });

  test("E2E-03: 不正な認証情報ではログインできずエラーが表示されること", async ({ page }) => {
    const email = "test@example.com";
    const password = "test";

    // ページ操作関数の初期化
    const loginPage = createLoginPage(page);

    // ログイン画面にアクセスしてログイン実行
    await loginPage.goto();
    await loginPage.login(email, password);

    // ログインページにいることを検証
    await loginPage.expectLoginPageLanding();

    // エラーメッセージが表示されていることを検証
    await loginPage.expectLoginErrorMessage();
  });
});
