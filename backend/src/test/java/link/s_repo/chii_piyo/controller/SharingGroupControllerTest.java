package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.SharingGroupConverter;
import link.s_repo.chii_piyo.controller.converter.SharingGroupMemberConverter;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
import link.s_repo.chii_piyo.model.gen.SharingGroupRequestDto;
import link.s_repo.chii_piyo.model.gen.SharingGroupResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.service.SharingGroupService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SharingGroupController.class)
public class SharingGroupControllerTest extends BaseControllerTest {
    @MockitoBean
    private SharingGroupService sharingGroupService;
    @MockitoBean
    private SharingGroupConverter sharingGroupConverter;
    @MockitoBean
    private SharingGroupMemberConverter sharingGroupMemberConverter;

    @Nested
    @DisplayName("getSharingGroups - ユーザーの所属する共有グループ一覧の取得")
    class GetSharingGroups {
        @Test
        @WithMockUser
        @DisplayName("ShareCtrl-01: ユーザーの所属する共有グループ一覧の取得ができること")
        void getSharingGroups_success() throws Exception {
            // モックデータの作成
            Long mockCurrentUserId = 1L;
            Long mockGroupId = 2L;
            Long mockMemberUserId = 3L;

            // 共有グループのモックデータ
            SharingGroups mockGroup = new SharingGroups();
            mockGroup.setId(mockGroupId);

            // 所属メンバーのモックデータ
            SharingGroupMembers mockMember = new SharingGroupMembers();
            mockMember.setSharingGroupId(mockGroupId);
            mockMember.setUserId(mockMemberUserId);

            // ユーザーのモックデータ
            Users mockUser = new Users();
            mockUser.setId(mockMemberUserId);

            // アイコンURLのモックデータ
            URI mockIconUrl = URI.create("https://example.com/icon.png");

            // メンバー・ユーザー・アイコンのマッピング結果
            SharingGroupService.MemberAndIconMapResult mockMemberAndIconMap =
                new SharingGroupService.MemberAndIconMapResult(
                    Map.of(mockMemberUserId, mockUser),
                    Map.of(mockMemberUserId, mockIconUrl),
                    Map.of(mockGroupId, List.of(mockMember)));

            // ユーザーID取得のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // 共有グループ取得のモック化
            when(sharingGroupService.getSharingGroups(mockCurrentUserId))
                .thenReturn(List.of(mockGroup));

            // 所属メンバー取得のモック化
            when(sharingGroupService.getMembersByGroupIds(List.of(mockGroupId)))
                .thenReturn(List.of(mockMember));

            // マッピング処理のモック化
            when(sharingGroupService.memberAndIconMapping(List.of(mockMember))).thenReturn(mockMemberAndIconMap);

            // レスポンス変換のモック化
            when(sharingGroupConverter.toSharingGroupResponseDto(any(), any()))
                .thenReturn(new SharingGroupResponseDto().id(mockGroupId));

            // GETリクエストの送信
            mockMvc.perform(get("/sharing-groups"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockGroupId));

            // 抽出したグループIDでメンバー取得が行われていることを検証
            verify(sharingGroupService).getMembersByGroupIds(List.of(mockGroupId));

            // メンバーに対応するユーザー・アイコンURLが引き当てられてConverterに渡ること
            verify(sharingGroupMemberConverter)
                .toSharingGroupMemberResponseDto(mockMember, mockUser, mockIconUrl);

            // グループとメンバーDTOがConverterに渡ること
            verify(sharingGroupConverter).toSharingGroupResponseDto(eq(mockGroup), any());
        }
    }

    @Nested
    @DisplayName("getAllSharingGroups - 共有グループの全件取得")
    class GetAllSharingGroups {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ShareCtrl-02: 共有グループの全件取得ができること")
        void getAllSharingGroups_success() throws Exception {
            // モックデータの作成
            Long mockGroupId = 2L;
            Long mockMemberUserId = 3L;

            // 共有グループのモックデータ
            SharingGroups mockGroup = new SharingGroups();
            mockGroup.setId(mockGroupId);

            // 所属メンバーのモックデータ
            SharingGroupMembers mockMember = new SharingGroupMembers();
            mockMember.setSharingGroupId(mockGroupId);
            mockMember.setUserId(mockMemberUserId);

            // ユーザーのモックデータ
            Users mockUser = new Users();
            mockUser.setId(mockMemberUserId);

            // アイコンURLのモックデータ
            URI mockIconUrl = URI.create("https://example.com/icon.png");

            // メンバー・ユーザー・アイコンのマッピング結果
            SharingGroupService.MemberAndIconMapResult mockMemberAndIconMap =
                new SharingGroupService.MemberAndIconMapResult(
                    Map.of(mockMemberUserId, mockUser),
                    Map.of(mockMemberUserId, mockIconUrl),
                    Map.of(mockGroupId, List.of(mockMember)));

            // 共有グループ取得のモック化
            when(sharingGroupService.getAllSharingGroups()).thenReturn(List.of(mockGroup));

            // 所属メンバー取得のモック化
            when(sharingGroupService.getMembersByGroupIds(List.of(mockGroupId)))
                .thenReturn(List.of(mockMember));

            // マッピング処理のモック化
            when(sharingGroupService.memberAndIconMapping(List.of(mockMember))).thenReturn(mockMemberAndIconMap);

            // レスポンス変換のモック化
            when(sharingGroupConverter.toSharingGroupResponseDto(any(), any()))
                .thenReturn(new SharingGroupResponseDto().id(mockGroupId));

            // GETリクエストの送信
            mockMvc.perform(get("/sharing-groups/all"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockGroupId));

            // 抽出したグループIDでメンバー取得が行われていることを検証
            verify(sharingGroupService).getMembersByGroupIds(List.of(mockGroupId));

            // メンバーに対応するユーザー・アイコンURLが引き当てられてConverterに渡ること
            verify(sharingGroupMemberConverter)
                .toSharingGroupMemberResponseDto(mockMember, mockUser, mockIconUrl);

            // グループとメンバーDTOがConverterに渡ること
            verify(sharingGroupConverter).toSharingGroupResponseDto(eq(mockGroup), any());
        }

        @Test
        @WithMockUser
        @DisplayName("ShareCtrl-03: 一般ユーザーで共有グループの全件取得を試みた場合アクセスが拒否されること")
        void getAllSharingGroups_forbidden() throws Exception {
            // GETリクエストの送信
            mockMvc.perform(get("/sharing-groups/all"))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 取得処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).getAllSharingGroups();
        }
    }

    @Nested
    @DisplayName("createSharingGroup - 共有グループの作成")
    class CreateSharingGroup {
        String mockName = "家族全員";
        List<Long> mockUserIds = List.of(1L, 2L, 3L);

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ShareCtrl-04: 共有グループの作成ができること")
        void createSharingGroup_success() throws Exception {
            SharingGroupRequestDto request = new SharingGroupRequestDto();
            request.setName(mockName);
            request.setUserIds(mockUserIds);

            // POSTリクエストの送信
            mockMvc.perform(post("/sharing-groups")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // グループ作成処理が呼ばれているか確認
            verify(sharingGroupService).createGroup(mockName, mockUserIds);
        }

        @Test
        @WithMockUser
        @DisplayName("ShareCtrl-05: 一般ユーザーで共有グループの作成を試みた場合アクセスが拒否されること")
        void createSharingGroup_forbidden() throws Exception {
            SharingGroupRequestDto request = new SharingGroupRequestDto();
            request.setName(mockName);
            request.setUserIds(mockUserIds);

            // POSTリクエストの送信
            mockMvc.perform(post("/sharing-groups")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // グループ作成処理が呼ばれているか確認
            verify(sharingGroupService, never()).createGroup(any(), any());
        }
    }

    @Nested
    @DisplayName("updateSharingGroup - 共有グループの更新")
    class UpdateSharingGroup {
        String mockName = "家族全員";
        List<Long> mockUserIds = List.of(1L, 2L, 3L);
        Long requestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ShareCtrl-06: 共有グループの更新ができること")
        void updateSharingGroup_success() throws Exception {
            // モックデータの作成
            SharingGroupRequestDto request = new SharingGroupRequestDto();
            request.setName(mockName);
            request.setUserIds(mockUserIds);

            SharingGroups mockSharingGroups = new SharingGroups();

            // 共有グループ取得のモック化
            when(sharingGroupService.getSharingGroupById(requestId)).thenReturn(mockSharingGroups);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/sharing-groups/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 名前の更新処理が呼ばれているか確認
            verify(sharingGroupService).updateSharingGroup(mockSharingGroups, mockName);

            // メンバーの更新処理が呼ばれているか確認
            verify(sharingGroupService).editMembers(requestId, mockUserIds);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ShareCtrl-07: 存在しない共有グループの更新を試みた場合404が返ること")
        void updateSharingGroup_notFound() throws Exception {
            // モックデータの作成
            SharingGroupRequestDto request = new SharingGroupRequestDto();
            request.setName(mockName);
            request.setUserIds(mockUserIds);

            // 共有グループ取得のモック化
            when(sharingGroupService.getSharingGroupById(requestId))
                .thenThrow(new ResourceNotFoundException("共有グループが見つかりません id=" + requestId));

            // PATCHリクエストの送信
            mockMvc.perform(patch("/sharing-groups/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // 名前の更新処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).updateSharingGroup(any(), any());

            // メンバーの更新処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).editMembers(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("ShareCtrl-08: 一般ユーザーで共有グループの更新を試みた場合アクセスが拒否されること")
        void updateSharingGroup_forbidden() throws Exception {
            // モックデータの作成
            SharingGroupRequestDto request = new SharingGroupRequestDto();
            request.setName(mockName);
            request.setUserIds(mockUserIds);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/sharing-groups/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 名前の更新処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).updateSharingGroup(any(), any());

            // メンバーの更新処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).editMembers(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteSharingGroup - 共有グループの削除")
    class DeleteSharingGroup {
        Long requestId = 1L;
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ShareCtrl-09: 共有グループの削除ができること")
        void deleteSharingGroup_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/sharing-groups/{id}", requestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれているか確認
            verify(sharingGroupService).deleteSharingGroup(requestId);
        }

        @Test
        @WithMockUser
        @DisplayName("ShareCtrl-10: 一般ユーザーで共有グループの削除を試みた場合アクセスが拒否されること")
        void deleteSharingGroup_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/sharing-groups/{id}", requestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを確認
            verify(sharingGroupService, never()).deleteSharingGroup(any());
        }
    }
}
