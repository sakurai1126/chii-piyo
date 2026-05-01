package link.s_repo.chii_piyo.repository.gen;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class GrowthRecordsDynamicSqlSupport {
    public static final GrowthRecords growthRecords = new GrowthRecords();

    public static final SqlColumn<Long> id = growthRecords.id;

    public static final SqlColumn<LocalDate> measurementDate = growthRecords.measurementDate;

    public static final SqlColumn<BigDecimal> height = growthRecords.height;

    public static final SqlColumn<BigDecimal> weight = growthRecords.weight;

    public static final SqlColumn<String> note = growthRecords.note;

    public static final SqlColumn<LocalDateTime> createdAt = growthRecords.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = growthRecords.updatedAt;

    public static final class GrowthRecords extends AliasableSqlTable<GrowthRecords> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDate> measurementDate = column("measurement_date", JDBCType.DATE).withJavaProperty("measurementDate");

        public final SqlColumn<BigDecimal> height = column("height", JDBCType.NUMERIC).withJavaProperty("height");

        public final SqlColumn<BigDecimal> weight = column("weight", JDBCType.NUMERIC).withJavaProperty("weight");

        public final SqlColumn<String> note = column("note", JDBCType.VARCHAR).withJavaProperty("note");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public GrowthRecords() {
            super("public.growth_records", GrowthRecords::new);
        }
    }
}