package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.UserConverter;
import link.s_repo.chii_piyo.controller.converter.UserGenerateIconDataConverter;
import link.s_repo.chii_piyo.model.gen.UserGenerateIconDataResponseDto;
import link.s_repo.chii_piyo.model.gen.UserResponseDto;
import link.s_repo.chii_piyo.model.gen.UserRoleUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.UserUpdateIconRequestDto;
import link.s_repo.chii_piyo.model.gen.UserUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.Users;

import link.s_repo.chii_piyo.service.SharingGroupService;
import link.s_repo.chii_piyo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserConverter userConverter;
    @MockitoBean
    private UserGenerateIconDataConverter userGenerateIconDataConverter;
    @MockitoBean
    private SharingGroupService sharingGroupService;

    @Nested
    @DisplayName("getMe - ログイン中のユーザー自身の情報を取得")
    class GetMe {
        @Test
        @WithMockUser
        @DisplayName("UserCtrl-01: ログイン中のユーザー自身の情報を取得ができること")
        void getMe_success() throws Exception {
            // モックデータの作成
            Long mockCurrentUserId = 1L;
            URI mockPresignedUrl = URI.create("https://example.com/icon.png");
            List<Long> mockScopeSharingGroups = List.of(1L, 2L);
            Users mockUser = new Users();
            mockUser.setId(mockCurrentUserId);
            UserResponseDto response = new UserResponseDto().id(mockCurrentUserId);

            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            // ユーザー取得処理のモック化
            when(userService.getUserById(mockCurrentUserId)).thenReturn(mockUser);
            // ユーザーの共有範囲の取得処理のモック化
            when(sharingGroupService.getUserSharingScopes(mockCurrentUserId)).thenReturn(mockScopeSharingGroups);
            // プロフィール画像取得処理のモック化
            when(userService.generateIconDownloadPresignedUrl(mockUser)).thenReturn(mockPresignedUrl);
            // レスポンス取得処理のモック化
            when(userConverter.toUserResponseDto(mockUser, mockPresignedUrl, mockScopeSharingGroups)).thenReturn(response);

            // GETリクエストの送信
            mockMvc.perform(get("/users/me"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockCurrentUserId));

            // 各処理が呼ばれていることを確認
            verify(userService).getUserById(mockCurrentUserId);
            verify(sharingGroupService).getUserSharingScopes(mockCurrentUserId);
            verify(userService).generateIconDownloadPresignedUrl(mockUser);
            verify(userConverter).toUserResponseDto(mockUser, mockPresignedUrl, mockScopeSharingGroups);
        }
    }

    @Nested
    @DisplayName("updateMe - ログイン中のユーザー自身の情報を更新")
    class UpdateMe {
        @Test
        @WithMockUser
        @DisplayName("UserCtrl-02: ログイン中のユーザー自身の情報の更新ができること")
        void updateMe_success() throws Exception {
            // モックデータの作成
            Long mockCurrentUserId = 1L;
            UserUpdateRequestDto request = new UserUpdateRequestDto();

            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/users/me")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを確認
            verify(userService).updateMe(mockCurrentUserId, request);
        }
    }

    @Nested
    @DisplayName("generateIconPresignedUrl - アイコンアップロード用URLの生成と取得")
    class GenerateIconPresignedUrl {
        @Test
        @WithMockUser
        @DisplayName("UserCtrl-03: アイコンアップロード用URLの生成と取得取得ができること")
        void generateIconPresignedUrl_success() throws Exception {
            String mockFileName = "icon.png";
            String mockContentType = "image/png";
            String mockS3Key = "icon.png";
            URI mockPresignedUrl = URI.create("https://example.com/icon.png");

            UserUpdateIconRequestDto request = new UserUpdateIconRequestDto();
            request.setFilename(mockFileName);
            request.setContentType(mockContentType);

            UserService.CreateIconS3KeyResult mockResult = new UserService
                .CreateIconS3KeyResult(mockS3Key, mockPresignedUrl);
            UserGenerateIconDataResponseDto response = new UserGenerateIconDataResponseDto()
                .s3key(mockS3Key)
                .presignedUrl(mockPresignedUrl);

            // アップロード用URL発行処理のモック化
            when(userService.generateIconPresignedUrl(mockFileName, mockContentType)).thenReturn(mockResult);

            // レスポンス生成処理のモック化
            when(userGenerateIconDataConverter.toUserGenerateIconDataResponseDto(mockS3Key, mockPresignedUrl))
                .thenReturn(response);

            // POSTリクエストの送信
            mockMvc.perform(post("/users/me/icon")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.s3key").value(mockS3Key))
                .andExpect(jsonPath("$.presignedUrl").value(mockPresignedUrl.toString()));

            // 各処理が呼ばれていることを確認
            verify(userService).generateIconPresignedUrl(mockFileName, mockContentType);
            verify(userGenerateIconDataConverter).toUserGenerateIconDataResponseDto(mockS3Key, mockPresignedUrl);
        }
    }

    @Nested
    @DisplayName("getUsers - ユーザー一覧の取得")
    class GetUsers {
        @Test
        @WithMockUser
        @DisplayName("UserCtrl-04: ユーザー一覧の取得ができること")
        void getUsers_success() throws Exception {
            // モックデータの作成
            Long mockUserId = 1L;
            URI mockPresignedUrl = URI.create("https://example.com/icon.png");
            List<Long> mockScopeSharingGroups = List.of(1L, 2L);
            Users mockUser = new Users();
            mockUser.setId(mockUserId);

            UserService.UsersAndIconResult mockResult = new UserService
                .UsersAndIconResult(mockUser, mockPresignedUrl);
            UserResponseDto response = new UserResponseDto().id(mockUserId);

            // ユーザー情報一覧とアイコンダウンロード用URL取得処理のモック化
            when(userService.getUsersAndIcon()).thenReturn(List.of(mockResult));
            // 共有グループの一括取得処理のモック化
            when(sharingGroupService.getUserSharingScopesBulk(List.of(mockUserId)))
                .thenReturn(Map.of(mockUserId, mockScopeSharingGroups));
            // レスポンス生成処理のモック化
            when(userConverter.toUserResponseDto(mockUser, mockPresignedUrl, mockScopeSharingGroups))
                .thenReturn(response);

            // GETリクエストの送信
            mockMvc.perform(get("/users"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // レスポンスの確認
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockUserId));

            // 各処理が呼ばれていることを確認
            verify(userService).getUsersAndIcon();
            verify(sharingGroupService).getUserSharingScopesBulk(List.of(mockUserId));
            verify(userConverter).toUserResponseDto(mockUser, mockPresignedUrl, mockScopeSharingGroups);
        }
    }


    @Nested
    @DisplayName("updateRole - 他ユーザーの権限変更")
    class UpdateRole {
        // モックデータの作成
        Long mockCurrentUserId = 1L;
        Long requestId = 2L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("UserCtrl-05: 他ユーザーの権限変更ができること")
        void updateRole_success() throws Exception {
            UserRoleUpdateRequestDto request = new UserRoleUpdateRequestDto();
            request.setRole(UserRoleUpdateRequestDto.RoleEnum.ADMIN);

            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/users/{id}/role", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを確認
            verify(userService).updateRole(requestId, request);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("UserCtrl-06: 自分のロールを変更しようとした際アクセスが拒否されること")
        void updateRole_own() throws Exception {
            UserRoleUpdateRequestDto request = new UserRoleUpdateRequestDto();
            request.setRole(UserRoleUpdateRequestDto.RoleEnum.ADMIN);

            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/users/{id}/role", mockCurrentUserId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 400 Bad Requestであることを確認
                .andExpect(status().isBadRequest());

            // 更新処理が呼ばれていないことを確認
            verify(userService, never()).updateRole(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("UserCtrl-07: 一般ユーザーで他ユーザーの権限変更を試みた場合アクセスが拒否されること")
        void updateRole_forbidden() throws Exception {
            UserRoleUpdateRequestDto request = new UserRoleUpdateRequestDto();
            request.setRole(UserRoleUpdateRequestDto.RoleEnum.ADMIN);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/users/{id}/role", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを確認
            verify(userService, never()).updateRole(any(), any());
        }
    }
}
