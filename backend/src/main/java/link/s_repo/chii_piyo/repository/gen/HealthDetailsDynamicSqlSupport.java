package link.s_repo.chii_piyo.repository.gen;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class HealthDetailsDynamicSqlSupport {
    public static final HealthDetails healthDetails = new HealthDetails();

    public static final SqlColumn<Long> id = healthDetails.id;

    public static final SqlColumn<Long> careRecordId = healthDetails.careRecordId;

    public static final SqlColumn<BigDecimal> temperature = healthDetails.temperature;

    public static final SqlColumn<String> note = healthDetails.note;

    public static final SqlColumn<LocalDateTime> createdAt = healthDetails.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = healthDetails.updatedAt;

    public static final class HealthDetails extends AliasableSqlTable<HealthDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> careRecordId = column("care_record_id", JDBCType.BIGINT).withJavaProperty("careRecordId");

        public final SqlColumn<BigDecimal> temperature = column("temperature", JDBCType.NUMERIC).withJavaProperty("temperature");

        public final SqlColumn<String> note = column("note", JDBCType.VARCHAR).withJavaProperty("note");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public HealthDetails() {
            super("public.health_details", HealthDetails::new);
        }
    }
}