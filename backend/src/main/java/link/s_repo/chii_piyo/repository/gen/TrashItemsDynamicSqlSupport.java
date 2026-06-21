package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TrashItemsDynamicSqlSupport {
    public static final TrashItems trashItems = new TrashItems();

    public static final SqlColumn<Long> id = trashItems.id;

    public static final SqlColumn<Long> mediaId = trashItems.mediaId;

    public static final SqlColumn<OffsetDateTime> expiresAt = trashItems.expiresAt;

    public static final SqlColumn<OffsetDateTime> createdAt = trashItems.createdAt;

    public static final class TrashItems extends AliasableSqlTable<TrashItems> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<OffsetDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public TrashItems() {
            super("public.trash_items", TrashItems::new);
        }
    }
}