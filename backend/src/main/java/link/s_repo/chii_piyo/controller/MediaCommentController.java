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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


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
     * DELETE /media/{mediaId}/comments/{id} : コメントを削除
     */
    @Override
    public ResponseEntity<Void> deleteMediaComment(String xRequestedWith, Long mediaId, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /media/{mediaId}/comments : メディアのコメント一覧を取得
     */
    @Override
    public ResponseEntity<List<MediaCommentResponseDto>> getMediaComments(
        String xRequestedWith, Long mediaId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /media/{mediaId}/comments/{id} : コメントを更新
     */
    @Override
    public ResponseEntity<MediaCommentResponseDto> updateMediaComment(
        String xRequestedWith, Long mediaId, Long id, MediaCommentRequestDto mediaCommentData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
