package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AlbumsDynamicSqlSupport {
    public static final Albums albums = new Albums();

    public static final SqlColumn<Long> id = albums.id;

    public static final SqlColumn<String> title = albums.title;

    public static final SqlColumn<OffsetDateTime> createdAt = albums.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = albums.updatedAt;

    public static final class Albums extends AliasableSqlTable<Albums> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public Albums() {
            super("public.albums", Albums::new);
        }
    }
}