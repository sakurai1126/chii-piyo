import { test, expect } from "@playwright/test";

test.describe("認証済みのテスト", () => {
  test("認証状態でトップページにアクセスしコンテンツが表示されること", async ({ page }) => {
    await page.goto("/");

    // 画面タイトルやログアウト状態でないことを検証
    await expect(page).toHaveURL("/");
    await expect(page.getByRole("heading", { name: "アルバム" })).toBeVisible();
  });
});
