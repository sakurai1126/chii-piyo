import { expect, Page } from "@playwright/test";

export const createMediaPage = ({ page }: { page: Page }) => {
  // 一覧画面へアクセス
  const goto = async () => {
    await page.goto("/media");
    // 画像読み込み等でネットワークリクエストが落ち着くまで待機
    await page.waitForLoadState("networkidle");
  };

  // 一覧画面が表示されていることを検証
  const expectLoaded = async () => {
    await expect(page).toHaveURL("/media");
    await expect(page.getByRole("heading", { name: "写真・動画一覧" })).toBeVisible();
  };

  // 現在表示されているメディアの件数を取得
  const getMediaCount = async () => {
    return await page.locator('a[href^="/media/"]:has(img)').count();
  };

  // メディア数の検証
  const expectMediaCount = async (expectedCount: number) => {
    await expect(page.locator('a[href^="/media/"]:has(img)')).toHaveCount(expectedCount);
  };

  // メディア削除
  const deleteMedia = async () => {
    await page.getByRole("button", { name: "選択を開始" }).click();
    await page.getByRole("checkbox", { name: "選択" }).first().check();
    await page.getByRole("button", { name: "選択したメディアをすべてゴミ箱に移動する" }).click();
    await page.getByRole("button", { name: "実行する" }).click();
    await expect(page.getByText("メディアをゴミ箱に移動しました")).toBeVisible();
  };

  return {
    goto,
    expectLoaded,
    getMediaCount,
    expectMediaCount,
    deleteMedia,
  };
};
