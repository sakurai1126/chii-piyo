package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.UserResponseDto;
import link.s_repo.chii_piyo.model.gen.Users;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserResponseDto toUserResponseDto(Users user) {
        return new UserResponseDto(
            user.getId(),
            user.getCognitoUserId(),
            user.getDisplayName(),
            user.getEmail(),
            user.getUserIconUrl(),
            user.getIsDarkMode(),
            user.getIsEasyMode(),
            user.getRole() != null ? UserResponseDto.RoleEnum.fromValue(user.getRole()) :
                UserResponseDto.RoleEnum.VIEWER,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
