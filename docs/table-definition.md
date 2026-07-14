# テーブル定義書
## 概要
- RDBMS: PostgreSQL
- 文字コード: UTF-8
- コード生成: MyBatis Generator
### 共通設計方針
- 主キーはすべて `BIGSERIAL` の自動採番
- すべてのテーブルに `created_at` を付与
- 更新が発生するテーブルには `updated_at` を付与
- 外部キー制約を設定し、データ整合性を担保
- インデックスは検索頻度の高いカラムに設定
### テーブル一覧
| No | 論理テーブル名 | 物理テーブル名 | ER図該当セクション |
| --- | --- | --- | --- |
| 1 | [ユーザー情報](#users) | users | ユーザー・権限 |
| 2 | [共有範囲グループ](#sharing_groups) | sharing_groups | ユーザー・権限 |
| 3 | [共有グループメンバー](#sharing_group_members) | sharing_group_members | ユーザー・権限 |
| 4 | [写真・動画](#media) | media | 写真・動画 |
| 5 | [写真・動画へのコメント](#media_comments) | media_comments | 写真・動画 |
| 6 | [タグ](#tags) | tags | 写真・動画 |
| 7 | [メディアとタグの中間テーブル](#media_tags) | media_tags | 写真・動画 |
| 8 | [お気に入り](#favorites) | favorites | 写真・動画 |
| 9 | [アルバム](#albums) | albums | 写真・動画 |
| 10 | [育児記録](#care_records) | care_records | 育児記録 |
| 11 | [食事記録の詳細](#meal_details) | meal_details | 育児記録 |
| 12 | [ミルク記録の詳細](#milk_details) | milk_details | 育児記録 |
| 13 | [排泄記録の詳細](#diaper_details) | diaper_details | 育児記録 |
| 14 | [体調記録の詳細](#health_details) | health_details | 育児記録 |
| 15 | [身長・体重記録](#growth_records) | growth_records | 育児記録 |
| 16 | [はじめて記録](#first_records) | first_records | はじめて・ことば |
| 17 | [ことばの記録](#word_records) | word_records | はじめて・ことば |
| 18 | [はじめて記録の写真・動画](#first_record_media) | first_record_media | はじめて・ことば |
| 19 | [ことば記録の写真・動画](#word_record_media) | word_record_media | はじめて・ことば |
| 20 | [ゴミ箱](#trash_items) | trash_items | ゴミ箱 |
## 各テーブル定義
### ユーザー・権限
#### users
##### 概要
**論理テーブル名** : ユーザー情報
**目的** : Cognitoと連携したユーザー情報と権限管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| cognito_user_id | VARCHAR(255) | NO | - | UK | Cognito ユーザーID |
| display_name | VARCHAR(100) | NO | - | | 表示名 |
| email | VARCHAR(255) | NO | - | UK | メールアドレス |
| user_icon_key | VARCHAR(500) | YES | NULL | | プロフィール画像URL |
| is_dark_mode | BOOLEAN | NO | FALSE | | ダークモードフラグ |
| is_easy_mode | BOOLEAN | NO | FALSE | | かんたんモードフラグ |
| role | VARCHAR(20) | NO | 'VIEWER' | | 権限ロール ADMIN:管理者 VIEWER:閲覧者 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### sharing_groups
##### 概要
**論理テーブル名** : 共有範囲グループ
**目的** : 写真・動画の共有範囲に対応するグループ定義
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| name | VARCHAR(100) | NO | - | UK | グループ名 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### sharing_group_members
##### 概要
**論理テーブル名** : 共有グループメンバー
**目的** : 共有グループと所属ユーザーの関連付け
想定ユーザーは少ないが、`sharing_groups`直保存に比べ外部キー制約でユーザーデータとの整合性を担保するため
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| sharing_group_id | BIGINT | NO | - | FK | 共有グループID |
| user_id | BIGINT | NO | - | FK | ユーザーID |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| sharing_group_id, user_id | UNIQUE |
| user_id | INDEX |
### 写真・動画
#### media
##### 概要
**論理テーブル名** : 写真・動画
**目的** : 写真・動画ファイルのメタデータと共有設定
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| uploaded_by | BIGINT | NO | - | FK | アップロードしたユーザーID |
| media_type | VARCHAR(10) | NO | - | | PHOTO:写真 VIDEO:動画 |
| original_filename | VARCHAR(255) | NO | - | | 元のファイル名 |
| content_type | VARCHAR(100) | NO | - | | MIMEタイプ |
| file_size | BIGINT | NO | - | | ファイルサイズ（バイト） |
| width | INTEGER | YES | NULL | | 幅（ピクセル） |
| height | INTEGER | YES | NULL | | 高さ（ピクセル） |
| s3_key | VARCHAR(500) | NO | - | UK | S3オブジェクトキー |
| thumbnail_s3_key | VARCHAR(500) | YES | NULL | UK | サムネイルのS3キー |
| taken_at | DATE | YES | NULL | | 撮影日 |
| album_id | BIGINT | YES | NULL | FK | 所属アルバムID |
| sharing_group_id | BIGINT | YES | NULL | FK | 共有範囲グループID |
| upload_status | VARCHAR(15) | NO | PROCESSING | | アップロード状況 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| uploaded_by | INDEX |
| taken_at | INDEX |
| sharing_group_id | INDEX |
| media_type | INDEX |
| album_id | INDEX |
#### media_comments
##### 概要
**論理テーブル名** : 写真・動画へのコメント
**目的** : 写真・動画に対するコメントデータ
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| media_id | BIGINT | NO | - | FK | メディアID |
| user_id | BIGINT | NO | - | FK | コメントしたユーザーID |
| content | TEXT | NO | - | | コメント本文 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| media_id | INDEX |
| user_id | INDEX |
#### tags
##### 概要
**論理テーブル名** : タグ
**目的** : メディア分類用のタグ
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| name | VARCHAR(100) | NO | - | UK | タグ名 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
#### media_tags
##### 概要
**論理テーブル名** : メディアとタグの中間テーブル
**目的** : メディアとタグの多対多関連付け
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| media_id | BIGINT | NO | - | FK | メディアID |
| tag_id | BIGINT | NO | - | FK | タグID |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| media_id, tag_id | UNIQUE |
| tag_id | INDEX |
#### favorites
##### 概要
**論理テーブル名** : お気に入り
**目的** : ユーザーがお気に入りしたメディアの管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| user_id | BIGINT | NO | - | FK | ユーザーID |
| media_id | BIGINT | NO | - | FK | メディアID |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| user_id, media_id | UNIQUE |
| media_id | INDEX |
#### albums
##### 概要
**論理テーブル名** : アルバム
**目的** : 1対多のアルバム管理用テーブル
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| title | VARCHAR(200) | NO | - | UK | アルバムタイトル |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
### 育児記録
#### care_records
##### 概要
**論理テーブル名** : 育児記録
**目的** : 育児データの統合管理テーブル
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| recorded_by | BIGINT | NO | - | FK | 記録者のユーザーID |
| record_type | VARCHAR(20) | NO | - | | MEAL:食事 MILK:ミルク DIAPER:排泄 HEALTH:体調 |
| recorded_at | TIMESTAMPTZ | NO | - | | 記録日時 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| recorded_at | INDEX |
| record_type | INDEX |
| recorded_by | INDEX |
#### meal_details
##### 概要
**論理テーブル名** : 食事記録の詳細
**目的** : 育児記録の食事タイプの詳細情報管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| care_record_id | BIGINT | NO | - | FK, UK | 育児記録ID |
| note | TEXT | YES | NULL | | 食事内容・メモ |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### milk_details
##### 概要
**論理テーブル名** : ミルク記録の詳細
**目的** : 育児記録のミルクタイプの詳細情報管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| care_record_id | BIGINT | NO | - | FK, UK | 育児記録ID |
| amount_ml | INTEGER | NO | NULL | | 量（ml） |
| note | TEXT | YES | NULL | | メモ・備考 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### diaper_details
##### 概要
**論理テーブル名** : 排泄記録の詳細
**目的** : 育児記録の排泄タイプの詳細情報管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| care_record_id | BIGINT | NO | - | FK, UK | 育児記録ID |
| diaper_type | VARCHAR(20) | NO | - | | WET:おしっこ DIRTY:うんち |
| note | TEXT | YES | NULL | | メモ・備考 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### health_details
##### 概要
**論理テーブル名** : 体調記録の詳細
**目的** : 育児記録の体調タイプの詳細情報管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| care_record_id | BIGINT | NO | - | FK, UK | 育児記録ID |
| temperature | DECIMAL(4,1) | YES | NULL | | 体温 |
| note | TEXT | YES | NULL | | メモ・備考 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
#### growth_records
##### 概要
**論理テーブル名** : 身長・体重記録
**目的** : 子供の成長推移を記録管理
低頻度記録でありタイムライン表示をさせないため他記録と独立させつつ記録ユーザー管理も除外
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| measurement_date | DATE | NO | - | | 測定日 |
| height | DECIMAL(5,1) | YES | - | | 身長（cm） |
| weight | DECIMAL(5,2) | YES | - | | 体重（kg） |
| note | TEXT | YES | NULL | | メモ |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| measurement_date | INDEX |
### はじめて・ことば
#### first_records
##### 概要
**論理テーブル名** : はじめて記録
**目的** : 子供の成長段階における初めての出来事を記録
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| title | VARCHAR(100) | NO | - | | タイトル |
| recorded_date | DATE | NO | - | | 達成日 |
| comment | TEXT | YES | NULL | | コメント |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| recorded_date | INDEX |
#### word_records
##### 概要
**論理テーブル名** : ことばの記録
**目的** : 子供が発した初めてのことばを記録管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| title | VARCHAR(100) | NO | - | | ことば |
| recorded_date | DATE | NO | - | | 記録日 |
| comment | TEXT | YES | NULL | | コメント |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 更新日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| recorded_date | INDEX |
#### first_record_media
##### 概要
**論理テーブル名** : はじめて記録の写真・動画
**目的** : はじめて記録に紐付いた写真・動画の管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| first_record_id | BIGINT | NO | - | FK | はじめて記録ID |
| media_id | BIGINT | NO | - | FK | メディアID |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| first_record_id, media_id | UNIQUE |
#### word_record_media
##### 概要
**論理テーブル名** : ことば記録の写真・動画
**目的** : ことば記録に紐付いた写真・動画の管理
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| word_record_id | BIGINT | NO | - | FK | ことば記録ID |
| media_id | BIGINT | NO | - | FK | メディアID |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| word_record_id, media_id | UNIQUE |
### ゴミ箱
#### trash_items
##### 概要
**論理テーブル名** : ゴミ箱
**目的** : 論理削除されたメディアの30日保持管理と完全削除スケジューリング
##### テーブル構造
| カラム名 | データ型 | NULL | デフォルト | キー | 説明 |
| --- | --- | --- | --- | --- | --- |
| id | BIGSERIAL | NO | AUTO | PK | 主キー |
| media_id | BIGINT | NO | - | FK, UK | メディアID |
| expires_at | TIMESTAMPTZ | NO | - | | 完全削除予定日時 |
| created_at | TIMESTAMPTZ | NO | CURRENT_TIMESTAMP | | 作成日時 |
##### インデックス/複合ユニーク制約
| カラム | 種別 |
| --- | --- |
| expires_at | INDEX |


## その他設計ドキュメント
- [アーキテクチャ設計書](./architecture.md)
- [ER図](./er-diagram.md)
- [画面設計書](./screen-spec.md)
- [API仕様](./openapi.yaml)
- [シーケンス図](./sequence.md)
