package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.UserResponseDto;
import link.s_repo.chii_piyo.model.gen.Users;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class UserConverter {
    public UserResponseDto toUserResponseDto(Users user, URI presignedUrl, List<Long> scopeSharingGroups) {
        return new UserResponseDto(
            user.getId(),
            user.getCognitoUserId(),
            user.getDisplayName(),
            user.getEmail(),
            presignedUrl,
            scopeSharingGroups,
            user.getIsDarkMode(),
            user.getIsEasyMode(),
            user.getRole() != null ? UserResponseDto.RoleEnum.fromValue(user.getRole()) :
                UserResponseDto.RoleEnum.VIEWER,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
