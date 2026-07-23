# バックエンド単体テストケース - コントローラー層

## メディア管理

対象ファイル：MediaController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|MediaCtrl-01|getMediaList|正常|検索条件を渡す|メディア一覧・お気に入り情報・コメント件数が整形され200が返る|
|MediaCtrl-02|getMediaList|境界|offset+取得件数が総件数と一致|hasNextがfalseになる|
|MediaCtrl-03|getMediaList|境界|offset+取得件数が総件数未満|hasNextがtrueになる|
|MediaCtrl-04|getMediaList|正常|現ユーザーがお気に入り済みのメディアを含む|対象のisFavoriteがtrueで返る|
|MediaCtrl-05|getMediaList|境界|サムネイルS3キーがnullのメディアを含む|該当メディアのサムネイルURLがnullで返る|
|MediaCtrl-06|getMedia|正常|有効なメディアIDを渡す|200とメディア詳細が返る|
|MediaCtrl-07|createMedia|正常|有効なアップロードリクエストを渡す|201とメディアID・署名付きURLが返る|
|MediaCtrl-08|updateMediaUploadStatus|正常|有効なステータス更新を渡す|204が返る|
|MediaCtrl-09|updateMedia|正常|有効な更新データを渡す|204が返る|
|MediaCtrl-10|updateMediaBatch|正常|複数メディアの一括更新を渡す|204が返る|
|MediaCtrl-11|deleteMedia|正常|有効なメディアIDを渡す|204が返る|
|MediaCtrl-12|deleteMultipleMedia|正常|複数メディアIDを渡す|204が返る|

## ユーザー管理

対象ファイル：UserController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|UserCtrl-01|getMe|正常|認証済みユーザーで呼ぶ|200と自身のユーザー情報が返る|
|UserCtrl-02|updateMe|正常|有効な更新データを渡す|204が返る|
|UserCtrl-03|generateIconPresignedUrl|正常|ファイル情報を渡す|201とS3キー・アップロード用URLが返る|
|UserCtrl-04|getUsers|正常|呼び出す|200とユーザー一覧がアイコンURL付きで返る|
|UserCtrl-05|updateRole|正常|ユーザーIDとロールを渡す|204が返る|

## 共有範囲管理

対象ファイル：SharingGroupController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|ShareCtrl-01|getSharingGroups|正常|認証済みユーザーで呼ぶ|200とメンバー・アイコンが整形されたグループ一覧が返る|
|ShareCtrl-02|getAllSharingGroups|正常|呼び出す|200と全グループ一覧が返る|
|ShareCtrl-03|createSharingGroup|正常|グループ名とメンバーを渡す|201が返る|
|ShareCtrl-04|updateSharingGroup|正常|グループIDと更新内容を渡す|204が返る|
|ShareCtrl-05|deleteSharingGroup|正常|有効なグループIDを渡す|204が返る|

## コメント管理

対象ファイル：MediaCommentController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|CommentCtrl-01|getMediaComments|正常|有効なメディアIDを渡す|200と投稿者情報付きのコメント一覧が返る|
|CommentCtrl-02|createMediaComment|正常|メディアIDと本文を渡す|201が返る|
|CommentCtrl-03|deleteMediaComment|正常|自身のコメントIDを渡す|204が返る|

## ゴミ箱管理

対象ファイル：TrashController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|TrashCtrl-01|getTrashItems|正常|offsetとlimitを渡す|200とゴミ箱一覧・総件数・残り日数が返る|
|TrashCtrl-02|restoreTrashItem|正常|有効なIDを渡す|204が返る|
|TrashCtrl-03|restoreTrashItems|正常|複数IDを渡す|204が返る|
|TrashCtrl-04|deleteTrashItem|正常|有効なIDを渡す|204が返る|
|TrashCtrl-05|deleteTrashItems|正常|複数IDを渡す|204が返る|
|TrashCtrl-06|emptyTrash|正常|呼び出す|204が返る|

## 育児記録管理

対象ファイル：CareRecordController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|CareCtrl-01|getCareRecords|正常|期間を渡す|200と種別ごとの詳細を含む記録一覧が返る|
|CareCtrl-02|createCareRecord|正常|有効な記録データを渡す|201が返る|
|CareCtrl-03|updateCareRecord|正常|IDと更新データを渡す|204が返る|
|CareCtrl-04|deleteCareRecord|正常|有効なIDを渡す|204が返る|

## アルバム

対象ファイル：AlbumController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|AlbumCtrl-01|getAlbums|正常|呼び出す|200とアルバム一覧が返る|
|AlbumCtrl-02|getAlbum|正常|有効なアルバムIDを渡す|200とアルバム詳細が返る|
|AlbumCtrl-03|createAlbum|正常|タイトルを渡す|201が返る|
|AlbumCtrl-04|updateAlbum|正常|IDとタイトルを渡す|204が返る|
|AlbumCtrl-05|deleteAlbum|正常|有効なIDを渡す|204が返る|
|AlbumCtrl-06|addAlbumMedia|正常|アルバムIDとメディアIDリストを渡す|204が返る|
|AlbumCtrl-07|deleteAlbumMedia|正常|アルバムIDとメディアIDリストを渡す|204が返る|

## タグ管理

対象ファイル：TagController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|TagCtrl-01|getTags|正常|呼び出す|200とタグ一覧が返る|
|TagCtrl-02|createTag|正常|タグ名を渡す|201が返る|
|TagCtrl-03|updateTag|正常|IDと更新内容を渡す|204が返る|
|TagCtrl-04|updateMediaTags|正常|メディアIDとタグIDリストを渡す|204が返る|
|TagCtrl-05|deleteTag|正常|有効なタグIDを渡す|204が返る|

## はじめて・ことば記録管理

対象ファイル：FirstRecordController.java / WordRecordController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|FirstCtrl-01|getFirstRecords|正常|呼び出す|200とはじめて記録一覧が返る|
|FirstCtrl-02|createFirstRecord|正常|有効な記録データを渡す|201が返る|
|FirstCtrl-03|updateFirstRecord|正常|IDと更新データを渡す|204が返る|
|FirstCtrl-04|deleteFirstRecord|正常|有効なIDを渡す|204が返る|
|WordCtrl-01|getWordRecords|正常|呼び出す|200とことば記録一覧が返る|
|WordCtrl-02|createWordRecord|正常|有効な記録データを渡す|201が返る|
|WordCtrl-03|updateWordRecord|正常|IDと更新データを渡す|204が返る|
|WordCtrl-04|deleteWordRecord|正常|有効なIDを渡す|204が返る|

## 成長記録管理

対象ファイル：GrowthRecordController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|GrowthCtrl-01|getGrowthRecords|正常|期間を渡す|200と成長記録一覧が返る|
|GrowthCtrl-02|createGrowthRecord|正常|有効な記録データを渡す|201が返る|
|GrowthCtrl-03|updateGrowthRecord|正常|IDと更新データを渡す|204が返る|
|GrowthCtrl-04|deleteGrowthRecord|正常|有効なIDを渡す|204が返る|

## お気に入り

対象ファイル：FavoriteController.java

|ケースID|対象メソッド|観点|条件|期待結果|
|---|---|---|---|---|
|FavCtrl-01|addFavorite|正常|有効なメディアIDを渡す|201が返る|
|FavCtrl-02|removeFavorite|正常|有効なメディアIDを渡す|204が返る|

## その他テストケース一覧

- [バックエンド単体テストケース(サービス層)](./unit-backend-service.md)
- [バックエンド単体テストケース(セキュリティ・基盤処理)](./unit-backend-other.md)
- [フロントエンド単体テストケース](./unit-frontend.md)
- [結合テストケース](./integration.md)
- [E2Eテストケース](./e2e.md)

## その他設計ドキュメント

- [アーキテクチャ設計書](../architecture.md)
- [テーブル定義書](../table-definition.md)
- [ER図](../er-diagram.md)
- [画面設計書](../screen-spec.md)
- [API仕様](../openapi.yaml)
- [シーケンス図](../sequence.md)
- [テスト設計書](../test-plan.md)
