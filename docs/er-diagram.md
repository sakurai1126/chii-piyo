# ER図

## 概要

DB基本情報・共通設計方針は[テーブル定義書](table-definition.md)を参照

テーブル数が多いため、各セクションごとに分割して表示

### ユーザー・権限

```mermaid
erDiagram
    users {
        bigserial id PK
        varchar cognito_user_id UK
        varchar display_name
        varchar email UK
        varchar user_icon_key
        boolean is_dark_mode
        boolean is_easy_mode
        varchar role
        timestamptz created_at
        timestamptz updated_at
    }

    sharing_groups {
        bigserial id PK
        varchar name UK
        timestamptz created_at
        timestamptz updated_at
    }

    sharing_group_members {
        bigserial id PK
        bigint sharing_group_id FK
        bigint user_id FK
        timestamptz created_at
    }

    sharing_groups ||--|{ sharing_group_members : ""
    users ||--o{ sharing_group_members : ""
```

### 写真・動画

```mermaid
erDiagram
    media {
        bigserial id PK
        bigint uploaded_by FK
        varchar media_type
        varchar original_filename
        varchar content_type
        bigint file_size
        integer width
        integer height
        varchar s3_key UK
        varchar thumbnail_s3_key UK
        date taken_at
        bigint album_id FK
        bigint sharing_group_id FK
        varchar upload_status
        timestamptz created_at
        timestamptz updated_at
    }

    media_comments {
        bigserial id PK
        bigint media_id FK
        bigint user_id FK
        text content
        timestamptz created_at
        timestamptz updated_at
    }

    tags {
        bigserial id PK
        varchar name UK
        timestamptz created_at
    }

    media_tags {
        bigserial id PK
        bigint media_id FK
        bigint tag_id FK
        timestamptz created_at
    }

    favorites {
        bigserial id PK
        bigint user_id FK
        bigint media_id FK
        timestamptz created_at
    }

    albums {
        bigserial id PK
        varchar title UK
        timestamptz created_at
        timestamptz updated_at
    }


    media ||--o{ media_comments : ""
    media ||--o{ media_tags : ""
    media ||--o{ favorites : ""
    albums ||--o{ media : ""
    tags ||--o{ media_tags : ""
    users ||--o{ media : ""
    users ||--o{ media_comments : ""
    users ||--o{ favorites : ""
    sharing_groups ||--o{ media : ""
```

### 育児記録

```mermaid
erDiagram
    care_records {
        bigserial id PK
        bigint recorded_by FK
        varchar record_type
        timestamptz recorded_at
        timestamptz created_at
        timestamptz updated_at
    }

    meal_details {
        bigserial id PK
        bigint care_record_id FK, UK
        text note
        timestamptz created_at
        timestamptz updated_at
    }

    milk_details {
        bigserial id PK
        bigint care_record_id FK, UK
        integer amount_ml
        text note
        timestamptz created_at
        timestamptz updated_at
    }

    diaper_details {
        bigserial id PK
        bigint care_record_id FK, UK
        varchar diaper_type
        text note
        timestamptz created_at
        timestamptz updated_at
    }

    health_details {
        bigserial id PK
        bigint care_record_id FK, UK
        decimal temperature
        text note
        timestamptz created_at
        timestamptz updated_at
    }

    growth_records {
        bigserial id PK
        date measurement_date
        decimal height
        decimal weight
        text note
        timestamptz created_at
        timestamptz updated_at
    }

    care_records ||--o| meal_details : ""
    care_records ||--o| milk_details : ""
    care_records ||--o| diaper_details : ""
    care_records ||--o| health_details : ""
    users ||--o{ care_records : ""
```

### はじめて・ことば

```mermaid
erDiagram
    first_records {
        bigserial id PK
        varchar title
        date recorded_date
        text comment
        timestamptz created_at
        timestamptz updated_at
    }

    word_records {
        bigserial id PK
        varchar title
        date recorded_date
        text comment
        timestamptz created_at
        timestamptz updated_at
    }

    first_record_media {
        bigserial id PK
        bigint first_record_id FK
        bigint media_id FK
        timestamptz created_at
    }

    word_record_media {
        bigserial id PK
        bigint word_record_id FK
        bigint media_id FK
        timestamptz created_at
    }

    first_records ||--o{ first_record_media : ""
    word_records ||--o{ word_record_media : ""
    media ||--o{ first_record_media : ""
    media ||--o{ word_record_media : ""
```

### ゴミ箱

```mermaid
erDiagram
    trash_items {
        bigserial id PK
        bigint media_id FK, UK
        timestamptz expires_at
        timestamptz created_at
    }

    media ||--o| trash_items : ""
```

## その他設計ドキュメント
- [アーキテクチャ設計書](./architecture.md)
- [テーブル定義書](./table-definition.md)
- [画面設計書](./screen-spec.md)
- [API仕様](./openapi.yaml)
- [シーケンス図](./sequence.md)
