# バックエンド単体テストケース - セキュリティ・基盤処理

## セキュリティ・バリデーションロジック

|ケースID|対象|観点|条件|期待結果|
|---|---|---|---|---|
|Auth-01|AudienceValidator|正常|audienceが一致するJWTを渡す|successを返す|
|Auth-02|AudienceValidator|異常|audienceが一致しないJWTを渡す|failureを返す|
|Auth-03|AudienceValidator|異常|audienceがnullのJWTを渡す|failureを返す|
|Auth-04|CustomJwtAuthenticationConverter|正常|subとemailを持つJWTを渡す|ロール付き認証トークンが生成される|
|Auth-05|CustomJwtAuthenticationConverter|異常|subクレームが空のJWTを渡す|BadJwtExceptionの例外がスローされる|
|Auth-06|CustomJwtAuthenticationConverter|異常|emailクレームが無いJWTを渡す|BadJwtExceptionの例外がスローされる|
|Auth-07|CurrentUserProvider|正常|有効なJWT認証コンテキストで呼ぶ|ユーザーIDが返る|
|Auth-08|CurrentUserProvider|異常|認証コンテキストがJWT認証でない|IllegalStateExceptionの例外がスローされる|
|Auth-09|CurrentUserProvider|異常|JWTのsubに対応するユーザーが存在しない|ResourceNotFoundExceptionの例外がスローされる|
|Auth-10|CustomAccessDeniedHandler|権限|認可エラーが発生|403とFORBIDDENのレスポンスボディが返る|
|Auth-11|CustomAuthenticationEntryPoint|異常|認証エラーが発生|401とUNAUTHORIZEDのレスポンスボディが返る|

## 共通例外処理

|ケースID|観点|条件|期待結果|
|---|---|---|---|
|Ex-01|異常|ResourceNotFoundExceptionが発生|NOT_FOUND と 404 が返る|
|Ex-02|権限|ResourceAccessDeniedExceptionが発生|FORBIDDEN と 403 が返る|
|Ex-03|異常|IllegalArgumentException / バリデーション違反が発生|VALIDATION_ERROR と 400 が返る|
|Ex-04|異常|未認証でのアクセス|UNAUTHORIZED と 401 が返る|
|Ex-05|異常|想定外の例外が発生|INTERNAL_SERVER_ERROR と 500 が返る|

## スケジューラ

|ケースID|対象|観点|条件|期待結果|
|---|---|---|---|---|
|Sched-01|TrashCleanupScheduler|正常|期限切れアイテムが存在する|対象が完全削除処理に渡される|
|Sched-02|TrashCleanupScheduler|境界|期限が現在時刻と同時刻|期限切れとして削除対象に含まれる|
|Sched-03|TrashCleanupScheduler|境界|期限がまだ到来していない|削除対象に含まれない|
|Sched-04|TrashCleanupScheduler|境界|期限切れアイテムが存在しない|削除処理が呼ばれず正常終了する|

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
