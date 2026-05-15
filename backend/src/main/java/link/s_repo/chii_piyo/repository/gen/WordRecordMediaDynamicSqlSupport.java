package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class WordRecordMediaDynamicSqlSupport {
    public static final WordRecordMedia wordRecordMedia = new WordRecordMedia();

    public static final SqlColumn<Long> id = wordRecordMedia.id;

    public static final SqlColumn<Long> wordRecordId = wordRecordMedia.wordRecordId;

    public static final SqlColumn<Long> mediaId = wordRecordMedia.mediaId;

    public static final SqlColumn<OffsetDateTime> createdAt = wordRecordMedia.createdAt;

    public static final class WordRecordMedia extends AliasableSqlTable<WordRecordMedia> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> wordRecordId = column("word_record_id", JDBCType.BIGINT).withJavaProperty("wordRecordId");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public WordRecordMedia() {
            super("public.word_record_media", WordRecordMedia::new);
        }
    }
}