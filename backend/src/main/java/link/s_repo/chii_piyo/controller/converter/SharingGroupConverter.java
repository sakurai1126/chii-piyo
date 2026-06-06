package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.SharingGroupMemberResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroupResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * SharingGroupsエンティティをSharingGroupResponseDtoに変換するロジックを提供する
 */
@Component
public class SharingGroupConverter {
    /**
     * SharingGroupsエンティティをSharingGroupResponseDtoに変換する
     *
     * @param sharingGroup SharingGroupsエンティティ
     * @param members 所属メンバーのリスト
     * @return SharingGroupResponseDto
     */
    public SharingGroupResponseDto toSharingGroupResponseDto(
        SharingGroups sharingGroup,
        List<SharingGroupMemberResponseDto> members) {
        return new SharingGroupResponseDto(
            sharingGroup.getId(), // ID
            sharingGroup.getName(), // 共有グループ名
            members, // メンバーのリスト
            sharingGroup.getCreatedAt(), // 作成日時
            sharingGroup.getUpdatedAt() // 更新日時
        );
    }
}
