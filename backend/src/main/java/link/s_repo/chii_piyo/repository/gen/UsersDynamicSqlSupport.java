package link.s_repo.chii_piyo.repository.gen;

import java.sql.JDBCType;
import java.time.OffsetDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class UsersDynamicSqlSupport {
    public static final Users users = new Users();

    public static final SqlColumn<Long> id = users.id;

    public static final SqlColumn<String> cognitoUserId = users.cognitoUserId;

    public static final SqlColumn<String> displayName = users.displayName;

    public static final SqlColumn<String> email = users.email;

    public static final SqlColumn<String> userIconUrl = users.userIconUrl;

    public static final SqlColumn<Boolean> isDarkMode = users.isDarkMode;

    public static final SqlColumn<Boolean> isEasyMode = users.isEasyMode;

    public static final SqlColumn<String> role = users.role;

    public static final SqlColumn<OffsetDateTime> createdAt = users.createdAt;

    public static final SqlColumn<OffsetDateTime> updatedAt = users.updatedAt;

    public static final class Users extends AliasableSqlTable<Users> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> cognitoUserId = column("cognito_user_id", JDBCType.VARCHAR).withJavaProperty("cognitoUserId");

        public final SqlColumn<String> displayName = column("display_name", JDBCType.VARCHAR).withJavaProperty("displayName");

        public final SqlColumn<String> email = column("email", JDBCType.VARCHAR).withJavaProperty("email");

        public final SqlColumn<String> userIconUrl = column("user_icon_url", JDBCType.VARCHAR).withJavaProperty("userIconUrl");

        public final SqlColumn<Boolean> isDarkMode = column("is_dark_mode", JDBCType.BIT).withJavaProperty("isDarkMode");

        public final SqlColumn<Boolean> isEasyMode = column("is_easy_mode", JDBCType.BIT).withJavaProperty("isEasyMode");

        public final SqlColumn<String> role = column("\"role\"", JDBCType.VARCHAR).withJavaProperty("role");

        public final SqlColumn<OffsetDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<OffsetDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public Users() {
            super("public.users", Users::new);
        }
    }
}