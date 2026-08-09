package link.s_repo.chii_piyo.security;

import link.s_repo.chii_piyo.component.UserSyncComponent;
import link.s_repo.chii_piyo.model.gen.Users;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomJwtAuthenticationConverterTest {
    @Mock
    private UserSyncComponent userSyncComponent;

    @InjectMocks
    private CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    @Nested
    @DisplayName("convert - JWT認証の変換処理")
    class Convert {
        @Test
        @DisplayName("Auth-04: JWTから情報を取得して認証情報を返せること")
        void convert_success() {
            // JWTモックデータの作成
            String mockCognitoUserId = "sub-user-123";
            String mockEmail = "test@example.com";
            Jwt mockJwt = Jwt.withTokenValue("mock-token-value")
                .header("alg", "RS256")
                .subject(mockCognitoUserId)
                .claim("email", mockEmail)
                .build();

            // ユーザーモックデータの作成
            Users mockUser = new Users();
            String mockRole = "ADMIN";
            mockUser.setRole(mockRole);

            // 取得処理のスタブ化
            when(userSyncComponent.findOrCreateByCognitoUserId(mockCognitoUserId, mockEmail))
                .thenReturn(mockUser);

            // 対象の実行
            AbstractAuthenticationToken result = customJwtAuthenticationConverter.convert(mockJwt);

            // 渡したIDと権限で生成されているか検証
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(mockCognitoUserId);
            assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_" + mockRole);

            // ユーザー取得処理が呼び出されているか確認
            verify(userSyncComponent).findOrCreateByCognitoUserId(mockCognitoUserId, mockEmail);
        }

        @Test
        @DisplayName("Auth-05: subクレームが空の場合例外で処理されること")
        void convert_noSub() {
            // JWTモックデータの作成
            String mockEmail = "test@example.com";
            Jwt mockJwt = Jwt.withTokenValue("mock-token-value")
                .header("alg", "RS256")
                .claim("email", mockEmail)
                .build();

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> customJwtAuthenticationConverter.convert(mockJwt))
                .isInstanceOf(BadJwtException.class)
                .hasMessage("IDトークンにsubクレームが含まれていません");

            // ユーザー取得処理が呼び出されていないことを確認
            verify(userSyncComponent, never()).findOrCreateByCognitoUserId(any(), any());
        }

        @Test
        @DisplayName("Auth-06: emailが空の場合例外で処理されること")
        void convert_noMail() {
            // JWTモックデータの作成
            String mockCognitoUserId = "sub-user-123";
            Jwt mockJwt = Jwt.withTokenValue("mock-token-value")
                .header("alg", "RS256")
                .subject(mockCognitoUserId)
                .build();

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> customJwtAuthenticationConverter.convert(mockJwt))
                .isInstanceOf(BadJwtException.class)
                .hasMessage("IDトークンにemailクレームが含まれていません");

            // ユーザー取得処理が呼び出されていないことを確認
            verify(userSyncComponent, never()).findOrCreateByCognitoUserId(any(), any());
        }
    }
}
