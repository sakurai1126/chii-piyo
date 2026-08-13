import { expect, Page } from "@playwright/test";

// トップページの操作オブジェクトを作成
export const createTopPage = (page: Page) => {
  // 画面へアクセス
  const goto = async () => {
    await page.goto("/");
    await page.waitForLoadState("domcontentloaded");
  };

  // ホーム画面のURLおよびサマリー要素が表示されていることを検証
  const expectSummaryLoaded = async () => {
    await expect(page).toHaveURL("/");
    await expect(page.getByRole("heading", { name: "アルバム" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "最近の記録" })).toBeVisible();
  };

  return {
    goto,
    expectSummaryLoaded,
  };
};
