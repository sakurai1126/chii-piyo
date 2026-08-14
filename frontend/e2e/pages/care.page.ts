import { expect, Page } from "@playwright/test";

export const createCarePage = ({ page }: { page: Page }) => {
  // 育児記録カレンダー画面へアクセス
  const goto = async () => {
    await page.goto("/care");
    await page.waitForLoadState("networkidle");
  };

  const expectPageLoaded = async () => {
    await expect(page).toHaveURL("/care");
    await expect(page.getByRole("button", { name: "食事" })).toBeVisible();
  };

  // 食事記録を登録
  const recordMeal = async ({ note }: { note: string }) => {
    await page.getByRole("button", { name: "食事" }).click();
    await page.locator('input[type="text"]').fill(note);
    await page.getByRole("button", { name: "記録する" }).click();
    await expect(page.getByText("食事を記録しました")).toBeVisible();
  };

  // カレンダー上に記録が反映されていることを検証
  const expectMealRecordInCalendar = async ({ note }: { note: string }) => {
    // カレンダー内の最新の食事記録アイコンをクリック
    await page.locator("button.border-accent-pink").last().click();
    await expect(page.getByText(note)).toBeVisible();
  };

  // 404エラーが表示されていることを検証
  const expectNotFound = async () => {
    await expect(page.getByText("404")).toBeVisible();
  };

  return {
    goto,
    expectPageLoaded,
    recordMeal,
    expectMealRecordInCalendar,
    expectNotFound,
  };
};
