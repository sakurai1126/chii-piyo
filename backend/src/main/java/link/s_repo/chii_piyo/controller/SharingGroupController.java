package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.SharingGroupConverter;
import link.s_repo.chii_piyo.controller.converter.SharingGroupMemberConverter;
import link.s_repo.chii_piyo.controller.gen.SharingGroupManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.service.SharingGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collections;
import java.util.List;


/**
 * 共有グループ管理コントローラー<br>
 * OpenAPI Generator生成のSharingGroupManagementApiインターフェースを実装し、タグの取得・作成およびメディアとのタグ紐付けに関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SharingGroupController implements SharingGroupManagementApi {
    private final SharingGroupService sharingGroupService;
    private final SharingGroupConverter sharingGroupConverter;
    private final SharingGroupMemberConverter sharingGroupMemberConverter;

    /**
     * POST /sharing-groups
     * 共有グループを新規作成する
     */
    @Override
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
     */
    @Override
    public ResponseEntity<Void> deleteSharingGroup(String xRequestedWith, Long id) {
        // Serviceに削除処理を委譲
        sharingGroupService.deleteSharingGroup(id);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /sharing-groups/{id}
     * 共有グループをID指定で取得する
     */
    @Override
    public ResponseEntity<SharingGroupResponseDto> getSharingGroup(String xRequestedWith, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /sharing-groups<br>
     * 共有グループ一覧を取得する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return 共有グループレスポンスDTOのリスト
     */
    @Override
    public ResponseEntity<List<SharingGroupResponseDto>> getSharingGroups(String xRequestedWith) {
        // サービス層でエンティティを取得
        List<SharingGroups> sharingGroups = sharingGroupService.getSharingGroups();

        // 取得した共有グループからIDを抽出
        List<Long> groupIds = sharingGroups.stream()
            .map(SharingGroups::getId)
            .toList();

        // 抽出したIDを持った所属メンバーを取得
        List<SharingGroupMembers> targetMembers = sharingGroupService.getMembersByGroupIds(groupIds);

        // サービス層でアイコンURLを生成しつつMap化
        SharingGroupService.MemberAndIconMapResult memberAndIconMap =
            sharingGroupService.memberAndIconMapping(targetMembers);

        // コンバータでDTOに変換する
        List<SharingGroupResponseDto> response = sharingGroups.stream()
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

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /sharing-groups/{id}<br>
     * 共有グループメンバーを編集する
     *
     * @param xRequestedWith         X-Requested-With ヘッダ (CSRF防御用)
     * @param id                     対象共有グループのID
     * @param sharingGroupMemberData 編集するメンバー情報
     * @return 更新されたメンバー情報一覧
     */
    @Override
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
