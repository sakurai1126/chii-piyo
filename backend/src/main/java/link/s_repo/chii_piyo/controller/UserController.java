package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.UserConverter;
import link.s_repo.chii_piyo.controller.gen.UserManagementApi;
import link.s_repo.chii_piyo.model.gen.UserResponseDto;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserManagementApi {
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;
    private final UserConverter userConverter;

    /**
     * GET /media/{mediaId}/comments
     *
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return 取得した現在のユーザー情報
     */
    @Override
    public ResponseEntity<UserResponseDto> getMe(String xRequestedWith) {
        Long currentUserId = currentUserProvider.getUserId();
        Users currentUser = userService.getUserById(currentUserId);

        return ResponseEntity.ok(userConverter.toUserResponseDto(currentUser));
    }
}
