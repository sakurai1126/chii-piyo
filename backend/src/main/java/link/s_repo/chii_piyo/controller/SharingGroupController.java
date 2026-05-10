package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.SharingGroupConverter;
import link.s_repo.chii_piyo.controller.gen.SharingGroupManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.service.SharingGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * POST /sharing-groups/{id}/members
     * 共有グループにメンバーを追加する
     */
    @Override
    public ResponseEntity<SharingGroupMemberResponseDto> addSharingGroupMember(
        String xRequestedWith, Long id, SharingGroupMemberRequestDto sharingGroupMemberData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /sharing-groups
     * 共有グループを新規作成する
     */
    @Override
    public ResponseEntity<SharingGroupResponseDto> createSharingGroup(
        String xRequestedWith, SharingGroupRequestDto sharingGroupData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /sharing-groups/{id}
     * 共有グループを削除する
     */
    @Override
    public ResponseEntity<Void> deleteSharingGroup(String xRequestedWith, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
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
     * GET /sharing-groups/{id}/members
     * 共有グループのメンバー一覧を取得する
     */
    @Override
    public ResponseEntity<List<SharingGroupMemberResponseDto>> getSharingGroupMembers(
        String xRequestedWith, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /sharing-groups
     * 共有グループ一覧を取得する
     */
    @Override
    public ResponseEntity<List<SharingGroupResponseDto>> getSharingGroups(String xRequestedWith) {
        // サービス層でエンティティを取得し、コンバータでDTOに変換する
        List<SharingGroupResponseDto> response = sharingGroupService.findAll().stream()
            .map(sharingGroupConverter::toSharingGroupResponseDto)
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /sharing-groups/{id}/members/{memberId}
     * 共有グループからメンバーを削除する
     */
    @Override
    public ResponseEntity<Void> removeSharingGroupMember(
        String xRequestedWith, Long id, Long memberId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /sharing-groups/{id}
     * 共有グループを更新する
     */
    @Override
    public ResponseEntity<SharingGroupResponseDto> updateSharingGroup(
        String xRequestedWith, Long id, SharingGroupRequestDto sharingGroupData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

}
