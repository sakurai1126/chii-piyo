package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.common.S3KeyGenerator;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.UserRoleUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.UserUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @Mock
    private S3StorageManager s3StorageManager;

    @InjectMocks
    private UserService userService;


    @Nested
    @DisplayName("getUserById - ユーザーのID指定取得")
    class GetUserById {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("User-01: 有効なユーザーIDを指定した場合、対象ユーザーが返ること")
        void getUserById_success() {
            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.of(mockUser));

            // 対象の実行
            Users result = userService.getUserById(requestId);

            // 取得処理が呼ばれたか確認
            verify(userRepository).findById(requestId);

            // 取得結果の検証
            assertThat(result).isSameAs(mockUser);
        }

        @Test
        @DisplayName("User-02: 存在しないユーザーIDを指定した場合例外で処理されること")
        void getUserById_notFound() {
            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> userService.getUserById(requestId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getUsersById - 複数ユーザーのID指定取得")
    class GetUsersById {
        @Test
        @DisplayName("User-03: 複数ユーザーのID指定取得ができること")
        void getUsersById_success() {
            // リクエストデータの作成
            List<Long> requestIds = List.of(1L);

            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findByIds(requestIds)).thenReturn(List.of(mockUser));

            // 対象の実行
            List<Users> results = userService.getUsersById(requestIds);

            // 取得処理が呼ばれたか確認
            verify(userRepository).findByIds(requestIds);

            // 取得結果の検証
            assertThat(results.getFirst()).isSameAs(mockUser);
        }

        @Test
        @DisplayName("User-04: 空リストを渡した場合、空リストが返ること")
        void getUsersById_emptyList() {
            // リクエストデータの作成
            List<Long> requestIds = List.of();

            // 対象の実行
            List<Users> results = userService.getUsersById(requestIds);

            // 取得処理が呼ばれていないことを確認
            verify(userRepository, never()).findByIds(any());

            // 取得結果の検証
            assertThat(results.size()).isZero();
        }
    }

    @Nested
    @DisplayName("updateMe - 自身の情報を更新")
    class UpdateMe {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("User-05: 表示名が更新できること")
        void updateMe_success() {
            // リクエストデータの作成
            String requestDisplayName = "ぱぱ";
            String requestS3Key = "profile/s3key.png";
            Boolean requestIsDarkMode = true;
            Boolean requestIsEasyMode = true;

            UserUpdateRequestDto request = new UserUpdateRequestDto();
            request.setDisplayName(requestDisplayName);
            request.setS3key(requestS3Key);
            request.setIsDarkMode(requestIsDarkMode);
            request.setIsEasyMode(requestIsEasyMode);

            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.of(mockUser));

            // 対象の実行
            userService.updateMe(requestId, request);

            // リクエストした詳細情報で保存処理が呼ばれていることの確認
            verify(userRepository).update(argThat(requestUser ->
                requestDisplayName.equals(requestUser.getDisplayName())
                    && requestS3Key.equals(requestUser.getUserIconKey())
                    && requestIsDarkMode.equals(requestUser.getIsDarkMode())
                    && requestIsEasyMode.equals(requestUser.getIsEasyMode())
            ));
        }

        @Test
        @DisplayName("User-06: 無効なS3キーを渡した場合アイコンキーが更新されないこと")
        void updateMe_invalidIconKey() {
            // リクエストデータの作成
            String requestDisplayName = "ぱぱ";
            String requestS3Key = "s3key.png";

            UserUpdateRequestDto request = new UserUpdateRequestDto();
            request.setDisplayName(requestDisplayName);
            request.setS3key(requestS3Key);

            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.of(mockUser));

            // 対象の実行
            userService.updateMe(requestId, request);

            // リクエストした詳細情報で保存処理が呼ばれていることの確認
            verify(userRepository).update(argThat(requestUser ->
                requestDisplayName.equals(requestUser.getDisplayName())
                    && requestUser.getUserIconKey() == null
            ));
        }

        @Test
        @DisplayName("User-07: 更新項目がすべて未入力の場合、DB更新処理が呼ばれないこと")
        void updateMe_nullAllFields() {
            // リクエストデータの作成
            UserUpdateRequestDto request = new UserUpdateRequestDto();

            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.of(mockUser));

            // 対象の実行
            userService.updateMe(requestId, request);

            // 保存処理が呼ばれていないことの確認
            verify(userRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("generateIconDownloadPresignedUrl - アイコンダウンロード用URLの生成")
    class GenerateIconDownloadPresignedUrl {
        @Test
        @DisplayName("User-08: アイコンキーを持つユーザーを渡した場合、ダウンロード用URLが返ること")
        void generateIconDownloadPresignedUrl_success() {
            // モックデータの作成
            Users mockUser = new Users();
            String mockS3Key = "profile/s3key.png";
            mockUser.setUserIconKey(mockS3Key);

            // 対象の実行
            userService.generateIconDownloadPresignedUrl(mockUser);

            // 生成処理が呼ばれていることの確認
            verify(s3StorageManager).generateDownloadPresignedUrl(mockS3Key, null);
        }

        @Test
        @DisplayName("User-09: アイコンキーが空の場合、URL生成が呼ばれないこと")
        void generateIconDownloadPresignedUrl_emptyIconKey() {
            // モックデータの作成
            Users mockUser = new Users();

            // 対象の実行
            userService.generateIconDownloadPresignedUrl(mockUser);

            // 生成処理が呼ばれていないことの確認
            verify(s3StorageManager, never()).generateDownloadPresignedUrl(any(), any());
        }
    }

    @Nested
    @DisplayName("generateIconPresignedUrl - アイコンアップロード用URLの生成")
    class GenerateIconPresignedUrl {
        @Test
        @DisplayName("User-10: アイコンアップロード用URLの生成ができること")
        void generateIconPresignedUrl_success() {
            // リクエストデータの作成
            String requestFileName = "image.png";
            String requestContentType = "image/png";
            String mockS3Key = "profile/s3key.png";
            URI mockUrl = URI.create("https://example.com/image.jpg");

            // 取得処理のスタブ化
            when(s3KeyGenerator.buildS3Key("profile", requestFileName)).thenReturn(mockS3Key);
            when(s3StorageManager.generateUploadPresignedUrl(mockS3Key, requestContentType)).thenReturn(mockUrl);

            // 対象の実行
            UserService.CreateIconS3KeyResult result =
                userService.generateIconPresignedUrl(requestFileName, requestContentType);

            // 取得結果の検証
            assertThat(result.s3Key()).isEqualTo(mockS3Key);
            assertThat(result.presignedUrl()).isEqualTo(mockUrl);

            // 生成処理が呼ばれていることの確認
            verify(s3KeyGenerator).buildS3Key("profile", requestFileName);
            verify(s3StorageManager).generateUploadPresignedUrl(mockS3Key, requestContentType);
        }
    }

    @Nested
    @DisplayName("getUsersAndIcon - ユーザー情報の一覧の取得")
    class GetUsersAndIcon {
        @Test
        @DisplayName("User-11: ユーザー情報の一覧の取得ができること")
        void getUsersAndIcon_success() {
            // モックデータの作成
            Users mockUser = new Users();
            String mockS3Key = "profile/s3key.png";
            mockUser.setUserIconKey(mockS3Key);
            URI mockUrl = URI.create("https://example.com/image.jpg");

            // 取得処理のスタブ化
            when(userRepository.findAll()).thenReturn(List.of(mockUser));
            when(s3StorageManager.generateDownloadPresignedUrl(mockS3Key, null)).thenReturn(mockUrl);

            // 対象の実行
            List<UserService.UsersAndIconResult> result = userService.getUsersAndIcon();

            // 取得結果の検証
            assertThat(result.getFirst().user()).isEqualTo(mockUser);
            assertThat(result.getFirst().presignedUrl()).isEqualTo(mockUrl);

            // 取得処理が呼ばれていることの確認
            verify(userRepository).findAll();
            verify(s3StorageManager).generateDownloadPresignedUrl(mockS3Key, null);
        }
    }

    @Nested
    @DisplayName("updateRole - ユーザー権限の更新")
    class UpdateRole {
        @Test
        @DisplayName("User-12: ユーザー権限の更新ができること")
        void updateRole_success() {
            // リクエストデータの作成
            Long requestId = 1L;
            UserRoleUpdateRequestDto request = new UserRoleUpdateRequestDto();
            request.setRole(UserRoleUpdateRequestDto.RoleEnum.ADMIN);

            // モックデータの作成
            Users mockUser = new Users();

            // 取得処理のスタブ化
            when(userRepository.findById(requestId)).thenReturn(Optional.of(mockUser));

            // 対象の実行
            userService.updateRole(requestId, request);

            // 更新処理が呼ばれていることの確認
            verify(userRepository).update(mockUser);
        }
    }
}
