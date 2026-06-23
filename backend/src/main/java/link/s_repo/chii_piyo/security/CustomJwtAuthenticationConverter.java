package link.s_repo.chii_piyo.security;

import link.s_repo.chii_piyo.component.UserSyncComponent;
import link.s_repo.chii_piyo.model.gen.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * JWT認証のカスタムコンバータ<br>
 * CognitoのJWTからユーザー情報を取り出し、DBからロールを取得してそれらを含めた認証情報オブジェクトを返却する
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserSyncComponent userSyncComponent;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        // CognitoのIDトークンからユーザーIDを取得 (CognitoはsubクレームにユーザーIDを入れる)
        String cognitoUserId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        if (email == null) {
            log.warn("emailクレームがJWTに含まれていません sub={}", cognitoUserId);
            throw new org.springframework.security.oauth2.jwt.BadJwtException(
                "IDトークンにemailクレームが含まれていません"
            );
        }

        // DBからユーザー情報を取得（存在しなければ作成）してロールを判定
        Users user = userSyncComponent.findOrCreateByCognitoUserId(cognitoUserId, email);

        // 権限情報を持つオブジェクトを作成する
        Collection<GrantedAuthority> authorities = List.of(
            // PreAuthorizeでROLE_を期待しているため"ROLE_ユーザーロール"の形式でSimpleGrantedAuthorityを作成
            new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );

        // JWTと権限情報、ユーザーIDを含めた認証情報オブジェクトを返す
        return new JwtAuthenticationToken(jwt, authorities, cognitoUserId);
    }
}
