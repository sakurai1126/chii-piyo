package link.s_repo.chii_piyo.security;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrentUserProviderTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserProvider currentUserProvider;

    // 後処理でセットした認証情報をクリア
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getUserId - ログイン中のユーザー情報の取得")
    class GetUserId {
        @Test
        @DisplayName("Auth-07: ログイン中のユーザー情報の取得ができること")
        void getUserId_success() {
            // 認証情報のモックを作成
            String mockCognitoSub = "sub-123";
            Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .subject(mockCognitoSub)
                .build();

            JwtAuthenticationToken mockAuthToken =
                new JwtAuthenticationToken(jwt, List.of(), mockCognitoSub);

            // テスト用に作成した認証情報をSecurityContextHolderにセットする
            SecurityContextHolder.getContext().setAuthentication(mockAuthToken);

            // ユーザーモックデータの取得
            Users mockUser = new Users();
            Long mockUserId = 1L;
            mockUser.setId(mockUserId);

            // ユーザー取得のスタブ化
            when(userRepository.findByCognitoUserId(mockCognitoSub))
                .thenReturn(Optional.of(mockUser));

            // 対象の実行
            Long result = currentUserProvider.getUserId();

            // 結果の検証
            assertThat(result).isEqualTo(mockUserId);
            verify(userRepository).findByCognitoUserId(mockCognitoSub);
        }

        @Test
        @DisplayName("Auth-08: JWT認証でない場合例外で処理されること")
        void getUserId_notJwt() {
            // JWTでない認証情報のモックを作成
            UsernamePasswordAuthenticationToken mockOtherAuth =
                new UsernamePasswordAuthenticationToken("user", "password");

            // テスト用に作成した認証情報をSecurityContextHolderにセットする
            SecurityContextHolder.getContext().setAuthentication(mockOtherAuth);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> currentUserProvider.getUserId())
                .isInstanceOf(IllegalStateException.class);

            // 呼び出し処理がされていないことを確認
            verify(userRepository, never()).findByCognitoUserId(any());
        }

        @Test
        @DisplayName("Auth-09: ユーザーが見つからない場合例外で処理されること")
        void getUserId_noUser() {
            // 認証情報のモックを作成
            String mockCognitoSub = "sub-123";
            Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .subject(mockCognitoSub)
                .build();

            JwtAuthenticationToken mockAuthToken =
                new JwtAuthenticationToken(jwt, List.of(), mockCognitoSub);

            // テスト用に作成した認証情報をSecurityContextHolderにセットする
            SecurityContextHolder.getContext().setAuthentication(mockAuthToken);

            // ユーザー取得のスタブ化
            when(userRepository.findByCognitoUserId(mockCognitoSub))
                .thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> currentUserProvider.getUserId())
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
