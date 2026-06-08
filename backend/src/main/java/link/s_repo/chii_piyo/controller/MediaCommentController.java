package link.s_repo.chii_piyo.controller;


import link.s_repo.chii_piyo.controller.converter.MediaCommentConverter;
import link.s_repo.chii_piyo.controller.gen.MediaCommentManagementApi;

import link.s_repo.chii_piyo.model.gen.MediaCommentRequestDto;
import link.s_repo.chii_piyo.model.gen.MediaCommentResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.MediaCommentService;
import link.s_repo.chii_piyo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MediaCommentController implements MediaCommentManagementApi {
    private final MediaCommentService mediaCommentService;
    private final MediaCommentConverter mediaCommentConverter;
    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;

    /**
     * POST /media/{mediaId}/comments<br>
     * メディアにコメントを追加する
     *
     * @param xRequestedWith   X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId          メディアID
     * @param mediaCommentData コメントの内容を含むリクエストDTO
     * @return 作成されたcommentの情報
     */
    @Override
    public ResponseEntity<MediaCommentResponseDto> createMediaComment(
        String xRequestedWith, Long mediaId, MediaCommentRequestDto mediaCommentData) {
        // 認証情報からアプリケーション側のユーザーIDを取得
        Long userId = currentUserProvider.getUserId();

        Users user = userService.getUserById(userId);

        // サービス層でコメントを作成する
        MediaComments createMediaComment = mediaCommentService.createMediaComment(mediaId, userId, mediaCommentData.getContent());

        // 作成されたコメントをDTOに変換してレスポンスする
        MediaCommentResponseDto response =
            mediaCommentConverter.toMediaCommentResponseDto(createMediaComment, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /media/comments/{id}<br>
     * コメントを削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             対象のコメントID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteMediaComment(String xRequestedWith, Long id) {
        // 認証情報からアプリケーション側のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();

        // サービス層で削除処理
        mediaCommentService.deleteMediaComment(id, currentUserId);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /media/{mediaId}/comments
     * メディアのコメント一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId        メディアID
     * @return 取得したコメントの情報
     */
    @Override
    public ResponseEntity<List<MediaCommentResponseDto>> getMediaComments(
        String xRequestedWith, Long mediaId) {

        // サービス層でmediaIdに紐づくコメントを取得
        List<MediaComments> mediaComments = mediaCommentService.getMediaComments(mediaId);

        // コメントが0件だった場合この時点で空リストを返す
        if (mediaComments.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // コメントの情報からユーザーIDを抽出し重複削除
        List<Long> userIds = mediaComments.stream()
            .map(MediaComments::getUserId)
            .distinct()
            .toList();

        // ユーザー情報を取得しIDとユーザーデータのMapにする
        List<Users> users = userService.getUsersById(userIds);

        Map<Long, Users> userMap = users.stream()
            .collect(Collectors.toMap(Users::getId, user -> user));

        return ResponseEntity.ok(mediaComments
            .stream()
            .map(c -> mediaCommentConverter.toMediaCommentResponseDto(c,
                userMap.get(c.getUserId())))
            .toList());
    }
}
