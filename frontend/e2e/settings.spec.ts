import { test } from "@playwright/test";

import { createAlbumPage } from "./pages/album.page";
import { createCarePage } from "./pages/care.page";
import { createMediaPage } from "./pages/media.page";
import { createSettingsPage } from "./pages/settings.page";
import { createTrashPage } from "./pages/trash.page";
import { createUploadPage } from "./pages/upload.page";

test.describe("設定関連シナリオ", () => {
  test("E2E-09: 設定した共有範囲に応じてメディアの表示が制御されること", async ({ page }) => {
    // ページ操作関数の初期化
    const settingsPage = createSettingsPage({ page });
    const uploadPage = createUploadPage({ page });
    const mediaPage = createMediaPage({ page });

    // 衝突しない共有グループ名を生成
    const groupName = `グループ_${Date.now()}`;

    // 設定画面に移動
    await settingsPage.goto();

    // 共有範囲を新規作成
    await settingsPage.createSharingGroup({ groupName });

    // アップロード画面へ移動し、追加した共有範囲選択をして写真をアップロード
    await uploadPage.goto();
    await uploadPage.inputPhoto();
    await uploadPage.selectSharingGroup({ groupName });
    await uploadPage.upload();

    // メディア一覧画面へ移動
    await mediaPage.goto();

    // 作成した共有グループで絞り込みを実行
    await mediaPage.filterBySharingGroup({ groupName });

    // 設定した共有範囲のメディアのみが表示されていることを検証
    await mediaPage.expectMediaCount(1);

    // 全員に公開フィルターに切り替えると、グループ限定のメディアが表示されないことを検証
    await mediaPage.filterBySharingGroup({ groupName: "全員に公開" });
    await mediaPage.expectMediaCount(0);
  });

  test("E2E-10: ロールに応じて操作可能な機能が出し分けられること", async ({ page, browser }) => {
    // ページ操作関数の初期化
    const settingsPage = createSettingsPage({ page });
    const albumPage = createAlbumPage({ page });
    const carePage = createCarePage({ page });
    const trashPage = createTrashPage({ page });

    // 設定画面に移動
    await settingsPage.goto();

    // 管理者専用セクションが表示されていることを検証
    await settingsPage.expectAdminSectionsVisible();

    // アルバム画面で新規作成ボタンが表示されていることを検証
    await albumPage.goto();
    await albumPage.expectCreateButtonVisible();

    // 育児記録画面へアクセスでき、404にならないことを検証
    await carePage.goto();
    await carePage.expectPageLoaded();

    // ゴミ箱画面へアクセスでき、404にならないことを検証
    await trashPage.goto();
    await trashPage.expectPageLoaded();

    // 閲覧者用の認証状態を読み込んだブラウザコンテキストを作成
    const viewerContext = await browser.newContext({
      storageState: "frontend/e2e/.auth/viewer.json",
    });
    const viewerPage = await viewerContext.newPage();

    // 閲覧者用のページ操作関数を初期化
    const viewerSettingsPage = createSettingsPage({ page: viewerPage });
    const viewerAlbumPage = createAlbumPage({ page: viewerPage });
    const viewerCarePage = createCarePage({ page: viewerPage });
    const viewerTrashPage = createTrashPage({ page: viewerPage });

    try {
      // 設定画面に移動
      await viewerSettingsPage.goto();

      // 設定画面で管理者専用セクションが非表示であることを検証
      await viewerSettingsPage.expectAdminSectionsHidden();

      // アルバム画面で新規作成ボタンが表示されていないことを検証
      await viewerAlbumPage.goto();
      await viewerAlbumPage.expectCreateButtonNotVisible();

      // 育児記録へのアクセスが制限され404になることを検証
      await viewerCarePage.goto();
      await viewerCarePage.expectNotFound();

      // ゴミ箱へのアクセスが制限され404になることを検証
      await viewerTrashPage.goto();
      await viewerTrashPage.expectNotFound();
    } finally {
      await viewerContext.close();
    }
  });

  test("E2E-11: かんたんモードのユーザーには簡素化されたUIが表示されること", async ({ page }) => {
    // ページ操作関数の初期化
    const settingsPage = createSettingsPage({ page });
    const carePage = createCarePage({ page });
    const mediaPage = createMediaPage({ page });

    // 設定画面へ移動
    await settingsPage.goto();

    // かんたんモードをONに切り替え
    await settingsPage.toggleEasyMode();
    await settingsPage.expectToggledEasyMode("ON");

    // 設定画面をリロードしてUIが簡素化されていることを確認
    await settingsPage.goto();
    await settingsPage.expectEasyModeApplied();

    // メディア一覧へ移動しUIが簡素化されていることを確認
    await mediaPage.goto();
    await mediaPage.expectEasyModeApplied();

    // かんたんモードでは育児記録へのアクセスが制限されていることを検証
    await carePage.goto();
    await carePage.expectNotFound();

    // クリーンアップ、かんたんモードをOFFに戻す
    await settingsPage.goto();
    await settingsPage.toggleEasyMode();
    await settingsPage.expectToggledEasyMode("OFF");
  });
});
