import { expect, Page } from "@playwright/test";

export const createUploadPage = ({ page }: { page: Page }) => {
  // 画面へアクセス
  const goto = async () => {
    await page.goto("/upload");
    await page.waitForLoadState("networkidle");
  };

  // 写真ファイルをinput要素にセット
  const inputPhoto = async () => {
    await page.locator('input[type="file"][accept="image/*"]').setInputFiles({
      name: "image.png",
      mimeType: "image/png",
      // 1x1のテスト用pngバッファ
      buffer: Buffer.from(
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
        "base64",
      ),
    });
  };

  // 共有範囲を選択
  const selectSharingGroup = async ({ groupName }: { groupName: string }) => {
    await page.locator('button[aria-controls^="accordion-"]').click();
    await page
      .locator("label")
      .filter({ hasText: groupName })
      .locator('input[type="radio"]')
      .check();
  };

  const upload = async () => {
    // アップロードボタンをクリック
    await page.getByRole("button", { name: "アップロード" }).click();

    // 完了通知の表示を待機検証
    await expect(page.getByText("アップロードが完了しました")).toBeVisible({ timeout: 15000 });
  };

  return {
    goto,
    inputPhoto,
    selectSharingGroup,
    upload,
  };
};
