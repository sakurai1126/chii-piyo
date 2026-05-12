package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.controller.gen.MediaManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * メディア管理コントローラー<br>
 * OpenAPI Generator生成のMediaApiインターフェースを実装し、メタデータ登録とアップロード状態更新のAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MediaController implements MediaManagementApi {

    private final MediaService mediaService;
    private final MediaConverter mediaConverter;
    private final CurrentUserProvider currentUserProvider;
    private final TagService tagService;
    private final TagConverter tagConverter;

    /**
     * POST /media<br>
     * メディアのメタデータを登録し署名付きアップロードURLを取得する
     *
     * @param xRequestedWith  X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaUploadData アップロードリクエストDTO
     * @return 作成されたメディアID と 署名付きURL
     */
    @Override
    public ResponseEntity<MediaUploadResponseDto> createMedia(
        String xRequestedWith,
        MediaUploadRequestDto mediaUploadData
    ) {
        // 認証情報からアプリケーション側のユーザーIDを取得
        Long userId = currentUserProvider.getUserId();

        // サービス層でメタデータ登録 + 署名付きURL発行
        MediaService.CreateMediaResult result = mediaService.createMedia(
            userId,
            mediaUploadData.getMediaType().getValue(),
            mediaUploadData.getOriginalFilename(),
            mediaUploadData.getContentType(),
            mediaUploadData.getFileSize(),
            mediaUploadData.getWidth().orElse(null),
            mediaUploadData.getHeight().orElse(null),
            mediaUploadData.getTakenAt().orElse(null),
            mediaUploadData.getAlbumId().orElse(null),
            mediaUploadData.getSharingGroupId()
        );

        // レスポンスDTOを構築
        MediaUploadResponseDto response = new MediaUploadResponseDto(
            result.media().getId(),
            result.presignedUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /media/{id}/status<br>
     * メディアのアップロード状態を更新する<br>
     * シーケンス図の S3 アップロード成功/失敗後の状態同期処理に該当する
     *
     * @param xRequestedWith        X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId               対象のメディアID
     * @param mediaUpdateStatusData ステータス更新DTO
     * @return 更新後のメディア情報
     */
    @Override
    public ResponseEntity<MediaResponseDto> updateMediaUploadStatus(
        String xRequestedWith,
        Long mediaId,
        MediaUploadStatusRequestDto mediaUpdateStatusData
    ) {

        // 認証情報からアプリケーション側のユーザーIDを取得
        Long userId = currentUserProvider.getUserId();

        // サービス層でステータス更新
        Media updated = mediaService.updateUploadStatus(
            mediaId,
            userId,
            mediaUpdateStatusData.getUploadStatus().getValue()
        );

        // メディアに紐づくタグを取得してDTOに変換
        List<Tags> tags = tagService.findMediaTags(mediaId);
        List<TagResponseDto> tagsDto = tags.stream()
            .map(tagConverter::toTagResponseDto)
            .toList();

        // レスポンスDTOに変換して返却
        return ResponseEntity.ok(mediaConverter.toMediaResponseDto(updated, tagsDto));
    }

    // ====================================================================
    // 以下別Issueで実装予定のメソッド
    // OpenAPI Generator の interfaceOnly:true 設定によりコンパイル時に実装が必須となるため一旦スタブ化
    // ====================================================================

    /**
     * GET /media : メディア一覧を取得
     */
    @Override
    public ResponseEntity<MediaListResponseDto> getMediaList(
        String xRequestedWith,
        Integer offset,
        Integer limit,
        String mediaKind,
        Long albumId,
        Long tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /media/{id} : メディアをID指定で1件取得
     */
    @Override
    public ResponseEntity<MediaResponseDto> getMedia(String xRequestedWith, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /media/{id} : メディア情報を更新
     */
    @Override
    public ResponseEntity<MediaResponseDto> updateMedia(
        String xRequestedWith,
        Long id,
        MediaUpdateRequestDto mediaUpdateData
    ) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /media/{id} : メディアを削除
     */
    @Override
    public ResponseEntity<Void> deleteMedia(String xRequestedWith, Long id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

}
