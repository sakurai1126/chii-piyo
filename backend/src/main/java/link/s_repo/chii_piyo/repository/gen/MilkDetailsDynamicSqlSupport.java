package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class MilkDetailsDynamicSqlSupport {
    public static final MilkDetails milkDetails = new MilkDetails();

    public static final SqlColumn<Long> id = milkDetails.id;

    public static final SqlColumn<Long> careRecordId = milkDetails.careRecordId;

    public static final SqlColumn<Integer> amountMl = milkDetails.amountMl;

    public static final SqlColumn<String> note = milkDetails.note;

    public static final SqlColumn<OffsetDateTime> createdAt = milkDetails.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = milkDetails.updatedAt;

    public static final class MilkDetails extends AliasableSqlTable<MilkDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> careRecordId = column("care_record_id", JDBCType.BIGINT).withJavaProperty("careRecordId");

        public final SqlColumn<Integer> amountMl = column("amount_ml", JDBCType.INTEGER).withJavaProperty("amountMl");

        public final SqlColumn<String> note = column("note", JDBCType.VARCHAR).withJavaProperty("note");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public MilkDetails() {
            super("public.milk_details", MilkDetails::new);
        }
    }
}