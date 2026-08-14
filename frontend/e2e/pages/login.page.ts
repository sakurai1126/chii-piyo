import { Page, expect } from "@playwright/test";

export const createLoginPage = ({ page }: { page: Page }) => {
  // 画面へアクセス
  const goto = async () => {
    await page.goto("/login");
    await page.waitForLoadState("networkidle");
  };

  // ログイン情報の入力と送信
  const login = async ({ email, password }: { email: string; password: string }) => {
    await page.getByLabel("メールアドレス").fill(email);
    await page.getByLabel("パスワード").fill(password);
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
