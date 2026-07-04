package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class WordRecordsDynamicSqlSupport {
    public static final WordRecords wordRecords = new WordRecords();

    public static final SqlColumn<Long> id = wordRecords.id;

    public static final SqlColumn<String> title = wordRecords.title;

    public static final SqlColumn<LocalDate> recordedDate = wordRecords.recordedDate;

    public static final SqlColumn<String> comment = wordRecords.comment;

    public static final SqlColumn<OffsetDateTime> createdAt = wordRecords.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = wordRecords.updatedAt;

    public static final class WordRecords extends AliasableSqlTable<WordRecords> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<LocalDate> recordedDate = column("recorded_date", JDBCType.DATE).withJavaProperty("recordedDate");

        public final SqlColumn<String> comment = column("\"comment\"", JDBCType.VARCHAR).withJavaProperty("comment");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public WordRecords() {
            super("public.word_records", WordRecords::new);
        }
    }
}