package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.SharingGroupConverter;
import link.s_repo.chii_piyo.controller.converter.SharingGroupMemberConverter;
import link.s_repo.chii_piyo.controller.gen.SharingGroupManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.SharingGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collections;
import java.util.List;


/**
 * 共有グループ管理コントローラー<br>
 * 共有グループと所属するメンバーの取得・作成・更新・削除に関するAPIエンドポイントを提供
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SharingGroupController implements SharingGroupManagementApi {
    private final SharingGroupService sharingGroupService;
    private final SharingGroupConverter sharingGroupConverter;
    private final SharingGroupMemberConverter sharingGroupMemberConverter;
    private final CurrentUserProvider currentUserProvider;

    /**
     * POST /sharing-groups
     * 共有グループを新規作成する
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createSharingGroup(
        String xRequestedWith, SharingGroupRequestDto sharingGroupData) {
        // サービス層で共有グループを作成する
        sharingGroupService.createGroup(sharingGroupData.getName(), sharingGroupData.getUserIds());

        // ステータスコードのみを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /sharing-groups/{id}<br>
     * 共有グループを削除する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             対象の共有グループID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteSharingGroup(String xRequestedWith, Long id) {
        // サービス層で削除処理
        sharingGroupService.deleteSharingGroup(id);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /sharing-groups<br>
     * 共有グループ一覧を取得する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @return 共有グループ一覧
     */
    @Override
    public ResponseEntity<List<SharingGroupResponseDto>> getSharingGroups(String xRequestedWith) {
        // 認証情報からアプリケーション側のユーザーIDを取得
        Long userId = currentUserProvider.getUserId();

        // サービス層でエンティティを取得
        List<SharingGroups> sharingGroups = sharingGroupService.getSharingGroups(userId);

        // レスポンスDTOに変換する
        List<SharingGroupResponseDto> response = buildSharingGroupsResponse(sharingGroups);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /sharing-groups/all<br>
     * 共有グループ一覧を全件取得
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @return 共有グループ一覧
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SharingGroupResponseDto>> getAllSharingGroups(
        String xRequestedWith) {
        // サービス層でエンティティを取得
        List<SharingGroups> sharingGroups = sharingGroupService.getAllSharingGroups();

        // レスポンスDTOに変換する
        List<SharingGroupResponseDto> response = buildSharingGroupsResponse(sharingGroups);

        return ResponseEntity.ok(response);
    }

    /**
     * 取得した共有グループからレスポンスDTOを作成する<br>
     * ユーザー毎一覧取得及び管理者向け全件取得の共通処理
     *
     * @param sharingGroups 共有グループエンティティリスト
     * @return 共有グループ一覧レスポンス
     */
    private List<SharingGroupResponseDto> buildSharingGroupsResponse(
        List<SharingGroups> sharingGroups) {
        // 取得した共有グループからIDを抽出
        List<Long> groupIds = sharingGroups.stream()
            .map(SharingGroups::getId)
            .toList();

        // 抽出したIDを持った所属メンバーを取得
        List<SharingGroupMembers> targetMembers = sharingGroupService.getMembersByGroupIds(groupIds);

        // サービス層でアイコンURLを生成しつつMap化
        SharingGroupService.MemberAndIconMapResult memberAndIconMap =
            sharingGroupService.memberAndIconMapping(targetMembers);

        // コンバータでDTOに変換して返却
        return sharingGroups.stream()
            .map(group -> {
                // MapからこのグループのIDに紐づくメンバーリスト(いない場合は空リスト)を取得する
                List<SharingGroupMembers> members =
                    memberAndIconMap.membersByGroupIdMap().getOrDefault(group.getId(),
                        Collections.emptyList());

                // 所属メンバーをレスポンスDTOに変換する
                List<SharingGroupMemberResponseDto> memberDtos = members.stream()
                    .map(member -> {
                        // IDを元に、Mapからユーザー情報とアイコンURLを取得
                        Users user = memberAndIconMap.usersMap().get(member.getUserId());
                        URI iconUrl = memberAndIconMap.iconUrlsMap().get(member.getUserId());
                        // メンバー情報、ユーザー情報、アイコンURLを渡して、メンバー用DTOを作成
                        return sharingGroupMemberConverter.toSharingGroupMemberResponseDto(member, user, iconUrl);
                    }).toList();
                // コンバータにグループ情報と、変換済みのメンバーDTOリストを渡す
                return sharingGroupConverter.toSharingGroupResponseDto(group, memberDtos);
            })
            .toList();
    }

    /**
     * PATCH /sharing-groups/{id}<br>
     * 共有グループメンバーを編集する
     *
     * @param xRequestedWith         CSRF防御用カスタムリクエストヘッダー
     * @param id                     対象共有グループのID
     * @param sharingGroupUpdateData 編集するメンバー情報
     * @return 更新されたメンバー情報一覧
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SharingGroupResponseDto> updateSharingGroup(
        String xRequestedWith, Long id, SharingGroupUpdateRequestDto sharingGroupUpdateData) {
        // サービス層でグループのエンティティを取得

        SharingGroups sharingGroups = sharingGroupService.getSharingGroupById(id);

        // 名前のリクエストがある場合のサービス層で名前を更新
        if (sharingGroupUpdateData.getName() != null) {
            sharingGroups = sharingGroupService.updateSharingGroup(sharingGroups, sharingGroupUpdateData.getName());
        }

        // サービス層でメンバー情報の更新を行う
        List<SharingGroupMembers> newMembers = sharingGroupService.editMembers(
            id, sharingGroupUpdateData.getUserIds());

        // サービス層でアイコンURLを生成しつつMap化
        SharingGroupService.MemberAndIconMapResult memberAndIconMap =
            sharingGroupService.memberAndIconMapping(newMembers);

        // 所属メンバーをレスポンスDTOに変換する
        List<SharingGroupMemberResponseDto> memberDtos = newMembers.stream()
            .map(member -> {
                // IDを元に、Mapからユーザー情報とアイコンURLを取得
                Users user = memberAndIconMap.usersMap().get(member.getUserId());
                URI iconUrl = memberAndIconMap.iconUrlsMap().get(member.getUserId());
                // メンバー情報、ユーザー情報、アイコンURLを渡して、メンバー用DTOを作成
                return sharingGroupMemberConverter.toSharingGroupMemberResponseDto(member, user, iconUrl);
            }).toList();

        // コンバータで変換する
        SharingGroupResponseDto response =
            sharingGroupConverter.toSharingGroupResponseDto(sharingGroups, memberDtos);

        // レスポンスを返却
        return ResponseEntity.ok(response);
    }
}
