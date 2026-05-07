package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class DiaperDetailsDynamicSqlSupport {
    public static final DiaperDetails diaperDetails = new DiaperDetails();

    public static final SqlColumn<Long> id = diaperDetails.id;

    public static final SqlColumn<Long> careRecordId = diaperDetails.careRecordId;

    public static final SqlColumn<String> diaperType = diaperDetails.diaperType;

    public static final SqlColumn<String> note = diaperDetails.note;

    public static final SqlColumn<OffsetDateTime> createdAt = diaperDetails.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = diaperDetails.updatedAt;

    public static final class DiaperDetails extends AliasableSqlTable<DiaperDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> careRecordId = column("care_record_id", JDBCType.BIGINT).withJavaProperty("careRecordId");

        public final SqlColumn<String> diaperType = column("diaper_type", JDBCType.VARCHAR).withJavaProperty("diaperType");

        public final SqlColumn<String> note = column("note", JDBCType.VARCHAR).withJavaProperty("note");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public DiaperDetails() {
            super("public.diaper_details", DiaperDetails::new);
        }
    }
}