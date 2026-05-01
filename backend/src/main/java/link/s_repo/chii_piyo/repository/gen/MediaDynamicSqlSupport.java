package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class MediaDynamicSqlSupport {
    public static final Media media = new Media();

    public static final SqlColumn<Long> id = media.id;

    public static final SqlColumn<Long> uploadedBy = media.uploadedBy;

    public static final SqlColumn<String> mediaType = media.mediaType;

    public static final SqlColumn<String> originalFilename = media.originalFilename;

    public static final SqlColumn<String> contentType = media.contentType;

    public static final SqlColumn<Long> fileSize = media.fileSize;

    public static final SqlColumn<Integer> width = media.width;

    public static final SqlColumn<Integer> height = media.height;

    public static final SqlColumn<String> s3Key = media.s3Key;

    public static final SqlColumn<String> thumbnailS3Key = media.thumbnailS3Key;

    public static final SqlColumn<LocalDate> takenAt = media.takenAt;

    public static final SqlColumn<Long> albumId = media.albumId;

    public static final SqlColumn<Long> sharingGroupId = media.sharingGroupId;

    public static final SqlColumn<String> uploadStatus = media.uploadStatus;

    public static final SqlColumn<LocalDateTime> createdAt = media.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = media.updatedAt;

    public static final class Media extends AliasableSqlTable<Media> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> uploadedBy = column("uploaded_by", JDBCType.BIGINT).withJavaProperty("uploadedBy");

        public final SqlColumn<String> mediaType = column("media_type", JDBCType.VARCHAR).withJavaProperty("mediaType");

        public final SqlColumn<String> originalFilename = column("original_filename", JDBCType.VARCHAR).withJavaProperty("originalFilename");

        public final SqlColumn<String> contentType = column("content_type", JDBCType.VARCHAR).withJavaProperty("contentType");

        public final SqlColumn<Long> fileSize = column("file_size", JDBCType.BIGINT).withJavaProperty("fileSize");

        public final SqlColumn<Integer> width = column("width", JDBCType.INTEGER).withJavaProperty("width");

        public final SqlColumn<Integer> height = column("height", JDBCType.INTEGER).withJavaProperty("height");

        public final SqlColumn<String> s3Key = column("s3_key", JDBCType.VARCHAR).withJavaProperty("s3Key");

        public final SqlColumn<String> thumbnailS3Key = column("thumbnail_s3_key", JDBCType.VARCHAR).withJavaProperty("thumbnailS3Key");

        public final SqlColumn<LocalDate> takenAt = column("taken_at", JDBCType.DATE).withJavaProperty("takenAt");

        public final SqlColumn<Long> albumId = column("album_id", JDBCType.BIGINT).withJavaProperty("albumId");

        public final SqlColumn<Long> sharingGroupId = column("sharing_group_id", JDBCType.BIGINT).withJavaProperty("sharingGroupId");

        public final SqlColumn<String> uploadStatus = column("upload_status", JDBCType.VARCHAR).withJavaProperty("uploadStatus");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public Media() {
            super("public.media", Media::new);
        }
    }
}