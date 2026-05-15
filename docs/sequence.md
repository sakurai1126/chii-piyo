# シーケンス図

## 概要

処理が複雑化する箇所をシーケンス図で記載

### 登場コンポーネント

| コンポーネント | 説明                                      |
| -------------- | ----------------------------------------- |
| ブラウザ       | ユーザーが操作するクライアント            |
| Next.js        | Amplify上のフロントエンドアプリケーション |
| Cognito        | 認証基盤                                  |
| Spring Boot    | Lightsail上のバックエンドAPIサーバー      |
| PostgreSQL     | データベース                              |
| S3             | 写真・動画ストレージ                      |

## 各種シーケンス

### ログイン

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Cognito as Cognito

    User->>Browser: ログイン画面にアクセス
    Browser->>Next: GET /login
    Next-->>Browser: ログインページ表示

    User->>Browser: メールアドレス・パスワード入力
    Browser->>Next: 認証リクエスト
    Next->>Cognito: Server Action認証リクエスト
    Cognito-->>Next: JWT（IDトークン・アクセストークン・リフレッシュトークン）

    Next-->>Browser: JWTをHttpOnly Cookieにセットしホーム画面にリダイレクト
```

### 認証付きAPIリクエスト

```mermaid
sequenceDiagram
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant Cognito as Cognito

  Next->>Cognito: 起動時または未取得時にJWK取得しキャッシュ
  Spring->>Cognito: 起動時または未取得時にJWK取得しキャッシュ

    Browser->>Next: ページリクエスト/Server Action呼び出し
    Next->>Next: Middlewareで JWT検証<br/>署名・有効期限・ユーザー情報

    alt JWT無効・期限切れ
        Next-->>Browser: ログイン画面にリダイレクト
    else JWT有効
        Next->>Spring: APIリクエスト<br/>Authorization: Bearer JWT
        Spring->>Spring: キャッシュ済みJWKで署名検証<br/>有効期限・クレーム確認

        alt 検証成功
            Spring-->>Next: 200 レスポンス
            Next-->>Browser: HTML/Server Actionレスポンス
        else 検証失敗
            Spring-->>Next: 401 Unauthorized
            Next-->>Browser: ログイン画面にリダイレクト
        end
    end
```

### 写真・動画アップロード

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL
    participant S3 as S3

    User->>Browser: アップロード画面でファイル選択
    User->>Browser: 共有範囲・アルバム等を設定しアップロード実行

    Browser->>Next: アップロードリクエスト
    Next->>Spring: POST /media（メタデータ）
    Spring->>DB: メディアレコード作成（upload_status: PROCESSING）
    DB-->>Spring: メディアID
    Spring->>S3: Pre-signed URL発行
    S3-->>Spring: 署名付きアップロードURL
    Spring-->>Next: 201 (メディアID、署名付きアップロードURL)
    Next-->>Browser: 署名付きURL返却

    Browser->>S3: PUT ファイルアップロード（署名付きURL）

    alt アップロード成功
        S3-->>Browser: 200 アップロード完了
        Browser->>Next: ステータス更新リクエスト
        Next->>Spring: PUT /media/{id}/status（COMPLETED）
        Spring->>DB: upload_status を COMPLETED に更新
        DB-->>Spring: 更新完了
        Spring-->>Next: 200 (メディア情報)
        Next-->>Browser: アップロード完了表示
    else アップロード失敗
        S3-->>Browser: エラーレスポンス
        Browser->>Next: ステータス更新リクエスト
        Next->>Spring: PUT /media/{id}/status（FAILED）
        Spring->>DB: upload_status を FAILED に更新
        DB-->>Spring: 更新完了
        Spring-->>Next: 200
        Next-->>Browser: エラー表示・リトライ案内
    end

    Note over Spring: 別スレッドで非同期サムネイル生成
    Spring->>S3: オリジナルダウンロード
    S3-->>Spring: ファイル
    Spring->>Spring: サムネイル生成 (画像: Thumbnailator / 動画: ffmpeg)
    Spring->>S3: サムネイルアップロード
    Spring->>DB: thumbnail_s3_key を更新
```

### ゴミ箱から完全削除

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL
    participant S3 as S3

    User->>Browser: 個別アイテムの完全削除ボタン押下
    Browser-->>User: 削除確認モーダル表示
    User->>Browser: 完全削除を確認

    Browser->>Next: 完全削除リクエスト
    Next->>Spring: DELETE /trash/{id}
    Spring->>DB: 権限チェック（ADMIN権限確認）

    alt 権限なし（一般ユーザー等）
        DB-->>Spring: 権限なし
        Spring-->>Next: 403 Forbidden
        Next-->>Browser: エラーメッセージ表示（権限がありません）
    else 権限あり（管理者・親権限）
        DB-->>Spring: 権限あり
        Spring->>S3: メディアファイル削除
        S3-->>Spring: 削除完了
        Spring->>DB: メディア関連レコード・trash_items・media 削除
        DB-->>Spring: 削除完了
        Spring-->>Next: 204 No Content
        Next-->>Browser: ゴミ箱一覧を更新
    end

    Note over User,S3: ゴミ箱を空にする場合は DELETE /trash で全件削除
```

### タグの更新

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    User->>Browser: タグの編集モーダルを開く
    Browser->>Next: タグ一覧取得
    Next->>Spring: GET /tags
    Spring->>DB: タグ一覧取得
    DB-->>Spring: タグ一覧
    Spring-->>Next: 200 (タグ一覧)
    Next-->>Browser: 選択可能なタグ一覧表示

    User->>Browser: タグを選択・変更して保存
    Browser->>Next: タグ更新リクエスト
    Next->>Spring: PUT /media/{mediaId}/tags（tagIds）
    Spring->>DB: 現在のmedia_tagsを取得
    DB-->>Spring: currentTagIds

    Spring->>Spring: 差分を抽出 (削除対象 / 追加対象)

    alt 変更あり
        Spring->>DB: 削除対象をDELETE
        Spring->>DB: 追加対象をINSERT (Bulk Insert)
        DB-->>Spring: 更新完了
    end

    Spring-->>Next: 200 OK (最新のタグ一覧)
    Next-->>Browser: 更新後のタグ表示
```

### アルバム削除

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    User->>Browser: アルバム削除ボタン押下
    Browser-->>User: 削除確認モーダル表示
    User->>Browser: 削除を確認

    Browser->>Next: 削除リクエスト
    Next->>Spring: DELETE /albums/{id}
    Spring->>DB: 権限チェック（ADMIN権限）

    alt 権限なし
        DB-->>Spring: 権限なし
        Spring-->>Next: 403 Forbidden
        Next-->>Browser: エラーメッセージ表示
    else 権限あり
        DB-->>Spring: 権限あり

        rect rgb(240, 240, 240)
            Note over Spring, DB: トランザクション開始
            Spring->>DB: 所属メディアの album_id を一括NULL更新
            Spring->>DB: アルバムレコードを削除
            DB-->>Spring: 完了
        end

        Spring-->>Next: 204 No Content
        Next-->>Browser: アルバム一覧画面に戻る
    end
```

### 育児記録の登録

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    User->>Browser: 育児記録トップ画面でワンタップボタン押下
    Browser-->>User: 記録入力モーダル表示（ミルク/排泄/食事/体調）

    User->>Browser: 内容を入力して保存

    Browser->>Next: 登録リクエスト
    Next->>Spring: POST /care-records
    rect rgb(240, 240, 240)
        Note over Spring, DB: トランザクション開始
        Spring->>DB: care_recordsを作成
        DB-->>Spring: 生成されたID

        Spring->>DB: 種別に応じた詳細テーブル(milk_detailsなど)に作成
        DB-->>Spring: 作成完了
    end
    Spring-->>Next: 201 (育児記録情報)
    Next-->>Browser: カレンダーに記録を反映
```

### 育児記録一覧取得(カレンダー形式 / タイムライン形式)

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    User->>Browser: 育児記録トップ画面にアクセス
    Browser->>Next: GET /care
    Next->>Spring: GET /care-records?startDate=...&endDate=...
    Spring->>DB: 日付範囲内の育児記録取得
    DB-->>Spring: 育児記録一覧
    Spring-->>Next: 200 (育児記録情報一覧)
    Next-->>Browser: カレンダーに記録マーカー表示

    User->>Browser: カレンダーの日付をタップ
    Browser->>Next: GET /care/:date
    Next->>Spring: GET /care-records?startDate=date&endDate=date
    Spring->>DB: 指定日の育児記録取得
    DB-->>Spring: 育児記録一覧
    Spring-->>Next: 200 (育児記録情報一覧)
    Next-->>Browser: タイムライン形式で記録一覧表示
```

### 育児記録の更新・削除

```mermaid
sequenceDiagram
    actor User as ユーザー
    participant Browser as ブラウザ
    participant Next as Next.js
    participant Spring as Spring Boot
    participant DB as PostgreSQL

    User->>Browser: タイムライン上の記録をタップして編集
    User->>Browser: 内容を変更して保存

    Browser->>Next: 更新リクエスト
    Next->>Spring: PUT /care-records/{id}（更新データ）
    rect rgb(240, 240, 240)
        Note over Spring, DB: トランザクション開始
        Spring->>DB: care_records更新
        DB-->>Spring: 更新完了
        Spring->>DB: 詳細テーブル更新
        DB-->>Spring: 更新完了
    end

    Spring-->>Next: 200 (更新後の育児記録情報)
    Next-->>Browser: タイムライン表示を更新

    User->>Browser: 記録の削除ボタン押下
    Browser-->>User: 削除確認モーダル表示
    User->>Browser: 削除を確認

    Browser->>Next: 削除リクエスト
    Next->>Spring: DELETE /care-records/{id}
    rect rgb(240, 240, 240)
        Note over Spring, DB: トランザクション開始
        Spring->>DB: 詳細テーブル削除
        DB-->>Spring: 削除完了
        Spring->>DB: care_records削除
        DB-->>Spring: 削除完了
    end

    Spring-->>Next: 204 No Content
    Next-->>Browser: タイムラインから記録を削除
```

## その他設計ドキュメント

- [アーキテクチャ設計書](./architecture.md)
- [テーブル定義書](./table-definition.md)
- [ER図](./er-diagram.md)
- [画面設計書](./screen-spec.md)
- [API仕様](./openapi.yaml)
