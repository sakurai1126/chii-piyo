import { test } from "@playwright/test";

import { createAlbumPage } from "./pages/album.page";
import { createMediaPage } from "./pages/media.page";
import { createTrashPage } from "./pages/trash.page";
import { createUploadPage } from "./pages/upload.page";

test.describe("メディア関連シナリオ", () => {
  test("E2E-04: アップロードした写真がアップロード完了後に一覧へ表示されること", async ({
    page,
  }) => {
    // ページ操作関数の初期化
    const uploadPage = createUploadPage({ page });
    const mediaPage = createMediaPage({ page });

    // メディア一覧ページへ移動
    await mediaPage.goto();

    // 初期状態のメディア数を確認
    const oldMediaCount = await mediaPage.getMediaCount();

    // アップロードページへアクセス
    await uploadPage.goto();

    // 写真を選択してアップロード実行
    await uploadPage.inputPhoto();
    await uploadPage.upload();

    // メディア一覧ページへ移動
    await mediaPage.goto();

    // アップロードしたメディアが一覧に追加されていることを検証
    await mediaPage.expectMediaCount(oldMediaCount + 1);
  });

  test("E2E-05: アルバムの作成操作により作成したアルバムに写真が追加され詳細で閲覧できること", async ({
    page,
  }) => {
    // ページ操作関数の初期化
    const albumPage = createAlbumPage({ page });

    // アルバム一覧ページへ移動
    await albumPage.goto();

    // アルバムを作成し詳細ページへ移動
    const albumTitle = `テストアルバム_${Date.now()}`;
    await albumPage.createAlbum({ title: albumTitle });
    await albumPage.openAlbum({ title: albumTitle });

    // アルバム詳細ページでメディアを追加
    await albumPage.addMedia();

    // 追加したメディアが一覧に追加されていることを検証
    await albumPage.expectMediaCount(1);
  });

  test("E2E-06: メディアの削除操作によってゴミ箱に移動し復元で元の一覧に戻ること", async ({
    page,
  }) => {
    // ページ操作関数の初期化
    const mediaPage = createMediaPage({ page });
    const trashPage = createTrashPage({ page });

    // メディア一覧ページへ移動
    await mediaPage.goto();

    // 初期状態のメディア数を確認
    const oldMediaCount = await mediaPage.getMediaCount();

    // メディアを選択してゴミ箱へ移動する
    await mediaPage.deleteMedia();

    // メディアがゴミ箱へ移動していることを検証
    await mediaPage.expectMediaCount(oldMediaCount - 1);

    // ゴミ箱ページへ移動
    await trashPage.goto();

    // メディアを復元する
    await trashPage.restoreMedia();

    // メディア一覧ページへ移動
    await mediaPage.goto();

    // メディアが元の一覧に戻っていることを検証
    await mediaPage.expectMediaCount(oldMediaCount);
  });

  test("E2E-07: 完全削除するとゴミ箱から消え復元不可になること", async ({ page }) => {
    // ページ操作関数の初期化
    const mediaPage = createMediaPage({ page });
    const trashPage = createTrashPage({ page });

    // メディア一覧ページへ移動
    await mediaPage.goto();

    // 初期状態のメディア数を確認
    const oldMediaCount = await mediaPage.getMediaCount();

    // メディアを選択してゴミ箱へ移動する
    await mediaPage.deleteMedia();

    // メディアがゴミ箱へ移動していることを検証
    await mediaPage.expectMediaCount(oldMediaCount - 1);

    // ゴミ箱ページへ移動
    await trashPage.goto();

    // 初期状態のゴミ箱内メディア数を確認
    const trashItemCount = await trashPage.getTrashItemCount();

    // メディアを完全削除する
    await trashPage.deleteMedia();

    // ゴミ箱内のメディア数が減少していることを検証
    await trashPage.expectTrashItemCount(trashItemCount - 1);
  });
});
