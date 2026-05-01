package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class MealDetailsDynamicSqlSupport {
    public static final MealDetails mealDetails = new MealDetails();

    public static final SqlColumn<Long> id = mealDetails.id;

    public static final SqlColumn<Long> careRecordId = mealDetails.careRecordId;

    public static final SqlColumn<String> note = mealDetails.note;

    public static final SqlColumn<LocalDateTime> createdAt = mealDetails.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = mealDetails.updatedAt;

    public static final class MealDetails extends AliasableSqlTable<MealDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> careRecordId = column("care_record_id", JDBCType.BIGINT).withJavaProperty("careRecordId");

        public final SqlColumn<String> note = column("note", JDBCType.VARCHAR).withJavaProperty("note");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public MealDetails() {
            super("public.meal_details", MealDetails::new);
        }
    }
}