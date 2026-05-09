package link.s_repo.chii_piyo.security;

import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.gen.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import static link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport.cognitoUserId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UsersMapper usersMapper;

    /**
     * SecurityContextHolder からJWT認証情報を取り出し<br>
     * Cognito subクレームをキーにDBユーザーを検索してアプリケーション側のユーザーIDを返す
     *
     * @return アプリケーション側のユーザーID
     * @throws IllegalStateException 認証情報が存在しない、またはJwtAuthenticationToken以外の場合
     */
    public Long getUserId() {
        // 現在の認証情報はSecurityContextHolderが保有しているのでそこから取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("認証情報が取得できません");
        }

        Jwt jwt = jwtAuth.getToken();
        // CognitoのユーザーIDはsubクレームに入っているのでそこから取得
        String cognitoSub = jwt.getSubject();

        // DBからユーザーを取得してIDを返す。ユーザーが見つからない場合は例外をスロー
        Users user = usersMapper.selectOne(c -> c.where(cognitoUserId, isEqualTo(cognitoSub)))
            .orElseThrow(() -> new IllegalStateException(
                "ユーザーが見つかりません"
            ));
        return user.getId();
    }
}
