package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class MediaCommentsDynamicSqlSupport {
    public static final MediaComments mediaComments = new MediaComments();

    public static final SqlColumn<Long> id = mediaComments.id;

    public static final SqlColumn<Long> mediaId = mediaComments.mediaId;

    public static final SqlColumn<Long> userId = mediaComments.userId;

    public static final SqlColumn<String> content = mediaComments.content;

    public static final SqlColumn<OffsetDateTime> createdAt = mediaComments.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = mediaComments.updatedAt;

    public static final class MediaComments extends AliasableSqlTable<MediaComments> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT).withJavaProperty("userId");

        public final SqlColumn<String> content = column("content", JDBCType.VARCHAR).withJavaProperty("content");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public MediaComments() {
            super("public.media_comments", MediaComments::new);
        }
    }
}