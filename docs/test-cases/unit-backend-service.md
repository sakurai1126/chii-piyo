# バックエンド単体テストケース - サービス層

## メディア管理

対象ファイル：MediaService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| -------- | ------------------ | --- | ----------------------- | --------------------------------------- |
| Media-01 | getMedia | 正常 | 自身がアクセス可能な有効IDを渡す | 対象メディアが返る |
| Media-02 | getMedia | 異常 | 存在しない、または権限外のIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Media-03 | getMediabyIds | 正常 | 有効なIDリストを渡す | 対応するメディア一覧が返る |
| Media-04 | countMedia | 正常 | 検索条件を渡す | 条件に合致する件数が返る |
| Media-05 | getMediaList | 正常 | 検索条件を渡す | 条件に合致するメディア一覧が返る |
| Media-06 | createMedia | 正常 | 有効なアップロード情報を渡す | メディアが登録されPre-signed URLが発行される |
| Media-07 | updateUploadStatus | 正常 | 自身のメディアIDとステータスを渡す | アップロードステータスが更新される |
| Media-08 | getMediaNavigation | 正常 | 有効なメディアIDを渡す | 前後のメディア位置情報が返る |
| Media-09 | updateMedia | 正常 | 自身のメディアの更新データを渡す | 対象メディアが更新される |
| Media-10 | updateMedia | 異常 | 存在しないメディアIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Media-11 | updateMedia | 権限 | 他ユーザーのメディアを更新しようとする | ResourceAccessDeniedExceptionの例外がスローされる |
| Media-12 | updateMedia | 異常 | 共有範囲更新で存在しない共有グループIDを指定 | ResourceNotFoundExceptionの例外がスローされる |
| Media-13 | updateMedia | 異常 | アルバム更新で存在しないアルバムIDを指定 | ResourceNotFoundExceptionの例外がスローされる |
| Media-14 | updateMediaBatch | 正常 | 有効なメディアIDリストと更新内容を渡す | 対象すべてが一括更新される |
| Media-15 | updateMediaBatch | 異常 | 対象メディアが存在しない | ResourceNotFoundExceptionの例外がスローされる |

## コメント管理

対象ファイル：MediaCommentService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ---------- | -------------------------- | --- | ------------------- | --------------------------------------- |
| Comment-01 | createMediaComment | 正常 | メディアID・ユーザーID・本文を渡す | コメントが登録される |
| Comment-02 | getMediaComments | 正常 | 有効なメディアIDを渡す | そのメディアのコメント一覧が返る |
| Comment-03 | getCommentCountsByMediaIds | 正常 | 有効なメディアIDリストを渡す | メディアごとのコメント件数が返る |
| Comment-04 | getCommentCountsByMediaIds | 境界 | 空のメディアIDリストを渡す | 空の結果が返る |
| Comment-05 | deleteMediaComment | 正常 | 自身のコメントを削除する | コメントが削除される |
| Comment-06 | deleteMediaComment | 権限 | 他ユーザーのコメントを削除しようとする | ResourceAccessDeniedExceptionの例外がスローされる |

## アルバム

対象ファイル：AlbumService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| -------- | ---------------------- | --- | ------------------------- | ----------------------------------- |
| Album-01 | createAlbum | 正常 | タイトルを渡す | アルバムが作成される |
| Album-02 | getAlbums | 正常 | 呼び出す | アルバム一覧が返る |
| Album-03 | getAlbumById | 正常 | 有効なアルバムIDを渡す | 対象アルバムが返る |
| Album-04 | getAlbumById | 異常 | 存在しないアルバムIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Album-05 | getMediaDataByAlbumIds | 正常 | PHOTOとVIDEOが混在するアルバムIDを渡す | アルバムごとに写真件数と動画件数が集計されて返る |
| Album-06 | getMediaDataByAlbumIds | 正常 | 複数アルバムのメディアを渡す | アルバムIDごとに分けて集計される |
| Album-07 | getMediaDataByAlbumIds | 境界 | 空のアルバムIDリストを渡す | 空のマップが返りリポジトリ層が呼ばれない |
| Album-08 | getMediaDataByAlbumIds | 境界 | サムネイル付きメディアが4件以上あるアルバムを渡す | カバーURLは3件までで打ち切られる |
| Album-09 | getMediaDataByAlbumIds | 境界 | サムネイルS3キーがnullのメディアを含む | 該当メディアのURLは追加されずURL生成も呼ばれない |
| Album-10 | updateAlbum | 正常 | アルバムIDと新しいタイトルを渡す | タイトルが更新される |
| Album-11 | deleteAlbum | 正常 | 有効なアルバムIDを渡す | 対象アルバムが削除される |
| Album-12 | addAlbumMedia | 正常 | アルバムIDとメディアIDリストを渡す | メディアがアルバムに追加される |
| Album-13 | addAlbumMedia | 異常 | 空のメディアIDリストを渡す | IllegalArgumentExceptionの例外がスローされる |
| Album-14 | addAlbumMedia | 異常 | 存在しないメディアを含める | ResourceNotFoundExceptionの例外がスローされる |
| Album-15 | deleteAlbumMedia | 正常 | アルバムに属するメディアを渡す | メディアがアルバムから削除される |
| Album-16 | deleteAlbumMedia | 異常 | 空のメディアIDリストを渡す | IllegalArgumentExceptionの例外がスローされる |
| Album-17 | deleteAlbumMedia | 異常 | 存在しないメディアを含める | ResourceNotFoundExceptionの例外がスローされる |
| Album-18 | deleteAlbumMedia | 異常 | アルバムに属さないメディアを含める | IllegalArgumentExceptionの例外がスローされる |

## お気に入り

対象ファイル：FavoriteService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ------ | ------------------------ | --- | ---------------------- | ---------------- |
| Fav-01 | addFavorite | 正常 | メディアIDと現ユーザーIDを渡す | お気に入りが登録される |
| Fav-02 | addFavorite | 境界 | 追加済みのメディアIDと現ユーザーIDを渡す | 即時リターンされる |
| Fav-03 | removeFavorite | 正常 | メディアIDと現ユーザーIDを渡す | お気に入りが解除される |
| Fav-04 | getCurrentUserIsFavorite | 正常 | お気に入り済みのメディアを渡す | trueが返る |
| Fav-05 | getCurrentUserIsFavorite | 正常 | 未登録のメディアを渡す | falseが返る |
| Fav-06 | getAddFavoriteUserIds | 正常 | 有効なメディアIDを渡す | 登録済みユーザーIDリストが返る |
| Fav-07 | getFavoriteList | 正常 | メディアリストを渡す | 対応するお気に入り一覧が返る |
| Fav-08 | getFavoriteList | 境界 | 空のメディアリストを渡す | 空リストが返る |

## ゴミ箱管理

対象ファイル：TrashService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| -------- | ------------------------- | --- | ------------------- | ----------------------------------- |
| Trash-01 | createTrashItem | 正常 | 有効なメディアIDを渡す | 対象IDのゴミ箱データが保存される |
| Trash-02 | createTrashItem | 境界 | 削除予定日時を検証する | 日本時間で30日後のAM2:00が設定される |
| Trash-03 | createTrashItems | 正常 | 複数のメディアIDリストを渡す | 件数分のゴミ箱データが一括保存される |
| Trash-04 | createTrashItems | 境界 | 空リストを渡す | 保存処理が呼ばれ0件で正常終了する |
| Trash-05 | getTrashItems | 正常 | offsetとlimitを渡す | 指定範囲のゴミ箱一覧が返る |
| Trash-06 | getTotalCount | 正常 | ゴミ箱にデータが存在する | 総件数が返る |
| Trash-07 | getEarliestDeadline | 正常 | 最古アイテムの期限が未来 | 今日から期限日までの残り日数が返る |
| Trash-08 | getEarliestDeadline | 境界 | 期限日が今日と同日 | 残り日数として0が返る |
| Trash-09 | getEarliestDeadline | 異常 | ゴミ箱が空 | nullが返る |
| Trash-10 | restoreTrashItem | 正常 | 有効なIDを渡す | 対象のゴミ箱データが削除される |
| Trash-11 | restoreTrashItems | 正常 | 複数のIDリストを渡す | 対象のゴミ箱データが一括削除される |
| Trash-12 | permanentlyDelete | 正常 | 有効なゴミ箱IDを渡す | 関連データ・ゴミ箱・メディアが削除されS3も削除される |
| Trash-13 | permanentlyDelete | 異常 | 存在しないゴミ箱IDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Trash-14 | permanentlyDelete | 異常 | ゴミ箱は存在するがメディアが存在しない | ResourceNotFoundExceptionの例外がスローされる |
| Trash-15 | permanentlyDelete | 境界 | S3キーとサムネイルキーが空 | S3削除処理が呼ばれず正常終了する |
| Trash-16 | permanentlyDelete | 正常 | 関連データの削除順序を検証する | 紐づきデータ削除の後にメディアが削除される |
| Trash-17 | multiplePermanentlyDelete | 正常 | 有効なゴミ箱IDリストを渡す | 対象すべての関連データ・ゴミ箱・メディア・S3が削除される |
| Trash-18 | multiplePermanentlyDelete | 異常 | 該当するゴミ箱データが存在しない | ResourceNotFoundExceptionの例外がスローされる |
| Trash-19 | allDelete | 正常 | ゴミ箱に複数データが存在する | 全件の関連データ・ゴミ箱・メディア・S3が削除される |
| Trash-20 | allDelete | 境界 | ゴミ箱が空 | 削除処理が呼ばれ0件で正常終了する |

## 育児記録管理

対象ファイル：CareRecordService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ------- | ---------------- | --- | ------------------ | ----------------------------------- |
| Care-01 | createCareRecord | 正常 | 種別MEALで食事詳細ありを渡す | 食事記録が保存される |
| Care-02 | createCareRecord | 正常 | 種別MILKでミルク詳細ありを渡す | ミルク記録が保存される |
| Care-03 | createCareRecord | 正常 | 種別DIAPERで排泄詳細ありを渡す | 排泄記録が保存される |
| Care-04 | createCareRecord | 正常 | 種別HEALTHで体調詳細ありを渡す | 体調記録が保存される |
| Care-05 | createCareRecord | 異常 | 種別MEALだが食事詳細が欠落 | IllegalArgumentExceptionの例外がスローされる |
| Care-06 | createCareRecord | 異常 | 種別MILKだがミルク詳細が欠落 | IllegalArgumentExceptionの例外がスローされる |
| Care-07 | createCareRecord | 異常 | 種別DIAPERだが排泄詳細が欠落 | IllegalArgumentExceptionの例外がスローされる |
| Care-08 | createCareRecord | 異常 | 種別HEALTHだが体調詳細が欠落 | IllegalArgumentExceptionの例外がスローされる |
| Care-09 | getCareRecords | 正常 | 開始日と終了日を渡す | 指定した開始日と終了日で取得処理が呼ばれ結果が返る |
| Care-10 | getMealRecords | 正常 | 有効な記録IDリストを渡す | 対応する食事詳細一覧が返る |
| Care-11 | getMealRecords | 境界 | 空の記録IDリストを渡す | 取得処理が呼ばれず空リストが返る |
| Care-12 | getMilkRecords | 正常 | 有効な記録IDリストを渡す | 対応するミルク詳細一覧が返る |
| Care-13 | getMilkRecords | 境界 | 空の記録IDリストを渡す | 取得処理が呼ばれず空リストが返る |
| Care-14 | getDiaperRecords | 正常 | 有効な記録IDリストを渡す | 対応する排泄詳細一覧が返る |
| Care-15 | getDiaperRecords | 境界 | 空の記録IDリストを渡す | 取得処理が呼ばれず空リストが返る |
| Care-16 | getHealthRecords | 正常 | 有効な記録IDリストを渡す | 対応する体調詳細一覧が返る |
| Care-17 | getHealthRecords | 境界 | 空の記録IDリストを渡す | 取得処理が呼ばれず空リストが返る |
| Care-18 | updateCareRecord | 正常 | 種別MEALで有効なIDと更新データを渡す | 対象の食事記録が更新される |
| Care-19 | updateCareRecord | 正常 | 種別MILKで有効なIDと更新データを渡す | 対象のミルク記録が更新される |
| Care-20 | updateCareRecord | 正常 | 種別DIAPERで有効なIDと更新データを渡す | 対象の排泄記録が更新される |
| Care-21 | updateCareRecord | 正常 | 種別HEALTHで有効なIDと更新データを渡す | 対象の体調記録が更新される |
| Care-22 | updateCareRecord | 異常 | 存在しないIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Care-23 | deleteCareRecord | 正常 | 種別MEALで有効なIDを渡す | 対象の食事記録が削除される |
| Care-24 | deleteCareRecord | 正常 | 種別MILKで有効なIDを渡す | 対象のミルク記録が削除される |
| Care-25 | deleteCareRecord | 正常 | 種別DIAPERで有効なIDを渡す | 対象の排泄記録が削除される |
| Care-26 | deleteCareRecord | 正常 | 種別HEALTHで有効なIDを渡す | 対象の体調記録が削除される |

## 共有範囲管理

対象ファイル：SharingGroupService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| -------- | ------------------------ | --- | ------------------ | ----------------------------------- |
| Share-01 | getSharingGroups | 正常 | 有効なユーザーIDを渡す | そのユーザーの共有グループ一覧が返る |
| Share-02 | getAllSharingGroups | 正常 | 呼び出す | 全共有グループ一覧が返る |
| Share-03 | getSharingGroupById | 正常 | 有効なグループIDを渡す | 対象の共有グループが返る |
| Share-04 | getSharingGroupById | 異常 | 存在しないグループIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Share-05 | createGroup | 正常 | グループ名とユーザーIDリストを渡す | 共有グループとメンバーが作成される |
| Share-06 | getMembersByGroupIds | 正常 | 有効なグループIDリストを渡す | 対応するメンバー一覧が返る |
| Share-07 | editMembers | 正常 | メンバーの追加削除を渡す | メンバー構成が更新される |
| Share-08 | deleteSharingGroup | 正常 | 有効なグループIDを渡す | 対象の共有グループが削除される |
| Share-09 | updateSharingGroup | 正常 | 対象グループと新しい名前を渡す | グループ名が更新される |
| Share-10 | getUserSharingScopes | 正常 | 有効なユーザーIDを渡す | そのユーザーの共有範囲IDリストが返る |
| Share-11 | getUserSharingScopesBulk | 正常 | 複数ユーザーIDを渡す | ユーザーごとの共有範囲マップが返る |
| Share-12 | getUserSharingScopesBulk | 境界 | 空のユーザーIDリストを渡す | 空のマップが返る |

## ユーザー管理

対象ファイル：UserService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ------- | -------------------------------- | --- | ------------------- | ----------------------------------- |
| User-01 | getUserById | 正常 | 有効なユーザーIDを渡す | 対象ユーザーが返る |
| User-02 | getUserById | 異常 | 存在しないユーザーIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| User-03 | getUsersById | 正常 | 有効なIDリストを渡す | 対応するユーザー一覧が返る |
| User-04 | getUsersById | 境界 | nullまたは空リストを渡す | 空リストが返り検索が実行されない |
| User-05 | updateMe | 正常 | 表示名を渡す | 表示名が更新される |
| User-06 | updateMe | 正常 | ダークモード・かんたんモードを渡す | 各設定が更新される |
| User-07 | updateMe | 正常 | profile/で始まるS3キーを渡す | アイコンキーが更新される |
| User-08 | updateMe | 異常 | profile/以外のS3キーを渡す | アイコンキーが更新されない |
| User-09 | updateMe | 境界 | 更新項目がすべてnull | DB更新処理が呼ばれない |
| User-10 | generateIconDownloadPresignedUrl | 正常 | アイコンキーを持つユーザーを渡す | ダウンロード用URLが返る |
| User-11 | generateIconDownloadPresignedUrl | 境界 | アイコンキーがnullまたは空 | nullが返りURL生成が呼ばれない |
| User-12 | generateIconPresignedUrl | 正常 | ファイル名とコンテンツタイプを渡す | S3キーとアップロード用URLが返る |
| User-13 | getUsersAndIcon | 正常 | ユーザーが複数存在する | 各ユーザーとアイコンURLがまとめて返る |
| User-14 | updateRole | 正常 | 有効なユーザーIDとロールを渡す | ロールが更新される |
| User-15 | updateRole | 異常 | 存在しないユーザーIDを渡す | ResourceNotFoundExceptionの例外がスローされる |

## はじめて記録管理

対象ファイル：FirstRecordService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| -------- | ----------------- | --- | ------------------ | -------------------------------- |
| First-01 | createFirstRecord | 正常 | メディアIDを含む記録データを渡す | 記録が保存され紐づくメディアも保存される |
| First-02 | createFirstRecord | 境界 | メディアIDが空の記録データを渡す | 記録のみ保存されメディア保存が呼ばれない |
| First-03 | createFirstRecord | 異常 | 存在しないメディアIDを含める | ResourceNotFoundException が送出される |
| First-04 | getFirstRecords | 正常 | 記録とメディアが存在する | 記録ごとにメディアが紐づいて返る |
| First-05 | getFirstRecords | 境界 | 記録が0件 | 空リストが返りメディア取得が呼ばれない |
| First-06 | getFirstRecords | 境界 | 記録はあるがメディアが紐づかない | メディアが空リストの記録が返る |
| First-07 | getFirstRecords | 境界 | ゴミ箱内のメディアIDが紐づいている | 該当メディアが除外されて返る |
| First-08 | updateFirstRecord | 正常 | 有効なIDと更新データを渡す | 既存メディアが削除された後に記録が更新され新メディアが保存される |
| First-09 | updateFirstRecord | 異常 | 存在しない記録IDを渡す | ResourceNotFoundException が送出される |
| First-10 | deleteFirstRecord | 正常 | 有効なIDを渡す | 紐づくメディアが削除された後に記録が削除される |

## ことば記録管理

対象ファイル：WordRecordService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ------- | ---------------- | --- | ------------------ | -------------------------------- |
| Word-01 | createWordRecord | 正常 | メディアIDを含む記録データを渡す | 記録が保存され紐づくメディアも保存される |
| Word-02 | createWordRecord | 境界 | メディアIDが空の記録データを渡す | 記録のみ保存されメディア保存が呼ばれない |
| Word-03 | createWordRecord | 異常 | 存在しないメディアIDを含める | ResourceNotFoundException が送出される |
| Word-04 | getWordRecords | 正常 | 記録とメディアが存在する | 記録ごとにメディアが紐づいて返る |
| Word-05 | getWordRecords | 境界 | 記録が0件 | 空リストが返りメディア取得が呼ばれない |
| Word-06 | getWordRecords | 境界 | 記録はあるがメディアが紐づかない | メディアが空リストの記録が返る |
| Word-07 | getWordRecords | 境界 | ゴミ箱内のメディアIDが紐づいている | 該当メディアが除外されて返る |
| Word-08 | updateWordRecord | 正常 | 有効なIDと更新データを渡す | 既存メディアが削除された後に記録が更新され新メディアが保存される |
| Word-09 | updateWordRecord | 異常 | 存在しない記録IDを渡す | ResourceNotFoundException が送出される |
| Word-10 | deleteWordRecord | 正常 | 有効なIDを渡す | 紐づくメディアが削除された後に記録が削除される |

## 成長記録管理

対象ファイル：GrowthRecordService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| --------- | ------------------ | --- | ---------------------- | ----------------------------------- |
| Growth-01 | createGrowthRecord | 正常 | 身長・体重・メモをすべて指定して渡す | 成長記録が登録される |
| Growth-02 | getGrowthRecords | 正常 | 記録が存在する期間を渡す | 該当期間の成長記録一覧が返る |
| Growth-03 | updateGrowthRecord | 正常 | 有効なIDと更新データを渡す | 対象の成長記録が更新される |
| Growth-04 | updateGrowthRecord | 異常 | 存在しないIDを渡す | ResourceNotFoundExceptionの例外がスローされる |
| Growth-05 | deleteGrowthRecord | 正常 | 有効なIDを渡す | 対象の成長記録が削除される |

## タグ管理

対象ファイル：TagService.java

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ------ | -------------------- | --- | -------------------- | -------------------------------- |
| Tag-01 | createTag | 正常 | タグ名を渡す | タグが保存される |
| Tag-02 | getTags | 正常 | 呼び出す | ID昇順のタグ一覧が返る |
| Tag-03 | getTagById | 正常 | 有効なタグIDを渡す | 対象タグが返る |
| Tag-04 | getTagById | 異常 | 存在しないタグIDを渡す | ResourceNotFoundException が送出される |
| Tag-05 | getMediaTags | 正常 | タグが紐づくメディアIDを渡す | 紐づくタグ一覧が返る |
| Tag-06 | getMediaTags | 境界 | タグが紐づかないメディアIDを渡す | 空リストが返り findByIds が呼ばれない |
| Tag-07 | syncMediaTags | 正常 | 既存タグに無いタグIDを渡す | 差分のタグのみ登録される |
| Tag-08 | syncMediaTags | 正常 | 既存タグから一部を除いたタグIDを渡す | 除かれたタグのみ削除される |
| Tag-09 | syncMediaTags | 正常 | 追加と削除が同時に発生するタグIDを渡す | 追加と削除の両方が実行される |
| Tag-10 | syncMediaTags | 境界 | 既存タグと同一のタグIDを渡す | 登録も削除も呼ばれない |
| Tag-11 | syncMediaTags | 境界 | 空のタグIDリストを渡す | 既存タグが全件削除され登録は呼ばれない |
| Tag-12 | getMediaCountByTagId | 正常 | タグごとのメディア数が存在する | タグIDをキーとしたマップが返る |
| Tag-13 | updateTag | 正常 | タグIDと新しい名前を渡す | タグ名が更新される |
| Tag-14 | updateTag | 異常 | 存在しないタグIDを渡す | ResourceNotFoundException が送出される |
| Tag-15 | deleteTag | 正常 | 有効なタグIDを渡す | メディアとの紐づきが削除された後にタグが削除される |
| Tag-16 | count | 正常 | タグIDリストを渡す | 一致するタグの件数が返る |

# その他テストケース一覧

- [バックエンド単体テストケース(コントローラー層)](./unit-backend-controller.md)
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
