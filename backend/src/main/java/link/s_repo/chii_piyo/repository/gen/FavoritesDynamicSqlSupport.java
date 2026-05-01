package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class FavoritesDynamicSqlSupport {
    public static final Favorites favorites = new Favorites();

    public static final SqlColumn<Long> id = favorites.id;

    public static final SqlColumn<Long> userId = favorites.userId;

    public static final SqlColumn<Long> mediaId = favorites.mediaId;

    public static final SqlColumn<LocalDateTime> createdAt = favorites.createdAt;

    public static final class Favorites extends AliasableSqlTable<Favorites> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT).withJavaProperty("userId");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public Favorites() {
            super("public.favorites", Favorites::new);
        }
    }
}