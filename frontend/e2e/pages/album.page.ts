import { expect, Page } from "@playwright/test";

export const createAlbumPage = ({ page }: { page: Page }) => {
  // 一覧画面へアクセス
  const goto = async () => {
    await page.goto("/albums");
    await page.waitForLoadState("networkidle");
  };

  // アルバム作成
  const createAlbum = async ({ title }: { title: string }) => {
    await page.getByRole("button", { name: "アルバムを新規作成" }).click();
    // モーダル内のinputを指定し入力
    await page
      .locator("div")
      .filter({ has: page.getByText("アルバム名", { exact: true }) })
      .getByRole("textbox")
      .fill(title);
    await page.getByRole("button", { name: "保存する" }).click();
    await expect(page.getByText("アルバムを作成しました")).toBeVisible();
  };

  // 作成したアルバムの詳細ページを開く
  const openAlbum = async ({ title }: { title: string }) => {
    await page.getByRole("link", { name: title }).click();
    await page.waitForLoadState("networkidle");
  };

  // アルバムにメディアを追加
  const addMedia = async () => {
    await page.getByRole("button", { name: "アルバムにメディアを追加" }).click();

    // モーダル内の1件目の写真をチェック
    await page.locator('input[type="checkbox"][name$="-check"]').first().check();

    await page.getByRole("button", { name: "追加する" }).click();
    await page.getByRole("button", { name: "実行する" }).click();
    await expect(page.getByText("メディアを追加しました")).toBeVisible();
  };

  // メディア数の検証
  const expectMediaCount = async (expectedCount: number) => {
    await expect(page.locator('a[href^="/media/"]:has(img)')).toHaveCount(expectedCount);
  };

  // アルバム作成ボタンの表示を検証
  const expectCreateButtonVisible = async () => {
    await expect(page.getByRole("button", { name: "アルバムを新規作成" })).toBeVisible();
  };

  // アルバム作成ボタンの非表示を検証
  const expectCreateButtonNotVisible = async () => {
    await expect(page.getByRole("button", { name: "アルバムを新規作成" })).not.toBeVisible();
  };

  return {
    goto,
    createAlbum,
    openAlbum,
    addMedia,
    expectMediaCount,
    expectCreateButtonVisible,
    expectCreateButtonNotVisible,
  };
};
