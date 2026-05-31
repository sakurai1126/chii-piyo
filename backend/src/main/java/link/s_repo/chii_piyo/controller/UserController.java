package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.UserConverter;
import link.s_repo.chii_piyo.controller.converter.UserGenerateIconDataConverter;
import link.s_repo.chii_piyo.controller.gen.UserManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserManagementApi {
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;
    private final UserConverter userConverter;
    private final UserGenerateIconDataConverter userGenerateIconDataConverter;

    /**
     * GET /users/me
     * 現在ログインしているユーザーのログイン情報を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return 取得した現在のユーザー情報
     */
    @Override
    public ResponseEntity<UserResponseDto> getMe(String xRequestedWith) {
        Long currentUserId = currentUserProvider.getUserId();
        Users currentUser = userService.getUserById(currentUserId);

        URI presignedUrl = userService.generateIconDownloadPresignedUrl(currentUser);
        return ResponseEntity.ok(userConverter.toUserResponseDto(currentUser, presignedUrl));
    }

    /**
     * PUT /users/me
     * 現在ログインしているユーザーのログイン情報を更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param userUpdateData 更新するユーザー情報
     * @return 取得した現在のユーザー情報
     */
    @Override
    public ResponseEntity<UserResponseDto> updateMe(
        String xRequestedWith, UserUpdateRequestDto userUpdateData) {
        Long currentUserId = currentUserProvider.getUserId();

        // サービス層でS3キーを更新し更新後のユーザー情報を受け取る
        Users updatedUser = userService.updateMe(currentUserId, userUpdateData);
        URI presignedUrl = userService.generateIconDownloadPresignedUrl(updatedUser);
        UserResponseDto response = userConverter.toUserResponseDto(updatedUser, presignedUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /users/me/icon
     * ログインユーザーのアイコン用情報を受信し、S3署名付きURLを取得
     *
     * @param xRequestedWith       X-Requested-With ヘッダ (CSRF防御用)
     * @param userGenerateIconData 生成するユーザーのアイコン画像のファイル名情報
     * @return 生成後のユーザーの情報
     */
    @Override
    public ResponseEntity<UserGenerateIconDataResponseDto> generateIconPresignedUrl(
        String xRequestedWith, UserUpdateIconRequestDto userGenerateIconData) {
        // サービス層で署名付きURLを発行
        UserService.CreateIconS3KeyResult result =
            userService.generateIconPresignedUrl(
                userGenerateIconData.getFilename(),
                userGenerateIconData.getContentType()
            );

        // レスポンスDTOを構築
        UserGenerateIconDataResponseDto response = userGenerateIconDataConverter.toUserGenerateIconDataResponseDto(
            result.s3Key(),
            result.presignedUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /users
     * ユーザー情報の一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return 取得したユーザー一覧情報
     */
    @Override
    public ResponseEntity<List<UserResponseDto>> getUsers(String xRequestedWith) {
        // サービス層でユーザー情報一覧とアイコンダウンロード用署名付きURLを取得する
        List<UserService.UsersAndIconResult> usersAndIcon = userService.getUsersAndIcon();

        // レスポンスDTOに変換して返却する
        return ResponseEntity.ok(usersAndIcon
            .stream()
            .map(c -> userConverter.toUserResponseDto(c.user(), c.presignedUrl()))
            .toList()
        );
    }
}
