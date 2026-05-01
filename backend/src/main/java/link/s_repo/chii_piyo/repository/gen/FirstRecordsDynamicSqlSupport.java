package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class FirstRecordsDynamicSqlSupport {
    public static final FirstRecords firstRecords = new FirstRecords();

    public static final SqlColumn<Long> id = firstRecords.id;

    public static final SqlColumn<String> title = firstRecords.title;

    public static final SqlColumn<LocalDate> achievedDate = firstRecords.achievedDate;

    public static final SqlColumn<String> comment = firstRecords.comment;

    public static final SqlColumn<LocalDateTime> createdAt = firstRecords.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = firstRecords.updatedAt;

    public static final class FirstRecords extends AliasableSqlTable<FirstRecords> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<LocalDate> achievedDate = column("achieved_date", JDBCType.DATE).withJavaProperty("achievedDate");

        public final SqlColumn<String> comment = column("\"comment\"", JDBCType.VARCHAR).withJavaProperty("comment");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public FirstRecords() {
            super("public.first_records", FirstRecords::new);
        }
    }
}