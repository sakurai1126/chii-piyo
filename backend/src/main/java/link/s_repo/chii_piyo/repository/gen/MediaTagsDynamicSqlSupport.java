package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class MediaTagsDynamicSqlSupport {
    public static final MediaTags mediaTags = new MediaTags();

    public static final SqlColumn<Long> id = mediaTags.id;

    public static final SqlColumn<Long> mediaId = mediaTags.mediaId;

    public static final SqlColumn<Long> tagId = mediaTags.tagId;

    public static final SqlColumn<OffsetDateTime> createdAt = mediaTags.createdAt;

    public static final class MediaTags extends AliasableSqlTable<MediaTags> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<Long> tagId = column("tag_id", JDBCType.BIGINT).withJavaProperty("tagId");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public MediaTags() {
            super("public.media_tags", MediaTags::new);
        }
    }
}