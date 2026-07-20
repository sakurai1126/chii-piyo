package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.SharingGroupMemberResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroupResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SharingGroupsエンティティをSharingGroupResponseDtoに変換するコンバーター
 */
@Component
public class SharingGroupConverter {
    /**
     * SharingGroupsエンティティをSharingGroupResponseDtoに変換する
     *
     * @param sharingGroup SharingGroupsエンティティ
     * @param members      所属メンバーのレスポンスリスト
     * @return SharingGroupResponseDto
     */
    public SharingGroupResponseDto toSharingGroupResponseDto(
        SharingGroups sharingGroup, List<SharingGroupMemberResponseDto> members) {
        return new SharingGroupResponseDto()
            .id(sharingGroup.getId())
            .name(sharingGroup.getName())
            .members(members)
            .createdAt(sharingGroup.getCreatedAt())
            .updatedAt(sharingGroup.getUpdatedAt());
    }
}
