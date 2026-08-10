import { test as setup, expect } from "@playwright/test";

const authFile = "frontend/e2e/.auth/user.json";

setup("authenticate", async ({ page }) => {
  const email = process.env.TEST_USER_EMAIL;
  const password = process.env.TEST_USER_PASSWORD;

  if (!email || !password) {
    throw new Error("環境変数に認証情報が設定されていません。");
  }

  await page.goto("/login");

  // フォーム入力とログイン実行
  await page.getByLabel("メールアドレス").fill(email);
  await page.getByLabel("パスワード").fill(password);
  await page.getByRole("button", { name: "ログイン" }).click();

  // トップページへの遷移を待機
  await page.waitForURL("/");

  // ログイン後の要素確認
  await expect(page.getByRole("heading", { name: "アルバム" })).toBeVisible();

  // 認証状態を保存
  await page.context().storageState({ path: authFile });
});
