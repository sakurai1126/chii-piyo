import { expect, Page } from "@playwright/test";

export const createSettingsPage = ({ page }: { page: Page }) => {
  // 設定画面へアクセス
  const goto = async () => {
    await page.goto("/settings");
    await page.waitForLoadState("networkidle");
  };

  // 共有グループの新規作成
  const createSharingGroup = async ({ groupName }: { groupName: string }) => {
    await page.locator("#sharing-groups").getByRole("button", { name: "新規追加" }).click();
    await page.locator('p:text("共有グループの名前") + input').fill(groupName);
    // ログイン中のユーザーを共有範囲に追加
    await page
      .locator('label[for^="new-"]')
      .filter({ hasText: process.env.TEST_USER_NAME || "" })
      .locator('input[type="checkbox"]')
      .check();
    await page.getByRole("button", { name: "共有範囲グループの新規保存" }).click();
    await expect(page.getByText("共有グループの作成に成功しました")).toBeVisible();
  };

  // 管理者専用セクションが表示されていることを検証
  const expectAdminSectionsVisible = async () => {
    await expect(page.getByText("共有範囲の設定")).toBeVisible();
    await expect(page.getByText("タグの設定")).toBeVisible();
    await expect(page.getByRole("heading", { name: "設定" })).toBeVisible();
  };

  // 閲覧者時に管理者セクションが非表示であることを検証
  const expectAdminSectionsHidden = async () => {
    await expect(page.getByText("共有範囲の設定")).not.toBeVisible();
    await expect(page.getByText("タグの設定")).not.toBeVisible();
    await expect(page.getByRole("heading", { name: "設定" })).toBeVisible();
  };

  // かんたんモードの切り替え
  const toggleEasyMode = async () => {
    await page.getByLabel("かんたんモードの切り替え").click();
  };

  // かんたんモードが切り替わったことの検証
  const expectToggledEasyMode = async (targetState: "ON" | "OFF") => {
    await expect(page.getByText(`かんたんモードを${targetState}にしました`)).toBeVisible();
  };

  // かんたんモード時のUI簡素化を検証
  const expectEasyModeApplied = async () => {
    // サイドバーが非表示になっていることを検証
    await expect(page.locator("aside")).not.toBeVisible();

    // 写真一覧や動画一覧のナビゲーションが表示されていることを検証
    await expect(page.locator('nav a[href^="/media?mediaKind=PHOTO"]')).toBeVisible();
    await expect(page.locator('nav a[href^="/media?mediaKind=VIDEO"]')).toBeVisible();
  };

  return {
    goto,
    createSharingGroup,
    expectAdminSectionsVisible,
    expectAdminSectionsHidden,
    toggleEasyMode,
    expectToggledEasyMode,
    expectEasyModeApplied,
  };
};
