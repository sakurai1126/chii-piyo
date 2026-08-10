# フロントエンド単体テストケース

## バリデーション

| ケースID  | 対象関数                       | 観点  | 条件                   | 期待結果     |
| ------ | -------------------------- | --- | -------------------- | -------- |
| Val-01 | validateMilkUpdate         | 正常  | ミルク量に有効値（10〜400）を渡す  | trueが返る  |
| Val-02 | validateMilkUpdate         | 異常  | ミルク量が未入力             | falseが返る |
| Val-03 | validateMilkUpdate         | 境界  | ミルク量が400を超える         | falseが返る |
| Val-04 | validateMilkUpdate         | 境界  | ミルク量が10未満            | falseが返る |
| Val-05 | validateMilkUpdate         | 境界  | ミルク量がちょうど10 / 400    | trueが返る  |
| Val-06 | validateDiaperUpdate       | 正常  | 排泄タイプにDIRTY / WETを渡す | trueが返る  |
| Val-07 | validateDiaperUpdate       | 異常  | 排泄タイプが未入力            | falseが返る |
| Val-08 | validateDiaperUpdate       | 異常  | 排泄タイプが不正な値           | falseが返る |
| Val-09 | validateHealthUpdate       | 正常  | 体温に有効値（34〜42）を渡す     | trueが返る  |
| Val-10 | validateHealthUpdate       | 異常  | 体温が未入力               | falseが返る |
| Val-11 | validateHealthUpdate       | 境界  | 体温が34未満 / 42超        | falseが返る |
| Val-12 | validateHealthUpdate       | 境界  | 体温がちょうど34 / 42       | trueが返る  |
| Val-13 | validateGrowthRecordUpdate | 正常  | 身長または体重に有効値を渡す       | trueが返る  |
| Val-14 | validateGrowthRecordUpdate | 異常  | 身長・体重どちらも未入力         | falseが返る |
| Val-15 | validateGrowthRecordUpdate | 境界  | 身長が0以下 / 200超        | falseが返る |
| Val-16 | validateGrowthRecordUpdate | 境界  | 体重が0以下 / 200超        | falseが返る |
| Val-17 | validateCareRecordUpdate   | 正常  | MEAL種別を渡す            | trueが返る  |

## パラメータ生成・日付・グラフ

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| -------- | ------------------------------------ | --- | ------------------ | --------------------------- |
| Param-01 | generateUpdateCareRecordActionParams | 正常 | MILK種別と更新データを渡す | milkDetailのみ生成され他はundefined |
| Param-02 | generateUpdateCareRecordActionParams | 正常 | DIAPER種別を渡す | diaperDetailのみ生成される |
| Param-03 | generateUpdateCareRecordActionParams | 境界 | 種別に対応する値がundefined | 該当detailがundefinedになる |
| Date-01 | formatJapaneseDate | 正常 | 有効な日付を渡す | 日本語形式の日時文字列が返る |
| Date-02 | calculateRemainingDays | 正常 | 未来の期限日を渡す | 残り日数が返る |
| Date-03 | calculateRemainingDays | 境界 | 期限日が当日 | 0が返る |
| Date-04 | calculateDaysSinceBirth | 正常 | 誕生日を渡す | 経過日数が返る |
| Date-05 | dateOnlyToUtcNoon | 正常 | 日付文字列を渡す | UTC正午のDateが返る |
| Graph-01 | getAndBuildGraphData | 正常 | 記録データを渡す | グラフ描画用データが返る |
| Graph-02 | getAndBuildGraphData | 境界 | 空の記録データを渡す | 空の結果が返り例外にならない |
| Graph-03 | growthStandardRanges | 正常 | 月齢を渡す | 対応する標準範囲が返る |
| Graph-04 | growthStandardRanges | 境界 | 範囲外の月齢を渡す | 仕様通りの境界挙動になる |

## 認証・セッションユーティリティ

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| ---------- | ------------- | --- | ------------------------- | -------- |
| Jwt-01 | verifyIdToken | 正常 | 有効なIDトークンを渡す | trueが返る |
| Jwt-02 | verifyIdToken | 異常 | 署名・Issuer・Audienceが不正 | falseが返る |
| Jwt-03 | verifyIdToken | 異常 | token_useがidでない | falseが返る |
| Jwt-04 | verifyIdToken | 異常 | sub / emailクレームが欠落 | falseが返る |
| Jwt-05 | verifyIdToken | 異常 | 検証で例外が発生 | falseが返る |
| Session-01 | isAdminUser | 権限 | 取得したユーザーのroleがADMIN | trueが返る |
| Session-02 | isAdminUser | 権限 | 取得したユーザーのroleがADMIN以外 | falseが返る |
| Session-03 | isEasyMode | 正常 | 取得したユーザーのisEasyModeがtrue | trueが返る |
| Session-04 | isEasyMode | 正常 | 取得したユーザーのisEasyModeがfalse | falseが返る |

## カスタムフック

| ケースID | 対象フック | 観点 | 条件 | 期待結果 |
| ------- | -------------------- | --- | ----------- | ------------- |
| Hook-01 | useCareRecord | 正常 | 記録操作を実行 | 状態が期待通り更新される |
| Hook-02 | useCalendar | 正常 | 月移動・日付選択を行う | カレンダー状態が更新される |
| Hook-03 | useInfiniteMediaList | 正常 | 追加読み込みを行う | 次ページが結合されて返る |
| Hook-04 | useInfiniteMediaList | 境界 | 次ページが無い | 追加読み込みが行われない |
| Hook-05 | useUploadRunner | 正常 | アップロードを実行 | 進行状態が遷移し完了する |
| Hook-06 | useUploadRunner | 異常 | アップロードが失敗 | エラー状態に遷移する |

## Server Actions / API 層

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| --------- | ------------------------- | --- | --------------- | ----------------- |
| Action-01 | loginAction | 正常 | 有効な認証情報を渡す | ログインが成功しリダイレクトされる |
| Action-02 | loginAction | 異常 | 認証に失敗 | エラーが返る |
| Action-03 | createCareRecordAction | 正常 | 有効な記録データを渡す | 記録が作成される |
| Action-04 | createCareRecordAction | 異常 | API呼び出しが失敗 | エラーが返る |
| Action-05 | createMediaAction | 正常 | 有効なアップロード情報を渡す | メディアが作成される |
| Action-06 | deleteMultipleMediaAction | 正常 | 複数メディアIDを渡す | 対象が削除される |
| Api-01 | getMediaList | 正常 | 検索条件を渡す | メディア一覧が返る |
| Api-02 | uploadToS3 | 正常 | 署名付きURLとファイルを渡す | アップロードが成功する |
| Api-03 | uploadToS3 | 異常 | アップロードが失敗 | エラーが送出される |

## その他テストケース一覧

- [バックエンド単体テストケース(サービス層)](./unit-backend-service.md)
- [バックエンド単体テストケース(コントローラー層)](./unit-backend-controller.md)
- [バックエンド単体テストケース(セキュリティ・基盤処理)](./unit-backend-other.md)
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
