package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class FirstRecordMediaDynamicSqlSupport {
    public static final FirstRecordMedia firstRecordMedia = new FirstRecordMedia();

    public static final SqlColumn<Long> id = firstRecordMedia.id;

    public static final SqlColumn<Long> firstRecordId = firstRecordMedia.firstRecordId;

    public static final SqlColumn<Long> mediaId = firstRecordMedia.mediaId;

    public static final SqlColumn<OffsetDateTime> createdAt = firstRecordMedia.createdAt;

    public static final class FirstRecordMedia extends AliasableSqlTable<FirstRecordMedia> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> firstRecordId = column("first_record_id", JDBCType.BIGINT).withJavaProperty("firstRecordId");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public FirstRecordMedia() {
            super("public.first_record_media", FirstRecordMedia::new);
        }
    }
}