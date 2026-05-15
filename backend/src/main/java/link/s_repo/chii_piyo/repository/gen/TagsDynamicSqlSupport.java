package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TagsDynamicSqlSupport {
    public static final Tags tags = new Tags();

    public static final SqlColumn<Long> id = tags.id;

    public static final SqlColumn<String> name = tags.name;

    public static final SqlColumn<OffsetDateTime> createdAt = tags.createdAt;

    public static final class Tags extends AliasableSqlTable<Tags> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> name = column("\"name\"", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public Tags() {
            super("public.tags", Tags::new);
        }
    }
}