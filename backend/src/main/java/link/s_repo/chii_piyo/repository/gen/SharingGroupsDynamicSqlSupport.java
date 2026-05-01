package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class SharingGroupsDynamicSqlSupport {
    public static final SharingGroups sharingGroups = new SharingGroups();

    public static final SqlColumn<Long> id = sharingGroups.id;

    public static final SqlColumn<String> name = sharingGroups.name;

    public static final SqlColumn<LocalDateTime> createdAt = sharingGroups.createdAt;

    public static final SqlColumn<LocalDateTime> updatedAt = sharingGroups.updatedAt;

    public static final class SharingGroups extends AliasableSqlTable<SharingGroups> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> name = column("\"name\"", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public SharingGroups() {
            super("public.sharing_groups", SharingGroups::new);
        }
    }
}