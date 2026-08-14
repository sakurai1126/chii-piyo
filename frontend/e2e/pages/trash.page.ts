import { expect, Page } from "@playwright/test";

export const createTrashPage = ({ page }: { page: Page }) => {
  // 一覧画面へアクセス
  const goto = async () => {
    await page.goto("/trash");
    await page.waitForLoadState("networkidle");
  };

  const expectPageLoaded = async () => {
    await expect(page).toHaveURL("/trash");
    await expect(page.getByRole("heading", { name: "ゴミ箱" })).toBeVisible();
  };

  // ゴミ箱内のメディア数を取得
  const getTrashItemCount = async () => {
    return await page.locator('input[type="checkbox"][id^="trashItem-"]').count();
  };

  // ゴミ箱内のメディア数の検証
  const expectTrashItemCount = async (expectedCount: number) => {
    await expect(page.locator('input[type="checkbox"][id^="trashItem-"]')).toHaveCount(
      expectedCount,
    );
  };

  // メディアの復元
  const restoreMedia = async () => {
    // メディアを選択して復元ボタンをクリック
    await page.locator('input[type="checkbox"][id^="trashItem-"]').first().check();
    await page.getByRole("button", { name: "選択したメディアを復元" }).click();

    // lastでモーダル内の復元するボタンを特定してクリック
    await page.getByRole("button", { name: "復元する" }).last().click();

    await expect(page.getByText("メディアを復元しました。")).toBeVisible();
  };

  // メディアの完全削除
  const deleteMedia = async () => {
    // メディアを選択して削除ボタンをクリック
    await page.locator('input[type="checkbox"][id^="trashItem-"]').first().check();
    await page.getByRole("button", { name: "選択したメディアを完全に削除" }).click();

    // lastでモーダル内の削除ボタンを特定してクリック
    await page.getByRole("button", { name: "完全に削除する" }).last().click();

    await expect(page.getByText("メディアを完全に削除しました。")).toBeVisible();
  };

  // 404エラーが表示されていることを検証
  const expectNotFound = async () => {
    await expect(page.getByText("404")).toBeVisible();
  };

  return {
    goto,
    expectPageLoaded,
    getTrashItemCount,
    expectTrashItemCount,
    restoreMedia,
    deleteMedia,
    expectNotFound,
  };
};
