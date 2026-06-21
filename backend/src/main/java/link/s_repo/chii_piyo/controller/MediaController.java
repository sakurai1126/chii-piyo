package link.s_repo.chii_piyo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import link.s_repo.chii_piyo.controller.converter.*;
import link.s_repo.chii_piyo.controller.gen.MediaManagementApi;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.*;
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
import java.util.Set;
import java.util.stream.Collectors;

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
    private final TagService tagService;
    private final TagConverter tagConverter;
    private final MediaNavigationConverter mediaNavigationConverter;
    private final MediaUploadConverter mediaUploadConverter;
    private final FavoriteService favoriteService;
    private final TrashService trashService;

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
        MediaUploadRequestDto mediaUploadData) {
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
            mediaUploadData.getSharingGroupId().orElse(null));

        // レスポンスDTOを構築
        MediaUploadResponseDto response = mediaUploadConverter.toMediaUploadResponseDto(
            result.media().getId(),
            result.presignedUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PATCH /media/{id}/status<br>
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
        MediaUploadStatusRequestDto mediaUpdateStatusData) {

        // 認証情報からアプリケーション側のユーザーIDを取得
        Long userId = currentUserProvider.getUserId();

        // サービス層でステータス更新
        Media media = mediaService.updateUploadStatus(
            mediaId,
            userId,
            mediaUpdateStatusData.getUploadStatus().getValue());

        // レスポンスDTOに変換して返却
        return ResponseEntity.ok(mediaConverter.toMediaResponseDto(
            media,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ));
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
     * @param excludeAlbumId 除外するアルバムIDフィルタ
     * @param tagId          タグIDフィルタのリスト
     * @param sharingGroupId 共有グループIDフィルタ
     * @param startDate      撮影日の開始日フィルタ
     * @param endDate        撮影日の終了日フィルタ
     * @param isFavorite     お気に入りフィルタ
     * @return MediaListResponseDto
     */
    @Override
    public ResponseEntity<MediaListResponseDto> getMediaList(
        String xRequestedWith,
        Integer offset,
        Integer limit,
        String mediaType,
        Long albumId,
        Long excludeAlbumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFavorite) {
        // 認証情報から現在のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();

        // 総件数を取得
        Long totalCount = mediaService.countMedia(
            mediaType, albumId, excludeAlbumId, tagId, sharingGroupId,
            startDate, endDate, isFavorite, currentUserId
        );

        // サービス層でメディアを取得
        List<Media> mediaList = mediaService.getMediaList(
            offset, limit, mediaType, albumId, excludeAlbumId, tagId,
            sharingGroupId, startDate, endDate, isFavorite, currentUserId
        );

        // 対象メディアのお気に入り情報を取得
        List<Favorites> favoriteList = favoriteService.getFavoriteList(mediaList);

        // 事前にMediaIDをキーにしてユーザーIDを取得できるように変換
        Map<Long, List<Long>> favoriteUserIdsByMediaId = favoriteList.stream()
            .collect(Collectors.groupingBy(
                Favorites::getMediaId,
                Collectors.mapping(Favorites::getUserId, Collectors.toList())
            ));

        // 現在のユーザーがお気に入りに追加したメディアのIDリストを取得
        Set<Long> favoritedMediaIds = favoriteList.stream()
            .filter(favorite -> favorite.getUserId().equals(currentUserId))
            .map(Favorites::getMediaId)
            .collect(Collectors.toSet());


        // hasNextの判定
        boolean hasNext = offset + mediaList.size() < totalCount;

        List<Long> mediaIds = mediaList.stream().map(Media::getId).toList();

        Map<Long, Long> commentCountsByMediaId = mediaCommentService.getCommentCountsByMediaIds(mediaIds);


        // コンバータでMediaResponseDtoのリストに変換する
        List<MediaResponseDto> responseMediaList = mediaList.stream()
            .map(media -> {
                URI thumbnailPresignedUrl = media.getThumbnailS3Key() != null
                    ? s3Service.generateDownloadPresignedUrl(media.getThumbnailS3Key(),
                    media.getOriginalFilename())
                    : null;

                Long commentCount = commentCountsByMediaId.getOrDefault(media.getId(), 0L);

                // 現在のユーザーがお気に入りに追加しているかを判定
                Boolean isFavoriteMedia = favoritedMediaIds.contains(media.getId());

                // メディアIDに紐づくお気に入りユーザーIDのリストを取得
                List<Long> addFavoriteUserIds = favoriteUserIdsByMediaId.getOrDefault(media.getId(), List.of());

                return mediaConverter.toMediaResponseDto(
                    media,
                    null,
                    null,
                    thumbnailPresignedUrl,
                    isFavoriteMedia,
                    commentCount,
                    null,
                    null,
                    null,
                    null,
                    addFavoriteUserIds
                );
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
        // サービス層でIDに基づくメディアを取得する
        Media media = mediaService.getMedia(id);

        URI presignedUrl = s3Service.generateDownloadPresignedUrl(media.getS3Key(), media.getOriginalFilename());

        // メディアに紐づくタグを取得してDTOに変換する
        List<TagResponseDto> tags = tagService.getMediaTags(id)
            .stream().map(c -> tagConverter.toTagResponseDto(c, null)).toList();


        // メディアの前後のナビゲーション情報(メディア情報と位置)を取得する
        List<MediaService.GetMediaNavigationResult> mediaNavigation =
            mediaService.getMediaNavigation(id);

        MediaNavigationResponseDto nextMedia = null;
        MediaNavigationResponseDto secondNextMedia = null;
        MediaNavigationResponseDto previousMedia = null;
        MediaNavigationResponseDto secondPreviousMedia = null;

        for (MediaService.GetMediaNavigationResult nav : mediaNavigation) {
            // ナビゲーション対象のメディアのサムネイル画像の署名付きURLを生成する
            URI navMediaPresignedUrl = nav.media().getThumbnailS3Key() != null
                ? s3Service.generateDownloadPresignedUrl(
                nav.media().getThumbnailS3Key(), nav.media().getOriginalFilename())
                : null;

            // DTOに変換
            MediaNavigationResponseDto dto = mediaNavigationConverter.toMediaNavigationResponseDto(
                nav.media(),
                navMediaPresignedUrl
            );

            // switch文で各変数に振り分ける
            switch (nav.position()) {
                case MediaService.NavigationPosition.NEXT_1 -> nextMedia = dto;
                case MediaService.NavigationPosition.NEXT_2 -> secondNextMedia = dto;
                case MediaService.NavigationPosition.PREVIOUS_1 -> previousMedia = dto;
                case MediaService.NavigationPosition.PREVIOUS_2 -> secondPreviousMedia = dto;
            }
        }

        URI thumbnailPresignedUrl = media.getThumbnailS3Key() != null
            ? s3Service.generateDownloadPresignedUrl(media.getThumbnailS3Key(), media.getOriginalFilename())
            : null;

        // 認証情報から現在のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();
        Boolean isFavorite = favoriteService.getCurrentUserIsFavorite(id, currentUserId);

        List<Long> addFavoriteUserIds = favoriteService.getAddFavoriteUserIds(id);

        return ResponseEntity.ok(mediaConverter.toMediaResponseDto(
            media,
            tags,
            presignedUrl,
            thumbnailPresignedUrl,
            isFavorite,
            null,
            nextMedia,
            secondNextMedia,
            previousMedia,
            secondPreviousMedia,
            addFavoriteUserIds
        ));
    }

    /**
     * PATCH /media/{id}<br>
     * メディア情報を更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             対象のメディアID
     * @param updateData     更新用データ（アルバムID と 共有グループIDを想定）
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateMedia(
        String xRequestedWith,
        Long id,
        MediaUpdateRequestDto updateData) {

        // サービス層でデータを更新
        mediaService.updateMedia(id, updateData);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }


    /**
     * DELETE /media/{id}<br>
     * メディアを削除<br>
     * ※ゴミ箱に移動し30日間保持
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             対象のメディアID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteMedia(String xRequestedWith, Long id) {
        // メディアの存在チェック
        mediaService.getMedia(id);

        // Trashサービス層を呼び出しフラグデータを追加する
        trashService.createTrashItem(id);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /media<br>
     * 複数メディアを削除<br>
     * ※ゴミ箱に移動し30日間保持
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaIds       対象メディアのIDリスト
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteMultipleMedia(String xRequestedWith, List<Long> mediaIds) {
        // 重複を削除しメディアの存在チェック
        List<Long> distinctMediaIds = mediaIds.stream().distinct().toList();
        List<Media> mediaList = mediaService.getMediabyIds(distinctMediaIds);

        if (mediaList.size() != distinctMediaIds.size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaIds);
        }

        // Trashサービス層を呼び出しフラグデータを追加する
        trashService.createTrashItems(distinctMediaIds);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /media/batch : メディアのタグ/共有範囲/アルバムを一括更新
     *
     * @param xRequestedWith       X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaBatchUpdateData 更新用のデータ
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateMediaBatch(
        String xRequestedWith, MediaBatchUpdateRequestDto mediaBatchUpdateData) {

        // サービス層でデータを更新
        mediaService.updateMediaBatch(mediaBatchUpdateData);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }
}
