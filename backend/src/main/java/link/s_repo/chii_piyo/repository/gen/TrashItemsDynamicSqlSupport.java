package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TrashItemsDynamicSqlSupport {
    public static final TrashItems trashItems = new TrashItems();

    public static final SqlColumn<Long> id = trashItems.id;

    public static final SqlColumn<Long> mediaId = trashItems.mediaId;

    public static final SqlColumn<Long> deletedBy = trashItems.deletedBy;

    public static final SqlColumn<LocalDateTime> expiresAt = trashItems.expiresAt;

    public static final SqlColumn<LocalDateTime> createdAt = trashItems.createdAt;

    public static final class TrashItems extends AliasableSqlTable<TrashItems> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> mediaId = column("media_id", JDBCType.BIGINT).withJavaProperty("mediaId");

        public final SqlColumn<Long> deletedBy = column("deleted_by", JDBCType.BIGINT).withJavaProperty("deletedBy");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public TrashItems() {
            super("public.trash_items", TrashItems::new);
        }
    }
}