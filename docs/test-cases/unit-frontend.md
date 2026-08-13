# フロントエンド単体テストケース

## バリデーション

| ケースID | 対象関数 | 観点 | 条件 | 期待結果 |
| ------ | -------------------------- | --- | -------------------- | -------- |
| Val-01 | validateMilkUpdate | 正常 | ミルク量に有効値（10〜400）を渡す | trueが返る |
| Val-02 | validateMilkUpdate | 異常 | ミルク量が未入力 | falseが返る |
| Val-03 | validateMilkUpdate | 境界 | ミルク量が400を超える | falseが返る |
| Val-04 | validateMilkUpdate | 境界 | ミルク量が10未満 | falseが返る |
| Val-05 | validateMilkUpdate | 境界 | ミルク量がちょうど10 / 400 | trueが返る |
| Val-06 | validateDiaperUpdate | 正常 | 排泄タイプにDIRTY / WETを渡す | trueが返る |
| Val-07 | validateDiaperUpdate | 異常 | 排泄タイプが未入力 | falseが返る |
| Val-08 | validateDiaperUpdate | 異常 | 排泄タイプが不正な値 | falseが返る |
| Val-09 | validateHealthUpdate | 正常 | 体温に有効値（34〜42）を渡す | trueが返る |
| Val-10 | validateHealthUpdate | 異常 | 体温が未入力 | falseが返る |
| Val-11 | validateHealthUpdate | 境界 | 体温が34未満 / 42超 | falseが返る |
| Val-12 | validateHealthUpdate | 境界 | 体温がちょうど34 / 42 | trueが返る |
| Val-13 | validateGrowthRecordUpdate | 正常 | 身長または体重に有効値を渡す | trueが返る |
| Val-14 | validateGrowthRecordUpdate | 異常 | 身長・体重どちらも未入力 | falseが返る |
| Val-15 | validateGrowthRecordUpdate | 境界 | 身長が0以下 / 200超 | falseが返る |
| Val-16 | validateGrowthRecordUpdate | 境界 | 体重が0以下 / 200超 | falseが返る |
| Val-17 | validateCareRecordUpdate | 正常 | MEAL種別を渡す | trueが返る |

## パラメータ生成

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| -------- | ------------------------------------ | --- | ------------------ | ----------------- |
| Param-01 | generateUpdateCareRecordActionParams | 正常 | MEAL種別と更新データを渡す | 食事記録詳細のみ生成される |
| Param-02 | generateUpdateCareRecordActionParams | 正常 | MILK種別と更新データを渡す | ミルク記録詳細のみ生成される |
| Param-03 | generateUpdateCareRecordActionParams | 正常 | DIAPER種別と更新データを渡す | 排泄記録詳細のみ生成される |
| Param-04 | generateUpdateCareRecordActionParams | 正常 | HEALTH種別と更新データを渡す | 健康記録詳細のみ生成される |
| Param-05 | generateUpdateCareRecordActionParams | 境界 | 種別に対応する値がundefined | 該当詳細がundefinedになる |

## 日付関連

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| ------- | -------------------------- | --- | --------------------- | ------------------------ |
| Date-01 | formatJapaneseDate | 正常 | 有効な日付を渡す | YYYY年M月D日 HH:MM形式の文字列が返る |
| Date-02 | formatJapaneseDate | 異常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-03 | formatJapaneseDateNonTime | 正常 | 有効な日付を渡す | YYYY年M月D日形式の文字列が返る |
| Date-04 | formatJapaneseDateNonTime | 正常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-05 | formatShortDate | 正常 | 有効な日付を渡す | 日本時間のM/D形式の文字列が返る |
| Date-06 | formatShortDate | 正常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-07 | formatShortMonth | 正常 | 有効な日付を渡す | 日本時間のYYYY/M形式の文字列が返る |
| Date-08 | formatShortMonth | 正常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-09 | formatJapaneseDateBasic | 正常 | 有効な日付を渡す | YYYY-MM-DD形式の文字列が返る |
| Date-10 | formatJapaneseDateBasic | 正常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-11 | formatJapaneseDateTimeOnly | 正常 | 有効な日付を渡す | HH:MM形式の文字列が返る |
| Date-12 | formatJapaneseDateTimeOnly | 正常 | 無効な日付文字列を渡す | 空文字が返る |
| Date-13 | calculateRemainingDays | 正常 | 未来の期限日を渡す | 残り日数（正の整数）が返る |
| Date-14 | calculateRemainingDays | 境界 | 期限日が当日または過去日付 | 0が返る |
| Date-15 | getCurrentDateTime | 正常 | 関数を実行する | 現在日時と現在時刻が返る |
| Date-16 | calculateDaysSinceBirth | 正常 | 誕生日からの経過日付を渡す | X年Yヶ月Z日形式の経過日数文字列が返る |
| Date-17 | calculateDaysSinceBirth | 境界 | 月跨ぎで日繰り下げが発生する日付を渡す | 前月末日を加算した日繰り下げ計算結果が返る |
| Date-18 | calculateDaysSinceBirth | 境界 | 誕生日より前の日付を渡す | 0日が返る |
| Date-19 | dateOnlyToUtcNoon | 正常 | YYYY-MM-DD形式の日付文字列を渡す | UTC正午が返る |

## グラフ

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| -------- | -------------------- | --- | ----------------- | ------------------------- |
| Graph-01 | getAndBuildGraphData | 正常 | 管理者権限で実行 | 12か月分および7日分の集計データが構築されて返る |
| Graph-02 | getAndBuildGraphData | 正常 | 一般ユーザー権限で実行 | 育児記録が集計されず初期値として返る |
| Graph-03 | getAndBuildGraphData | 境界 | 記録データが存在しない | 12か月分および7日分の空のデータ構造が返る |
| Graph-04 | getAndBuildGraphData | 境界 | 同一月に複数の身体測定記録が存在 | 最新の測定記録が優先して採用される |
| Graph-05 | getAndBuildGraphData | 境界 | 発育標準範囲内外の月齢を対象とする | 標準範囲のデータが正しく処理される |

## カスタムフック

| ケースID | 対象フック | 観点 | 条件 | 期待結果 |
| ------- | ------------------- | --- | --------------------------- | ---------------------------- |
| Hook-01 | useCalendar | 正常 | 初期化時 | 当週の日付一覧および当日の選択状態が生成される |
| Hook-02 | useCalendar | 正常 | 翌週への移動操作を実行 | 表示週が7日後にずれ、週の日付一覧が再生成される |
| Hook-03 | useCalendar | 境界 | 日付移動で週をまたぐ場合 | 自動的に週変更が実行され表示週が更新される |
| Hook-04 | useCalendar | 境界 | 当週以外に移動後、当週に戻った場合 | 現在の週のフラグが切り替わる |
| Hook-05 | useCalendarPop | 正常 | 育児記録がセットされた状態で編集モードを開く | 記録の日時・メモ・種別固有の値が入力初期値にセットされる |
| Hook-06 | useCalendarPop | 正常 | 成長記録がセットされた状態で編集モードを開く | 記録の日付・身長・体重・メモが入力初期値にセットされる |
| Hook-07 | useCalendarPop | 異常 | 育児記録のバリデーションに失敗する入力で保存操作を実行 | 更新APIが呼ばれずエラーとなる |
| Hook-08 | useRecordEdit | 正常 | 必須項目を入力した状態で確認操作を実行 | 入力検証を通過し確認状態へ遷移する |
| Hook-09 | useRecordEdit | 異常 | 必須項目が未入力の状態で確認操作を実行 | 入力検証エラーとなり確認状態へ遷移しない |
| Hook-10 | useRecordEdit | 正常 | 編集のキャンセル操作を実行 | 入力内容および選択済みメディアが初期状態にリセットされる |
| Hook-11 | useDragAndDrop | 正常 | 指定形式のファイルをドロップ | ファイル検証を通過しファイル追加処理が実行される |
| Hook-12 | useDragAndDrop | 異常 | 対象外形式のファイルをドロップ | 対象外ファイルが除外され追加処理が実行されない |
| Hook-13 | useDragAndDrop | 境界 | 子要素をまたいでドラッグした場合 | ドラッグ状態が途中で意図せず解除されない |
| Hook-14 | useUploadMediaState | 正常 | サイズ・枚数制限内の画像を追加 | ファイルが一覧に追加される |
| Hook-15 | useUploadMediaState | 境界 | 上限枚数（30枚）を超える画像を追加 | 上限分のみ追加され超過分はスキップされる |
| Hook-16 | useUploadMediaState | 境界 | サイズ制限（20MB）を超える画像を追加 | サイズ超過ファイルが除外される |
| Hook-17 | useUploadMediaState | 正常 | 特定インデックスのファイルを個別削除 | 対象ファイルのみ一覧から除外される |
| Hook-18 | useUploadMediaState | 正常 | 全ファイルを一括削除 | 一覧が空になる |
| Hook-19 | useUploadRunner | 正常 | 複数ファイルのアップロード処理を開始 | 最大3並列で処理が実行され全件完了コールバックが呼ばれる |
| Hook-20 | useUploadRunner | 異常 | アップロード処理中に通信エラーが発生 | 失敗件数がカウントされエラー情報が保持される |
| Hook-21 | useUploadRunner | 境界 | 既にアップロード中に再度実行を呼び出す | 二重実行されずスキップされる |
| Hook-22 | useUploadRunner | 境界 | アップロード中に処理を中断 | 処理が中断され残キューが実行されない |

## アップロード

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| --------- | ---------- | --- | -------------------- | ------------------------ |
| Upload-01 | uploadToS3 | 正常 | アップロードURLとファイルを渡して実行 | アップロードが実行され、進捗率が正しく通知される |
| Upload-02 | uploadToS3 | 異常 | S3から4xx/5xxレスポンス | 適切なエラーが送出される |
| Upload-03 | uploadToS3 | 異常 | ネットワークエラーが発生 | 適切なエラーが送出される |
| Upload-04 | uploadToS3 | 境界 | アップロード途中でによる中断が発生 | 処理が中断され、中断エラーが送出される |

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
