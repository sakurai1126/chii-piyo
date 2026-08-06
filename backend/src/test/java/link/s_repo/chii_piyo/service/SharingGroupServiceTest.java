package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.SharingGroupRepository;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SharingGroupServiceTest {
    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private SharingGroupRepository sharingGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3StorageManager s3StorageManager;

    @InjectMocks
    private SharingGroupService sharingGroupService;


    @Nested
    @DisplayName("getSharingGroups - 所属共有グループの取得")
    class GetSharingGroups {
        @Test
        @DisplayName("Share-01: ログインユーザー所属の共有グループを取得できること")
        void getSharingGroups_success() {
            // リクエストデータの作成
            Long requestUserId = 1L;
            Long mockGroupId = 2L;
            SharingGroupMembers mockMembers = new SharingGroupMembers();
            mockMembers.setSharingGroupId(mockGroupId);

            // 取得処理のスタブ化
            when(sharingGroupRepository.findMembersByUserId(requestUserId))
                .thenReturn(List.of(mockMembers));

            // 対象の実行
            sharingGroupService.getSharingGroups(requestUserId);

            // 取得処理が呼ばれたか確認
            verify(sharingGroupRepository).findByIdsOrderById(List.of(mockGroupId));
        }

        @Test
        @DisplayName("Share-02: 所属グループが存在しない場合空リストが返ること")
        void getSharingGroups_empty() {
            // リクエストデータの作成
            Long requestUserId = 1L;

            // 取得処理のスタブ化
            when(sharingGroupRepository.findMembersByUserId(requestUserId))
                .thenReturn(List.of());

            // 対象の実行
            List<SharingGroups> result = sharingGroupService.getSharingGroups(requestUserId);

            // 取得結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことを確認
            verify(sharingGroupRepository, never()).findByIdsOrderById(any());
        }
    }

    @Nested
    @DisplayName("getAllSharingGroups - 共有グループの全件取得")
    class GetAllSharingGroups {
        @Test
        @DisplayName("Share-03: 共有グループの全件取得ができること")
        void getAllSharingGroups_success() {
            // 対象の実行
            sharingGroupService.getAllSharingGroups();

            // 取得処理が呼ばれたか確認
            verify(sharingGroupRepository).findAllOrderById();
        }
    }

    @Nested
    @DisplayName("getSharingGroupById - 共有グループのID指定取得")
    class GetSharingGroupById {
        @Test
        @DisplayName("Share-04: 共有グループのID指定取得ができること")
        void getSharingGroupById_success() {
            // リクエストデータの作成
            Long requestUserId = 1L;

            // モックデータの作成
            SharingGroups mockGroup = new SharingGroups();

            // 取得処理のスタブ化
            when(sharingGroupRepository.findById(requestUserId))
                .thenReturn(Optional.of(mockGroup));

            // 対象の実行
            SharingGroups result = sharingGroupService.getSharingGroupById(requestUserId);

            // 結果の確認
            assertThat(result).isSameAs(mockGroup);

            // 取得処理が呼ばれたか確認
            verify(sharingGroupRepository).findById(requestUserId);
        }

        @Test
        @DisplayName("Share-05: 存在しない共有グループIDを指定してリクエストした場合例外で処理されること")
        void getSharingGroupById_notFound() {
            // リクエストデータの作成
            Long requestUserId = 1L;

            // 取得処理のスタブ化
            when(sharingGroupRepository.findById(requestUserId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> sharingGroupService.getSharingGroupById(requestUserId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createGroup - 共有グループの作成")
    class CreateGroup {
        @Test
        @DisplayName("Share-06: 共有グループの作成ができること")
        void createGroup_success() {

            // リクエストデータの作成
            String requestName = "家族全員";
            Long requestUserId = 1L;

            // 対象の実行
            sharingGroupService.createGroup(requestName, List.of(requestUserId));

            // 保存処理が呼ばれたか確認
            verify(sharingGroupRepository).save(any(SharingGroups.class));
            verify(sharingGroupRepository).membersSave(any());
        }
    }

    @Nested
    @DisplayName("getMembersByGroupIds - 共有グループメンバーの取得")
    class GetMembersByGroupIds {
        @Test
        @DisplayName("Share-07: 共有グループメンバーの取得ができること")
        void getMembersByGroupIds_success() {
            List<Long> requestGroupIds = List.of(1L);

            // 対象の実行
            sharingGroupService.getMembersByGroupIds(requestGroupIds);

            // 取得処理が呼ばれたか確認
            verify(sharingGroupRepository).findMembersByGroupIds(requestGroupIds);
        }

        @Test
        @DisplayName("Share-08: 空のIDでリクエストした場合空リストが返ること")
        void getMembersByGroupIds_empty() {
            // 対象の実行
            List<SharingGroupMembers> result = sharingGroupService
                .getMembersByGroupIds(List.of());

            // 結果の確認
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことを確認
            verify(sharingGroupRepository, never()).findMembersByGroupIds(any());
        }
    }

    @Nested
    @DisplayName("editMembers - 共有グループメンバーの編集")
    class EditMembers {
        @Test
        @DisplayName("Share-09: 共有グループメンバーの編集ができること")
        void editMembers_success() {
            // リクエストデータの作成
            Long requestId = 1L;
            List<Long> requestNewUserIds = List.of(1L);

            // 対象の実行
            sharingGroupService.editMembers(requestId, requestNewUserIds);

            // 各処理が呼ばれていることを確認
            verify(sharingGroupRepository).deleteMembersByGroupId(requestId);
            verify(sharingGroupRepository).membersSave(
                argThat(members -> members.size() == 1
                    && requestId.equals(members.getFirst().getSharingGroupId())
                    && requestNewUserIds.getFirst().equals(members.getFirst().getUserId()))
            );
        }
    }

    @Nested
    @DisplayName("deleteSharingGroup - 共有グループの削除")
    class DeleteSharingGroup {
        @Test
        @DisplayName("Share-10: 共有グループの削除ができること")
        void deleteSharingGroup_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            SharingGroups mockGroup = new SharingGroups();

            // 取得処理のスタブ化
            when(sharingGroupRepository.findById(requestId)).thenReturn(Optional.of(mockGroup));

            // 対象の実行
            sharingGroupService.deleteSharingGroup(requestId);

            // 各処理が呼ばれていることを確認
            verify(mediaRepository).clearSharingGroupId(requestId);
            verify(sharingGroupRepository).deleteMembersByGroupId(requestId);
            verify(sharingGroupRepository).delete(requestId);
        }
    }

    @Nested
    @DisplayName("updateSharingGroup - 共有グループ名の更新")
    class UpdateSharingGroup {
        @Test
        @DisplayName("Share-11: 共有グループ名の更新ができること")
        void updateSharingGroup_success() {
            // リクエストデータの作成
            SharingGroups requestGroup = new SharingGroups();
            String requestName = "夫婦のみ";

            // 対象の実行
            sharingGroupService.updateSharingGroup(requestGroup, requestName);

            // 更新処理が呼ばれていることを確認
            verify(sharingGroupRepository).update(
                argThat(group -> requestName.equals(group.getName())));
        }
    }

    @Nested
    @DisplayName("getUserSharingScopes - ユーザーの共有範囲IDリストの取得")
    class GetUserSharingScopes {
        @Test
        @DisplayName("Share-12: ユーザーの共有範囲IDリストの取得ができること")
        void getUserSharingScopes_success() {
            // リクエストデータの作成
            Long requestUserId = 1L;

            // モックデータの作成
            Long mockGroupId = 2L;
            SharingGroupMembers mockMembers = new SharingGroupMembers();
            mockMembers.setSharingGroupId(mockGroupId);

            // 取得処理のスタブ化
            when(sharingGroupRepository.findMembersByUserId(requestUserId)).thenReturn(List.of(mockMembers));

            // 対象の実行
            List<Long> result = sharingGroupService.getUserSharingScopes(requestUserId);

            // 結果の確認
            assertThat(result.getFirst()).isEqualTo(mockGroupId);

            // 各処理が呼ばれていることを確認
            verify(sharingGroupRepository).findMembersByUserId(requestUserId);
        }
    }

    @Nested
    @DisplayName("getUserSharingScopesBulk - 複数ユーザーの共有範囲の取得")
    class GetUserSharingScopesBulk {
        @Test
        @DisplayName("Share-13: 複数ユーザーの共有範囲の取得ができること")
        void getUserSharingScopesBulk_success() {
            // リクエストデータの作成
            Long requestUserId = 1L;

            // モックデータの作成
            Long mockGroupId = 2L;
            SharingGroupMembers mockMember = new SharingGroupMembers();
            mockMember.setUserId(requestUserId);
            mockMember.setSharingGroupId(mockGroupId);

            // 取得処理のスタブ化
            when(sharingGroupRepository.findMembersByUserIds(List.of(requestUserId)))
                .thenReturn(List.of(mockMember));

            // 対象の実行
            Map<Long, List<Long>> result = sharingGroupService
                .getUserSharingScopesBulk(List.of(requestUserId));

            // 結果の検証
            assertThat(result.get(requestUserId)).containsExactly(mockGroupId);

            // 取得処理が呼ばれていることの確認
            verify(sharingGroupRepository).findMembersByUserIds(List.of(requestUserId));
        }

        @Test
        @DisplayName("Share-14: 空のユーザーIDリストを渡した場合、空のマップが返ること")
        void getUserSharingScopesBulk_emptyList() {
            // 対象の実行
            Map<Long, List<Long>> result = sharingGroupService
                .getUserSharingScopesBulk(List.of());

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(sharingGroupRepository, never()).findMembersByUserIds(any());
        }
    }

    @Nested
    @DisplayName("memberAndIconMapping - 所属メンバーとアイコンのデータ構造化")
    class MemberAndIconMapping {
        @Test
        @DisplayName("Share-15: メンバー情報からユーザーマップとアイコンURLマップが生成されること")
        void memberAndIconMapping_success() {
            // モックデータの作成
            Long mockUserId = 1L;
            Long mockGroupId = 2L;
            String mockS3Key = "profile/image.png";
            URI mockUrl = URI.create("https://example.com/icon.jpg");

            Users mockUser = new Users();
            mockUser.setId(mockUserId);
            mockUser.setUserIconKey(mockS3Key);

            // リクエストするメンバー情報を作成
            SharingGroupMembers requestMember = new SharingGroupMembers();
            requestMember.setUserId(mockUserId);
            requestMember.setSharingGroupId(mockGroupId);

            // 取得処理のスタブ化
            when(userRepository.findByIds(List.of(mockUserId)))
                .thenReturn(List.of(mockUser));
            when(s3StorageManager.generateDownloadPresignedUrl(mockS3Key, null))
                .thenReturn(mockUrl);

            // 対象の実行
            SharingGroupService.MemberAndIconMapResult result =
                sharingGroupService.memberAndIconMapping(List.of(requestMember));

            // 結果の検証
            assertThat(result.usersMap().get(mockUserId)).isEqualTo(mockUser);
            assertThat(result.iconUrlsMap().get(mockUserId)).isEqualTo(mockUrl);
            assertThat(result.membersByGroupIdMap().get(mockGroupId)).isEqualTo(List.of(requestMember));

            // 各処理が呼ばれていることを確認
            verify(userRepository).findByIds(List.of(mockUserId));
            verify(s3StorageManager).generateDownloadPresignedUrl(mockS3Key, null);
        }
    }
}
