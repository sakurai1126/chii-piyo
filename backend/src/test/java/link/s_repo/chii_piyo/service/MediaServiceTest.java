package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.common.S3KeyGenerator;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.component.ThumbnailGenerator;
import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.MediaSearchCriteria;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaBatchUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.MediaUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.AlbumRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.SharingGroupRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private S3StorageManager s3StorageManager;
    @Mock
    private ThumbnailGenerator thumbnailGenerator;
    @Mock
    private S3KeyGenerator s3KeyGenerator;
    @Mock
    private SharingGroupRepository sharingGroupRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private MediaService mediaService;

    @Nested
    @DisplayName("getMedia - メディアの取得")
    class GetMedia {
        @Test
        @DisplayName("Media-01: ID指定でメディアの取得ができること")
        void getMedia_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Media mockMedia = new Media();
            Long mockUserId = 2L;

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findById(requestId, mockUserId)).thenReturn(Optional.of(mockMedia));

            // 対象の実行
            Media result = mediaService.getMedia(requestId);

            // 結果の確認
            assertThat(result).isSameAs(mockMedia);

            // 取得処理が呼ばれていることを確認
            verify(currentUserProvider).getUserId();
            verify(mediaRepository).findById(requestId, mockUserId);
        }

        @Test
        @DisplayName("Media-02: 存在しないか、権限外のメディアのID指定でリクエストした場合例外にて処理されること")
        void getMedia_notFound() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Long mockUserId = 2L;

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findById(requestId, mockUserId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.getMedia(requestId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getMediabyIds - メディアのID指定複数取得")
    class GetMediaByIds {
        @Test
        @DisplayName("Media-03: ID指定で複数メディアの取得ができること")
        void getMediabyIds_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Media mockMedia = new Media();
            Long mockUserId = 2L;

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(List.of(requestId), mockUserId)).thenReturn(List.of(mockMedia));

            // 対象の実行
            List<Media> results = mediaService.getMediabyIds(List.of(requestId));

            // 結果の確認
            assertThat(results.getFirst()).isSameAs(mockMedia);

            // 取得処理が呼ばれていることを確認
            verify(currentUserProvider).getUserId();
            verify(mediaRepository).findByIds(List.of(requestId), mockUserId);
        }
    }

    @Nested
    @DisplayName("countMedia - メディアの件数取得")
    class CountMedia {
        @Test
        @DisplayName("Media-04: メディア件数の取得ができること")
        void countMedia_success() {
            // リクエストデータの作成
            MediaSearchCriteria mockMediaSearchCriteria = new MediaSearchCriteria(
                0,
                20,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1L
            );

            // モックデータの作成
            Long mockCount = 1L;

            // 取得処理のスタブ化
            when(mediaRepository.countMedia(mockMediaSearchCriteria)).thenReturn(mockCount);

            // 対象の実行
            Long results = mediaService.countMedia(mockMediaSearchCriteria);

            // 結果の確認
            assertThat(results).isEqualTo(mockCount);

            // 取得処理が呼ばれていることを確認
            verify(mediaRepository).countMedia(mockMediaSearchCriteria);
        }
    }

    @Nested
    @DisplayName("getMediaList - メディア一覧の取得")
    class GetMediaList {
        @Test
        @DisplayName("Media-05: メディア一覧の取得ができること")
        void getMediaList_success() {
            // リクエストデータの作成
            MediaSearchCriteria mockMediaSearchCriteria = new MediaSearchCriteria(
                0,
                20,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1L
            );

            // モックデータの作成
            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(mediaRepository.findBySearchCriteria(mockMediaSearchCriteria)).thenReturn(List.of(mockMedia));

            // 対象の実行
            List<Media> results = mediaService.getMediaList(mockMediaSearchCriteria);

            // 結果の確認
            assertThat(results.getFirst()).isEqualTo(mockMedia);

            // 取得処理が呼ばれていることを確認
            verify(mediaRepository).findBySearchCriteria(mockMediaSearchCriteria);
        }
    }

    @Nested
    @DisplayName("createMedia - メディア作成")
    class CreateMedia {
        @Test
        @DisplayName("Media-06: メディアの作成ができること")
        void createMedia_success() {
            // リクエストデータの作成
            Long resuestUserId = 1L;
            String requestMediaType = "PHOTO";
            String requestOriginalFilename = "image.png";
            String requestContentType = "image/png";
            Long requestFileSize = 300L;
            Integer requestWidth = 100;
            Integer requestHeight = 200;
            LocalDate requestTakenAt = LocalDate.now();
            Long requestAlbumId = 2L;
            Long requestSharingGroupId = 3L;

            // S3関連処理のモックデータの作成とスタブ化
            String mockS3Key = "media/s3key.png";
            URI mockUrl = URI.create("https://example.com/image.png");
            when(s3KeyGenerator.buildS3Key("media", requestOriginalFilename))
                .thenReturn(mockS3Key);
            when(s3StorageManager.generateUploadPresignedUrl(mockS3Key, requestContentType))
                .thenReturn(mockUrl);

            // 対象の実行
            MediaService.CreateMediaResult result = mediaService.createMedia(
                resuestUserId,
                requestMediaType,
                requestOriginalFilename,
                requestContentType,
                requestFileSize,
                requestWidth,
                requestHeight,
                requestTakenAt,
                requestAlbumId,
                requestSharingGroupId
            );

            // 結果の検証
            assertThat(result.media().getUploadedBy()).isEqualTo(resuestUserId);
            assertThat(result.media().getMediaType()).isEqualTo(requestMediaType);
            assertThat(result.media().getOriginalFilename()).isEqualTo(requestOriginalFilename);
            assertThat(result.media().getContentType()).isEqualTo(requestContentType);
            assertThat(result.media().getFileSize()).isEqualTo(requestFileSize);
            assertThat(result.media().getWidth()).isEqualTo(requestWidth);
            assertThat(result.media().getHeight()).isEqualTo(requestHeight);
            assertThat(result.media().getTakenAt()).isEqualTo(requestTakenAt);
            assertThat(result.media().getAlbumId()).isEqualTo(requestAlbumId);
            assertThat(result.media().getSharingGroupId()).isEqualTo(requestSharingGroupId);
            assertThat(result.media().getUploadStatus()).isEqualTo("PROCESSING");
            assertThat(result.media().getS3Key()).isEqualTo(mockS3Key);
            assertThat(result.presignedUrl()).isEqualTo(mockUrl);

            // S3関連処理が呼ばれていることを確認
            verify(s3KeyGenerator).buildS3Key("media", requestOriginalFilename);
            verify(mediaRepository).save(result.media());
            verify(s3StorageManager).generateUploadPresignedUrl(mockS3Key, requestContentType);
        }
    }

    @Nested
    @DisplayName("updateUploadStatus - アップロードステータスの更新")
    class UpdateUploadStatus {
        @Test
        @DisplayName("Media-07: アップロードステータスの更新ができること")
        void updateUploadStatus_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long requestUserId = 2L;
            String requestUploadStatus = "COMPLETED";

            // モックデータの作成
            String mockMediaType = "PHOTO";
            String mockS3Key = "media/image.png";
            String mockOriginalFilename = "image.png";
            Long mockFileSize = 100L;

            Media mockMedia = new Media();
            mockMedia.setUploadedBy(requestUserId);
            mockMedia.setMediaType(mockMediaType);
            mockMedia.setS3Key(mockS3Key);
            mockMedia.setOriginalFilename(mockOriginalFilename);
            mockMedia.setFileSize(mockFileSize);

            // 取得処理のスタブ化
            when(mediaRepository.findUnscopedById(requestMediaId)).thenReturn(Optional.of(mockMedia));

            // 対象の実行
            mediaService.updateUploadStatus(requestMediaId, requestUserId, requestUploadStatus);

            // 各処理が呼ばれていることを確認
            verify(mediaRepository).update(mockMedia);
            verify(thumbnailGenerator).generateThumbnailAsync(
                requestMediaId, mockMediaType, mockS3Key, mockOriginalFilename, mockFileSize);
        }

        @Test
        @DisplayName("Media-08: アップロードが完了していない場合サムネイル生成が起動されないこと")
        void updateUploadStatus_incomplete() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long requestUserId = 2L;
            String requestUploadStatus = "FAILED";

            Media mockMedia = new Media();
            mockMedia.setUploadedBy(requestUserId);

            // 取得処理のスタブ化
            when(mediaRepository.findUnscopedById(requestMediaId)).thenReturn(Optional.of(mockMedia));

            // 対象の実行
            mediaService.updateUploadStatus(requestMediaId, requestUserId, requestUploadStatus);

            // 各処理が呼ばれていることを確認
            verify(mediaRepository).update(mockMedia);

            // サムネイル生成が呼ばれていないことを確認
            verify(thumbnailGenerator, never()).generateThumbnailAsync(
                any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Media-09: 存在しないか、権限外のメディアのID指定でリクエストした場合例外にて処理されること")
        void updateUploadStatus_notFound() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long requestUserId = 2L;
            String requestUploadStatus = "COMPLETED";

            // 取得処理のスタブ化
            when(mediaRepository.findUnscopedById(requestMediaId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateUploadStatus(requestMediaId, requestUserId, requestUploadStatus))
                .isInstanceOf(ResourceNotFoundException.class);

            // 各処理が呼ばれていないことを確認
            verify(mediaRepository, never()).update(any());
            verify(thumbnailGenerator, never()).generateThumbnailAsync(any(), any(), any(), any(), any());
        }


        @Test
        @DisplayName("Media-10: 他ユーザーのメディアのステータス更新")
        void updateUploadStatus_accessDenied() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long requestUserId = 2L;
            String requestUploadStatus = "COMPLETED";

            // 別ユーザーIDを付与したモックデータの作成
            Media mockMedia = new Media();
            mockMedia.setUploadedBy(9L);

            // 取得処理のスタブ化
            when(mediaRepository.findUnscopedById(requestMediaId)).thenReturn(Optional.of(mockMedia));

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateUploadStatus(requestMediaId, requestUserId, requestUploadStatus))
                .isInstanceOf(ResourceAccessDeniedException.class);

            // 各処理が呼ばれていないことを確認
            verify(mediaRepository, never()).update(any());
            verify(thumbnailGenerator, never()).generateThumbnailAsync(any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("getMediaNavigation - メディア前後のメディア情報の取得")
    class GetMediaNavigation {
        @Test
        @DisplayName("Media-11: メディア前後のメディア情報の取得ができること")
        void getMediaNavigation_success() {
            // リクエストデータの作成
            Long requestMediaId = 2L;
            Long mockUserId = 10L;
            Media prevMediaFirst = new Media();
            Media prevMediaSecond = new Media();
            Media nextMediaFirst = new Media();
            Media nextMediaSecond = new Media();

            // 各取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            when(mediaRepository.findPreviousMedia(requestMediaId, mockUserId))
                .thenReturn(new ArrayList<>(List.of(prevMediaFirst, prevMediaSecond)));
            when(mediaRepository.findNextMedia(requestMediaId, mockUserId))
                .thenReturn(List.of(nextMediaFirst, nextMediaSecond));

            // 対象の実行
            List<MediaService.GetMediaNavigationResult> results =
                mediaService.getMediaNavigation(requestMediaId);

            // 結果の確認
            assertThat(results.size()).isEqualTo(4);
            assertThat(results.getFirst().position()).isEqualTo(MediaService.NavigationPosition.PREVIOUS_2);
            assertThat(results.get(1).position()).isEqualTo(MediaService.NavigationPosition.PREVIOUS_1);
            assertThat(results.get(2).position()).isEqualTo(MediaService.NavigationPosition.NEXT_1);
            assertThat(results.getLast().position()).isEqualTo(MediaService.NavigationPosition.NEXT_2);

            // 呼び出し確認
            verify(mediaRepository).findPreviousMedia(requestMediaId, mockUserId);
            verify(mediaRepository).findNextMedia(requestMediaId, mockUserId);
        }
    }

    @Nested
    @DisplayName("updateMedia - メディア情報の更新")
    class UpdateMedia {
        // 共通データの作成
        Long requestId = 1L;
        Long mockUserId = 2L;
        Long mockSharingGroupId = 3L;
        Long mockAlbumId = 4L;

        @Test
        @DisplayName("Media-12: メディア情報の更新ができること")
        void updateMedia_success() {
            // リクエストデータの作成
            MediaUpdateRequestDto requestUpdateData = new MediaUpdateRequestDto();
            requestUpdateData.setSharingGroupId(JsonNullable.of(mockSharingGroupId));
            requestUpdateData.setAlbumId(JsonNullable.of(mockAlbumId));

            // モックデータの作成
            Media mockMedia = new Media();
            mockMedia.setSharingGroupId(mockSharingGroupId);
            mockMedia.setAlbumId(mockAlbumId);

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findById(requestId, mockUserId)).thenReturn(Optional.of(mockMedia));
            when(sharingGroupRepository.findById(mockSharingGroupId)).thenReturn(Optional.of(new SharingGroups()));
            when(albumRepository.findById(mockAlbumId)).thenReturn(Optional.of(new Albums()));

            // 対象の実行
            mediaService.updateMedia(requestId, requestUpdateData);

            // リクエストデータで更新処理が呼ばれているか検証
            verify(mediaRepository).updateAll(
                argThat(media -> media.getSharingGroupId().equals(mockSharingGroupId)
                    && media.getAlbumId().equals(mockAlbumId)));
        }

        @Test
        @DisplayName("Media-13: 共有範囲更新で存在しない共有グループIDを指定して更新しようとすると例外で処理されること")
        void updateMedia_sharingGroupNotFound() {
            // リクエストデータの作成
            MediaUpdateRequestDto requestUpdateData = new MediaUpdateRequestDto();
            requestUpdateData.setSharingGroupId(JsonNullable.of(mockSharingGroupId));
            requestUpdateData.setAlbumId(JsonNullable.of(mockAlbumId));

            // モックデータの作成
            Media mockMedia = new Media();
            mockMedia.setSharingGroupId(mockSharingGroupId);

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findById(requestId, mockUserId)).thenReturn(Optional.of(mockMedia));
            when(sharingGroupRepository.findById(mockSharingGroupId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateMedia(requestId, requestUpdateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageStartingWith("共有グループが見つかりません");

            // 更新処理が呼ばれていないことを確認
            verify(mediaRepository, never()).updateAll(any());
        }

        @Test
        @DisplayName("Media-14: アルバム更新で存在しないアルバムIDを指定して更新しようとすると例外で処理されること")
        void updateMedia_albumNotFound() {
            // リクエストデータの作成
            MediaUpdateRequestDto requestUpdateData = new MediaUpdateRequestDto();
            requestUpdateData.setSharingGroupId(JsonNullable.of(mockSharingGroupId));
            requestUpdateData.setAlbumId(JsonNullable.of(mockAlbumId));

            // モックデータの作成
            Media mockMedia = new Media();
            mockMedia.setSharingGroupId(mockSharingGroupId);
            mockMedia.setAlbumId(mockAlbumId);

            // 取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findById(requestId, mockUserId)).thenReturn(Optional.of(mockMedia));
            when(sharingGroupRepository.findById(mockSharingGroupId)).thenReturn(Optional.of(new SharingGroups()));
            when(albumRepository.findById(mockAlbumId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateMedia(requestId, requestUpdateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageStartingWith("アルバムが見つかりません");


            // 更新処理が呼ばれていないことを確認
            verify(mediaRepository, never()).updateAll(any());
        }
    }

    @Nested
    @DisplayName("updateMediaBatch - メディアの一括更新")
    class UpdateMediaBatch {
        // 共通リクエストデータの作成
        Long mockUserId = 1L;
        List<Long> mockMediaIds = List.of(2L);
        Long mockSharingGroupId = 3L;
        List<Long> mockTagIds = List.of(4L);

        @Test
        @DisplayName("Media-15: メディアの一括更新ができること")
        void updateMediaBatch_success() {
            // リクエストデータの作成
            MediaBatchUpdateRequestDto requestUpdateData = new MediaBatchUpdateRequestDto();
            requestUpdateData.setMediaIds(mockMediaIds);
            requestUpdateData.setSharingGroupId(JsonNullable.of(mockSharingGroupId));
            requestUpdateData.setTagIds(JsonNullable.of(mockTagIds));

            // 取得および存在チェックのスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockUserId))
                .thenReturn(List.of(new Media()));
            when(sharingGroupRepository.findById(mockSharingGroupId))
                .thenReturn(Optional.of(new SharingGroups()));
            when(tagRepository.countByTagIds(mockTagIds)).thenReturn(1L);

            // 対象の実行
            mediaService.updateMediaBatch(requestUpdateData);

            // 各更新処理が呼ばれていることを検証
            verify(tagRepository).deleteMediaTagsByMediaIds(mockMediaIds);
            verify(tagRepository).saveMediaTags(argThat(insertList ->
                insertList.size() == 1
                    && insertList.getFirst().getMediaId().equals(mockMediaIds.getFirst())
            ));
            verify(mediaRepository).updateSharingGroupBatch(requestUpdateData);
        }

        @Test
        @DisplayName("Media-16: タグIDが空の場合既存のタグが一括削除されること")
        void updateMediaBatch_tagIdEmptyList() {
            MediaBatchUpdateRequestDto requestUpdateData = new MediaBatchUpdateRequestDto();
            requestUpdateData.setMediaIds(mockMediaIds);
            requestUpdateData.setTagIds(JsonNullable.of(List.of()));

            // 取得および存在チェックのスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockUserId))
                .thenReturn(List.of(new Media()));

            // 対象の実行
            mediaService.updateMediaBatch(requestUpdateData);

            // タグの削除処理が呼ばれていることを検証
            verify(tagRepository).deleteMediaTagsByMediaIds(mockMediaIds);

            // タグの追加処理が呼ばれていないことを確認
            verify(tagRepository, never()).saveMediaTags(any());
        }

        @Test
        @DisplayName("Media-17: 存在しないメディアIDが含まれている場合例外で処理されること")
        void updateMediaBatch_mediaNotFound() {
            // リクエストデータの作成
            MediaBatchUpdateRequestDto requestUpdateData = new MediaBatchUpdateRequestDto();
            requestUpdateData.setMediaIds(mockMediaIds);

            // 取得および存在チェックのスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockUserId)).thenReturn(List.of());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateMediaBatch(requestUpdateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageStartingWith("メディアが見つかりません");

            // 各更新処理が呼ばれていないことを検証
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(tagRepository, never()).saveMediaTags(any());
            verify(mediaRepository, never()).updateSharingGroupBatch(any());
        }

        @Test
        @DisplayName("Media-18: 存在しない共有グループIDを指定した場合例外で処理されること")
        void updateMediaBatch_sharingGroupNotFound() {
            // リクエストデータの作成
            MediaBatchUpdateRequestDto requestUpdateData = new MediaBatchUpdateRequestDto();
            requestUpdateData.setMediaIds(mockMediaIds);
            requestUpdateData.setSharingGroupId(JsonNullable.of(mockSharingGroupId));

            // 取得および存在チェックのスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockUserId))
                .thenReturn(List.of(new Media()));
            when(sharingGroupRepository.findById(mockSharingGroupId))
                .thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateMediaBatch(requestUpdateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageStartingWith("共有グループが見つかりません");

            // 各更新処理が呼ばれていないことを検証
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(tagRepository, never()).saveMediaTags(any());
            verify(mediaRepository, never()).updateSharingGroupBatch(any());
        }

        @Test
        @DisplayName("Media-19: 存在しないタグIDが含まれている場合例外で処理されること")
        void updateMediaBatch_tagNotFound() {
            // リクエストデータの作成
            MediaBatchUpdateRequestDto requestUpdateData = new MediaBatchUpdateRequestDto();
            requestUpdateData.setMediaIds(mockMediaIds);
            requestUpdateData.setTagIds(JsonNullable.of(mockTagIds));

            // 取得および存在チェックのスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockUserId))
                .thenReturn(List.of(new Media()));
            when(tagRepository.countByTagIds(mockTagIds)).thenReturn(0L);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaService.updateMediaBatch(requestUpdateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageStartingWith("存在しないタグが含まれています");

            // 各更新処理が呼ばれていないことを検証
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(tagRepository, never()).saveMediaTags(any());
            verify(mediaRepository, never()).updateSharingGroupBatch(any());
        }
    }

    @Nested
    @DisplayName("getTrashItemAndMedia - ゴミ箱データと紐づくメディアデータ取得")
    class GetTrashItemAndMedia {

        @Test
        @DisplayName("Media-20: ゴミ箱データと紐づくメディアデータ取得ができること")
        void getTrashItemAndMedia_success() {
            // モックデータの作成
            Long mockMediaId = 1L;
            TrashItems mockTrashItem = new TrashItems();
            mockTrashItem.setMediaId(mockMediaId);

            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);

            // 取得処理のスタブ化
            when(mediaRepository.findUnscopedByIds(List.of(mockMediaId))).thenReturn(List.of(mockMedia));

            // 対象の実行
            List<MediaService.TrashItemAndMediaResult> results =
                mediaService.getTrashItemAndMedia(List.of(mockTrashItem));

            // 結果の確認
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.getFirst().trashItem()).isEqualTo(mockTrashItem);
            assertThat(results.getFirst().media()).isEqualTo(mockMedia);

            // 取得処理が呼ばれていることの確認
            verify(mediaRepository).findUnscopedByIds(List.of(mockMediaId));
        }

        @Test
        @DisplayName("Media-21: 空リクエストで呼び出されたとき空リストを返すこと")
        void getTrashItemAndMedia_empty() {
            // 対象の実行
            List<MediaService.TrashItemAndMediaResult> results = mediaService.getTrashItemAndMedia(List.of());

            // 結果の確認
            assertThat(results.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(mediaRepository, never()).findUnscopedByIds(any());
        }
    }
}
