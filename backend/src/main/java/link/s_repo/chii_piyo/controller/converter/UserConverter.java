package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.UserResponseDto;
import link.s_repo.chii_piyo.model.gen.Users;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Usersエンティティ、URL、共有範囲グループを受け取りUserResponseDtoに変換するコンバーター
 */
@Component
public class UserConverter {
    /**
     * Usersエンティティ、URL、共有範囲グループを受け取りUserResponseDtoに変換する
     *
     * @param user               Usersエンティティ
     * @param presignedUrl       アイコンダウンロードURL
     * @param scopeSharingGroups 共有範囲グループのリスト
     * @return UserResponseDto
     */
    public UserResponseDto toUserResponseDto(
        Users user, URI presignedUrl, List<Long> scopeSharingGroups) {
        return new UserResponseDto()
            .id(user.getId())
            .cognitoUserId(user.getCognitoUserId())
            .displayName(user.getDisplayName())
            .email(user.getEmail())
            .presignedIconUrl(presignedUrl)
            .scopeSharingGroups(scopeSharingGroups)
            .isDarkMode(user.getIsDarkMode())
            .isEasyMode(user.getIsEasyMode())
            .role(user.getRole() != null ? UserResponseDto.RoleEnum.fromValue(user.getRole()) :
                UserResponseDto.RoleEnum.VIEWER)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt());
    }
}
