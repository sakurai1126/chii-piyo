package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CareRecordsDynamicSqlSupport {
    public static final CareRecords careRecords = new CareRecords();

    public static final SqlColumn<Long> id = careRecords.id;

    public static final SqlColumn<Long> recordedBy = careRecords.recordedBy;

    public static final SqlColumn<String> recordType = careRecords.recordType;

    public static final SqlColumn<OffsetDateTime> recordedAt = careRecords.recordedAt;

    public static final SqlColumn<OffsetDateTime> createdAt = careRecords.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = careRecords.updatedAt;

    public static final class CareRecords extends AliasableSqlTable<CareRecords> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> recordedBy = column("recorded_by", JDBCType.BIGINT).withJavaProperty("recordedBy");

        public final SqlColumn<String> recordType = column("record_type", JDBCType.VARCHAR).withJavaProperty("recordType");

        public final SqlColumn<OffsetDateTime> recordedAt = column("recorded_at", JDBCType.TIMESTAMP).withJavaProperty("recordedAt");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public CareRecords() {
            super("public.care_records", CareRecords::new);
        }
    }
}