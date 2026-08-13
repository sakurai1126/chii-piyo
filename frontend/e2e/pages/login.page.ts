import { Page, expect } from "@playwright/test";

// ログインページの操作オブジェクトを作成
export const createLoginPage = (page: Page) => {
  // 画面へアクセス
  const goto = async () => {
    await page.goto("/login");
    await page.waitForLoadState("domcontentloaded");
  };

  // ログイン情報の入力と送信
  const login = async (email: string, pass: string) => {
    await page.getByLabel("メールアドレス").fill(email);
    await page.getByLabel("パスワード").fill(pass);
    await page.getByRole("button", { name: "ログイン" }).click();
  };

  // ログインページにいることを検証
  const expectLoginPageLanding = async () => {
    await expect(page).toHaveURL("/login");
  };

  // エラーメッセージが表示されていることを検証
  const expectLoginErrorMessage = async () => {
    await expect(page.getByText("メールアドレスまたはパスワードが正しくありません")).toBeVisible();
  };

  return {
    goto,
    login,
    expectLoginPageLanding,
    expectLoginErrorMessage,
  };
};
