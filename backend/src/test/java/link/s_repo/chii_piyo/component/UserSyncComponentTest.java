package link.s_repo.chii_piyo.component;

import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class UserSyncComponentTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSyncComponent userSyncComponent;

    @Nested
    @DisplayName("findOrCreateByCognitoUserId - ユーザー同期処理")
    class FindOrCreateByCognitoUserId {
        // 共通リクエストデータの作成
        String requestCognitoUserId = "12345";
        String requestEmail = "test@example.com";

        @Test
        @DisplayName("Util-04: CognitoのユーザーIDでユーザーを取得できること")
        void findOrCreateByCognitoUserId_success() {
            // モックユーザーの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findByCognitoUserId(requestCognitoUserId))
                .thenReturn(Optional.of(mockUser));

            // 対象の実行
            Users result = userSyncComponent.findOrCreateByCognitoUserId(
                requestCognitoUserId, requestEmail);

            // 結果の検証
            assertThat(result).isSameAs(mockUser);

            // 呼び出し処理が行われているかの確認
            verify(userRepository).findByCognitoUserId(requestCognitoUserId);
        }

        @Test
        @DisplayName("Util-05: CognitoのユーザーIDでユーザー検索して存在しない場合作成されること")
        void findOrCreateByCognitoUserId_create() {
            // 取得処理のスタブ化
            when(userRepository.findByCognitoUserId(requestCognitoUserId))
                .thenReturn(Optional.empty());

            // 対象の実行
            Users result = userSyncComponent.findOrCreateByCognitoUserId(
                requestCognitoUserId, requestEmail);

            // 結果の検証
            assertThat(result.getCognitoUserId()).isEqualTo(requestCognitoUserId);
            assertThat(result.getEmail()).isEqualTo(requestEmail);
            assertThat(result.getDisplayName()).isEqualTo(requestEmail);
            assertThat(result.getIsDarkMode()).isFalse();
            assertThat(result.getIsEasyMode()).isFalse();
            assertThat(result.getRole()).isEqualTo("VIEWER");

            // 呼び出し処理が行われているかの確認
            verify(userRepository).findByCognitoUserId(requestCognitoUserId);

            // デフォルト値にて保存処理が呼び出されているか確認
            verify(userRepository).save(argThat(user ->
                user.getCognitoUserId().equals(requestCognitoUserId)
                    && user.getEmail().equals(requestEmail)
                    && user.getDisplayName().equals(requestEmail)
                    && user.getIsDarkMode().equals(false)
                    && user.getIsEasyMode().equals(false)
                    && user.getRole().equals("VIEWER")
            ));
        }

        @Test
        @DisplayName("Util-06: ADMIN_EMAILに一致するメールアドレスの場合、ADMIN権限で作成されること")
        void findOrCreateByCognitoUserId_createAdmin() {
            // 環境変数で管理者メールアドレスを管理しているフィールドにテスト用の値をセット
            setField(userSyncComponent, "adminEmail", requestEmail);

            // 取得処理のスタブ化
            when(userRepository.findByCognitoUserId(requestCognitoUserId))
                .thenReturn(Optional.empty());

            // 対象の実行
            Users result = userSyncComponent.findOrCreateByCognitoUserId(
                requestCognitoUserId, requestEmail);

            // 作成されたユーザーの権限が管理者であることを検証
            assertThat(result.getRole()).isEqualTo("ADMIN");
        }

    }
}
