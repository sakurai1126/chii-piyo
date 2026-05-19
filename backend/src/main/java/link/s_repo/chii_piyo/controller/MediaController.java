package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.MediaListConverter;
import link.s_repo.chii_piyo.controller.gen.MediaManagementApi;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.MediaCommentService;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private final MediaListConverter mediaListConverter;
    private final CurrentUserProvider currentUserProvider;
    private final MediaCommentService mediaCommentService;
    private final S3Service s3Service;

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
        Media media = mediaService.updateUploadStatus(
            mediaId,
            userId,
            mediaUpdateStatusData.getUploadStatus().getValue()
        );

        // レスポンスDTOに変換して返却
        return ResponseEntity.ok(mediaConverter.toMediaResponseDto(media, null, null,
            null, null, null));
    }


    /**
     * GET /media<br>
     * メディア一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param offset         ページネーションのオフセット
     * @param limit          ページネーションのリミット
     * @param mediaType      メディア種別フィルタ (IMAGE / VIDEO)
     * @param albumId        アルバムIDフィルタ
     * @param tagId          タグIDフィルタのリスト
     * @param sharingGroupId 共有グループIDフィルタ
     * @param startDate      撮影日の開始日フィルタ
     * @param endDate        撮影日の終了日フィルタ
     * @return MediaListResponseDto
     */
    @Override
    public ResponseEntity<MediaListResponseDto> getMediaList(
        String xRequestedWith,
        Integer offset,
        Integer limit,
        String mediaType,
        Long albumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        // 総件数を取得
        Long totalCount = mediaService.countMedia(mediaType, albumId, tagId, sharingGroupId, startDate, endDate);

        // サービス層でメディアを取得
        List<Media> mediaList = mediaService.getMediaList(offset, limit, mediaType, albumId, tagId, sharingGroupId, startDate, endDate);

        // hasNextの判定
        boolean hasNext = offset + mediaList.size() < totalCount;

        List<Long> mediaIds = mediaList.stream().map(Media::getId).toList();

        Map<Long, Long> commentCountsByMediaId =
            mediaCommentService.getCommentCountsByMediaIds(mediaIds);

        // コンバータでMediaResponseDtoのリストに変換する
        List<MediaResponseDto> responseMediaList = mediaList.stream()
            .map(media -> {
                URI thumbnailPresignedUrl = media.getThumbnailS3Key() != null
                    ? URI.create(s3Service.generateDownloadPresignedUrl(media.getThumbnailS3Key()))
                    : null;
                Long commentCount = commentCountsByMediaId.getOrDefault(media.getId(), 0L);
                return mediaConverter.toMediaResponseDto(media, null, null,
                    thumbnailPresignedUrl, null, commentCount);
            })
            .toList();

        // コンバータで一覧用DTOに変換する
        MediaListResponseDto response = mediaListConverter.toMediaListResponseDto(
            responseMediaList,
            totalCount, hasNext);

        return ResponseEntity.ok(response);
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
