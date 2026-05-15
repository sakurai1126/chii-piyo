package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class SharingGroupMembersDynamicSqlSupport {
    public static final SharingGroupMembers sharingGroupMembers = new SharingGroupMembers();

    public static final SqlColumn<Long> id = sharingGroupMembers.id;

    public static final SqlColumn<Long> sharingGroupId = sharingGroupMembers.sharingGroupId;

    public static final SqlColumn<Long> userId = sharingGroupMembers.userId;

    public static final SqlColumn<OffsetDateTime> createdAt = sharingGroupMembers.createdAt;

    public static final class SharingGroupMembers extends AliasableSqlTable<SharingGroupMembers> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> sharingGroupId = column("sharing_group_id", JDBCType.BIGINT).withJavaProperty("sharingGroupId");

        public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT).withJavaProperty("userId");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public SharingGroupMembers() {
            super("public.sharing_group_members", SharingGroupMembers::new);
        }
    }
}