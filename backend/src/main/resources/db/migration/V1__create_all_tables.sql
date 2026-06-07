-- ユーザー情報
CREATE TABLE users
(
    id              BIGSERIAL PRIMARY KEY,
    cognito_user_id VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    user_icon_key   VARCHAR(500),
    is_dark_mode    BOOLEAN      NOT NULL DEFAULT FALSE,
    is_easy_mode    BOOLEAN      NOT NULL DEFAULT FALSE,
    role            VARCHAR(20)  NOT NULL DEFAULT 'VIEWER',
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_cognito_user_id UNIQUE (cognito_user_id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- 共有範囲グループ
CREATE TABLE sharing_groups
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sharing_groups_name UNIQUE (name)
);

-- 共有範囲グループメンバー
CREATE TABLE sharing_group_members
(
    id               BIGSERIAL PRIMARY KEY,
    sharing_group_id BIGINT      NOT NULL,
    user_id          BIGINT      NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_sharing_group_members UNIQUE (sharing_group_id, user_id),
    CONSTRAINT fk_sharing_group_members_sharing_group FOREIGN KEY (sharing_group_id) REFERENCES sharing_groups (id),
    CONSTRAINT fk_sharing_group_members_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_sharing_group_members_user ON sharing_group_members (user_id);

-- アルバム
-- ※mediaテーブルが参照するため先に作成する
CREATE TABLE albums
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_albums_title UNIQUE (title)
);

-- 写真・動画
CREATE TABLE media
(
    id                BIGSERIAL PRIMARY KEY,
    uploaded_by       BIGINT       NOT NULL,
    media_type        VARCHAR(10)  NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type      VARCHAR(100) NOT NULL,
    file_size         BIGINT       NOT NULL,
    width             INTEGER,
    height            INTEGER,
    s3_key            VARCHAR(500) NOT NULL,
    thumbnail_s3_key  VARCHAR(500),
    taken_at          DATE,
    album_id          BIGINT,
    sharing_group_id  BIGINT,
    upload_status     VARCHAR(15)  NOT NULL DEFAULT 'PROCESSING',
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_media_s3_key UNIQUE (s3_key),
    CONSTRAINT uk_media_thumbnail_s3_key UNIQUE (thumbnail_s3_key),
    CONSTRAINT fk_media_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users (id),
    CONSTRAINT fk_media_album FOREIGN KEY (album_id) REFERENCES albums (id),
    CONSTRAINT fk_media_sharing_group FOREIGN KEY (sharing_group_id) REFERENCES sharing_groups (id)
);

CREATE INDEX idx_media_uploaded_by ON media (uploaded_by);
CREATE INDEX idx_media_taken_at ON media (taken_at);
CREATE INDEX idx_media_sharing_group_id ON media (sharing_group_id);
CREATE INDEX idx_media_media_type ON media (media_type);
CREATE INDEX idx_media_album_id ON media (album_id);

-- 写真・動画へのコメント
CREATE TABLE media_comments
(
    id         BIGSERIAL PRIMARY KEY,
    media_id   BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    content    TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_media_comments_media FOREIGN KEY (media_id) REFERENCES media (id),
    CONSTRAINT fk_media_comments_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_media_comments_media_id ON media_comments (media_id);
CREATE INDEX idx_media_comments_user_id ON media_comments (user_id);

-- タグ
CREATE TABLE tags
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tags_name UNIQUE (name)
);

-- メディアとタグの中間テーブル
CREATE TABLE media_tags
(
    id         BIGSERIAL PRIMARY KEY,
    media_id   BIGINT      NOT NULL,
    tag_id     BIGINT      NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_media_tags UNIQUE (media_id, tag_id),
    CONSTRAINT fk_media_tags_media FOREIGN KEY (media_id) REFERENCES media (id),
    CONSTRAINT fk_media_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE INDEX idx_media_tags_tag_id ON media_tags (tag_id);

-- お気に入り
CREATE TABLE favorites
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    media_id   BIGINT      NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_favorites UNIQUE (user_id, media_id),
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_favorites_media FOREIGN KEY (media_id) REFERENCES media (id)
);

CREATE INDEX idx_favorites_media_id ON favorites (media_id);

-- 育児記録
CREATE TABLE care_records
(
    id          BIGSERIAL PRIMARY KEY,
    recorded_by BIGINT      NOT NULL,
    record_type VARCHAR(20) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_care_records_user FOREIGN KEY (recorded_by) REFERENCES users (id)
);

CREATE INDEX idx_care_records_recorded_at ON care_records (recorded_at);
CREATE INDEX idx_care_records_record_type ON care_records (record_type);
CREATE INDEX idx_care_records_recorded_by ON care_records (recorded_by);

-- 食事記録の詳細
CREATE TABLE meal_details
(
    id             BIGSERIAL PRIMARY KEY,
    care_record_id BIGINT      NOT NULL,
    note           TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_meal_details_care_record_id UNIQUE (care_record_id),
    CONSTRAINT fk_meal_details_care_record FOREIGN KEY (care_record_id) REFERENCES care_records (id)
);

-- ミルク記録の詳細
CREATE TABLE milk_details
(
    id             BIGSERIAL PRIMARY KEY,
    care_record_id BIGINT      NOT NULL,
    amount_ml      INTEGER,
    note           TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_milk_details_care_record_id UNIQUE (care_record_id),
    CONSTRAINT fk_milk_details_care_record FOREIGN KEY (care_record_id) REFERENCES care_records (id)
);

-- 排泄記録の詳細
CREATE TABLE diaper_details
(
    id             BIGSERIAL PRIMARY KEY,
    care_record_id BIGINT      NOT NULL,
    diaper_type    VARCHAR(20) NOT NULL,
    note           TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_diaper_details_care_record_id UNIQUE (care_record_id),
    CONSTRAINT fk_diaper_details_care_record FOREIGN KEY (care_record_id) REFERENCES care_records (id)
);

-- 体調記録の詳細
CREATE TABLE health_details
(
    id             BIGSERIAL PRIMARY KEY,
    care_record_id BIGINT      NOT NULL,
    temperature    DECIMAL(4, 1),
    note           TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_health_details_care_record_id UNIQUE (care_record_id),
    CONSTRAINT fk_health_details_care_record FOREIGN KEY (care_record_id) REFERENCES care_records (id)
);

-- 身長・体重記録
CREATE TABLE growth_records
(
    id               BIGSERIAL PRIMARY KEY,
    measurement_date DATE          NOT NULL,
    height           DECIMAL(5, 1) NOT NULL,
    weight           DECIMAL(5, 2) NOT NULL,
    note             TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_growth_records_measurement_date ON growth_records (measurement_date);

-- はじめて記録
CREATE TABLE first_records
(
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    achieved_date DATE         NOT NULL,
    comment       TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_first_records_achieved_date ON first_records (achieved_date);

-- ことば記録
CREATE TABLE word_records
(
    id            BIGSERIAL PRIMARY KEY,
    word          VARCHAR(100) NOT NULL,
    recorded_date DATE         NOT NULL,
    comment       TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_word_records_recorded_date ON word_records (recorded_date);

-- はじめて記録の写真・動画
CREATE TABLE first_record_media
(
    id              BIGSERIAL PRIMARY KEY,
    first_record_id BIGINT      NOT NULL,
    media_id        BIGINT      NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_first_record_media UNIQUE (first_record_id, media_id),
    CONSTRAINT fk_first_record_media_first_record FOREIGN KEY (first_record_id) REFERENCES first_records (id),
    CONSTRAINT fk_first_record_media_media FOREIGN KEY (media_id) REFERENCES media (id)
);

-- ことば記録の写真・動画
CREATE TABLE word_record_media
(
    id             BIGSERIAL PRIMARY KEY,
    word_record_id BIGINT      NOT NULL,
    media_id       BIGINT      NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_word_record_media UNIQUE (word_record_id, media_id),
    CONSTRAINT fk_word_record_media_word_record FOREIGN KEY (word_record_id) REFERENCES word_records (id),
    CONSTRAINT fk_word_record_media_media FOREIGN KEY (media_id) REFERENCES media (id)
);

-- ゴミ箱
CREATE TABLE trash_items
(
    id         BIGSERIAL PRIMARY KEY,
    media_id   BIGINT      NOT NULL,
    deleted_by BIGINT      NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_trash_items_media_id UNIQUE (media_id),
    CONSTRAINT fk_trash_items_media FOREIGN KEY (media_id) REFERENCES media (id),
    CONSTRAINT fk_trash_items_user FOREIGN KEY (deleted_by) REFERENCES users (id)
);

CREATE INDEX idx_trash_items_expires_at ON trash_items (expires_at);
