package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.UserConverter;
import link.s_repo.chii_piyo.controller.converter.UserGenerateIconDataConverter;
import link.s_repo.chii_piyo.controller.gen.UserManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.SharingGroupService;
import link.s_repo.chii_piyo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * ユーザー管理コントローラー<br>
 * ユーザー情報の取得・更新に関するAPIエンドポイントを提供
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UserManagementApi {
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;
    private final UserConverter userConverter;
    private final UserGenerateIconDataConverter userGenerateIconDataConverter;
    private final SharingGroupService sharingGroupService;

    /**
     * GET /users/me<br>
     * 現在ログインしているユーザーのログイン情報を取得
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @return 取得した現在のユーザー情報
     */
    @Override
    public ResponseEntity<UserResponseDto> getMe(String xRequestedWith) {
        Long currentUserId = currentUserProvider.getUserId();
        Users currentUser = userService.getUserById(currentUserId);
        List<Long> scopeSharingGroups = sharingGroupService.getUserSharingScopes(currentUserId);

        URI presignedUrl = userService.generateIconDownloadPresignedUrl(currentUser);
        return ResponseEntity.ok(userConverter.toUserResponseDto(currentUser, presignedUrl, scopeSharingGroups));
    }

    /**
     * PATCH /users/me<br>
     * 現在ログインしているユーザーのログイン情報を更新
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param userUpdateData 更新するユーザー情報
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateMe(
        String xRequestedWith, UserUpdateRequestDto userUpdateData) {
        Long currentUserId = currentUserProvider.getUserId();

        // サービス層で更新処理
        userService.updateMe(currentUserId, userUpdateData);

        // 204ステータスを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /users/me/icon<br>
     * ログインユーザーのアイコン用情報を受信し、S3署名付きURLを取得
     *
     * @param xRequestedWith       CSRF防御用カスタムリクエストヘッダー
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
     * GET /users<br>
     * ユーザー情報の一覧を取得
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @return 取得したユーザー一覧情報
     */
    @Override
    public ResponseEntity<List<UserResponseDto>> getUsers(String xRequestedWith) {
        // サービス層でユーザー情報一覧とアイコンダウンロード用署名付きURLを取得する
        List<UserService.UsersAndIconResult> usersAndIcon = userService.getUsersAndIcon();

        // 取得したデータからユーザーIDを抽出し手リスト化
        List<Long> userIds = usersAndIcon.stream()
            .map(c -> c.user().getId())
            .toList();

        // IDリストから共有グループを一括取得してMap型で受け取る
        Map<Long, List<Long>> scopeMap = sharingGroupService.getUserSharingScopesBulk(userIds);

        return ResponseEntity.ok(
            usersAndIcon
                .stream()
                .map(c ->
                    userConverter.toUserResponseDto(
                        c.user(),
                        c.presignedUrl(),
                        // 事前準備したscopeMapから対象ユーザーのものを取得
                        scopeMap.getOrDefault(c.user().getId(), List.of())
                    )
                )
                .toList()
        );
    }

    /**
     * PATCH /users/{id}/role<br>
     * ログインユーザーの権限を更新
     *
     * @param xRequestedWith     CSRF防御用カスタムリクエストヘッダー
     * @param id                 ユーザーID
     * @param userUpdateRoleData 更新するユーザー情報
     * @return 更新後のユーザー情報 (status code 200)
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(
        String xRequestedWith, Long id, UserRoleUpdateRequestDto userUpdateRoleData) {

        // ログイン中のユーザーIDを受け取り自分のID変更の場合は例外で弾く
        Long currentUserId = currentUserProvider.getUserId();
        if (currentUserId.equals(id)) {
            throw new IllegalArgumentException("自分自身の権限は変更できません");
        }

        // サービス層で更新処理
        userService.updateRole(id, userUpdateRoleData);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }
}
