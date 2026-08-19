# バックエンド単体テストケース - セキュリティ・基盤処理

## セキュリティ・バリデーションロジック

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| ------- | -------------------------------- | --- | ---------------------- | ----------------------------------- |
| Auth-01 | AudienceValidator | 正常 | audienceが一致するJWTを渡す | successを返す |
| Auth-02 | AudienceValidator | 異常 | audienceが一致しないJWTを渡す | failureを返す |
| Auth-03 | AudienceValidator | 異常 | audienceがnullのJWTを渡す | failureを返す |
| Auth-04 | CustomJwtAuthenticationConverter | 正常 | subとemailを持つJWTを渡す | ロール付き認証トークンが生成される |
| Auth-05 | CustomJwtAuthenticationConverter | 異常 | subクレームが空のJWTを渡す | BadJwtExceptionの例外がスローされる |
| Auth-06 | CustomJwtAuthenticationConverter | 異常 | emailクレームが無いJWTを渡す | BadJwtExceptionの例外がスローされる |
| Auth-07 | CurrentUserProvider | 正常 | 有効なJWT認証コンテキストで呼ぶ | ユーザーIDが返る |
| Auth-08 | CurrentUserProvider | 異常 | 認証コンテキストがJWT認証でない | IllegalStateExceptionの例外がスローされる |
| Auth-09 | CurrentUserProvider | 異常 | JWTのsubに対応するユーザーが存在しない | ResourceNotFoundExceptionの例外がスローされる |
| Auth-10 | CustomAccessDeniedHandler | 権限 | 認可エラーが発生 | 403とFORBIDDENのレスポンスボディが返る |
| Auth-11 | CustomAuthenticationEntryPoint | 異常 | 認証エラーが発生 | 401とUNAUTHORIZEDのレスポンスボディが返る |

## 共通ユーティリティ

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| ------- | ----------------- | --- | ------------------------ | ---------------------------------- |
| Util-01 | S3KeyGenerator | 正常 | 英数字のみの安全なファイル名を渡す | prefix/yyyy/MM/dd/UUID_ファイル名の形式で返る |
| Util-02 | S3KeyGenerator | 異常 | null または空文字を渡す | ファイル名部分が unknown として生成される |
| Util-03 | S3KeyGenerator | 境界 | 日本語やパス区切り文字を含むファイル名を渡す | 許可されていない文字がすべて _ にサニタイズされて生成される |
| Util-04 | UserSyncComponent | 正常 | DBに存在するCognitoユーザーIDを渡す | 既存のユーザー情報がそのまま返る |
| Util-05 | UserSyncComponent | 正常 | DBに存在しないCognitoユーザーIDを渡す | デフォルト値（ロール等）がセットされた新規ユーザーとして保存され返る |
| Util-06 | UserSyncComponent | 正常 | ADMIN_EMAILと一致するメールアドレスで初回作成 | ADMINとして保存され返る |

## 共通例外処理

| ケースID | 対象メソッド | 観点 | 条件 | 期待結果 |
| ----- | ---------------------------- | --- | -------------------------------- | --------------------------------------- |
| Ex-01 | handleResourceNotFound | 異常 | ResourceNotFoundExceptionが発生 | NOT_FOUNDと404と指定エラーメッセージが返る |
| Ex-02 | handleResourceAccessDenied | 権限 | ResourceAccessDeniedExceptionが発生 | FORBIDDENと403と指定エラーメッセージが返る |
| Ex-03 | handleIllegalArgument | 異常 | IllegalArgumentExceptionが発生 | VALIDATION_ERRORと400と指定エラーメッセージが返る |
| Ex-04 | handleMethodArgumentNotValid | 異常 | バリデーションエラーが発生 | VALIDATION_ERRORと400と独自エラーメッセージが返る |
| Ex-05 | handleAccessDeniedException | 異常 | 未認証でのアクセス | UNAUTHORIZEDと401と指定エラーメッセージが返る |
| Ex-06 | handleException | 異常 | 想定外の例外が発生 | INTERNAL_SERVER_ERRORと500と指定エラーメッセージが返る |
| Ex-07 | handleExceptionInternal | 異常 | その他のSpring MVC標準の例外が発生 | 標準のステータスコードとメッセージが返る |

## スケジューラ

| ケースID | 対象 | 観点 | 条件 | 期待結果 |
| -------- | ----------------------- | --- | ------------------- | ---------------------- |
| Sched-01 | TrashCleanupScheduler | 正常 | 期限切れアイテムが存在する | 対象が完全削除処理に渡される |
| Sched-02 | TrashCleanupScheduler | 境界 | 期限切れアイテムが存在しない | 削除処理が呼ばれず正常終了する |
| Sched-03 | TrashCleanupScheduler | 境界 | S3キーとサムネイルキーが空 | S3削除処理が呼ばれず正常終了する |
| Sched-04 | TrashCleanupScheduler | 異常 | 削除中に例外が発生 | ログ出力して正常終了する |
| Sched-05 | ThumbnailRetryScheduler | 正常 | サムネイル未生成のメディアが存在する | 対象の件数分サムネイル生成処理が呼び出される |
| Sched-06 | ThumbnailRetryScheduler | 境界 | サムネイル未生成のメディアが存在しない | サムネイル生成処理は呼ばれず正常終了する |

## その他テストケース一覧

- [バックエンド単体テストケース(サービス層)](./unit-backend-service.md)
- [バックエンド単体テストケース(コントローラー層)](./unit-backend-controller.md)
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
