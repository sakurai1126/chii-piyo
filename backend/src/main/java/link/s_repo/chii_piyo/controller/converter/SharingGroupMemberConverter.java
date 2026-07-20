package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.SharingGroupMemberResponseDto;
import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
import link.s_repo.chii_piyo.model.gen.Users;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

/**
 * SharingGroupMembersエンティティをSharingGroupMemberResponseDtoに変換するコンバーター
 */
@Component
public class SharingGroupMemberConverter {
    /**
     * SharingGroupMembersエンティティをSharingGroupMemberResponseDtoに変換する
     *
     * @param sharingGroupMember SharingGroupMembersエンティティ
     * @return SharingGroupMemberResponseDto
     */
    public SharingGroupMemberResponseDto toSharingGroupMemberResponseDto(
        SharingGroupMembers sharingGroupMember, Users user, URI presignedIconUrl) {
        return new SharingGroupMemberResponseDto()
            .id(sharingGroupMember.getId())
            .userId(sharingGroupMember.getUserId())
            .displayName(user.getDisplayName())
            .presignedIconUrl(Objects.toString(presignedIconUrl, null))
            .createdAt(sharingGroupMember.getCreatedAt());
    }
}
