package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.AlbumRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private S3StorageManager s3StorageManager;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @InjectMocks
    private AlbumService albumService;

    @Nested
    @DisplayName("createAlbum - アルバム作成")
    class CreateAlbum {
        @Test
        @DisplayName("Album-01: アルバムを作成できること")
        void createAlbum_success() {
            // 対象の実行
            albumService.createAlbum("運動会");

            // 保存処理が呼ばれたことの確認
            verify(albumRepository).save(any(Albums.class));
        }
    }

    @Nested
    @DisplayName("getAlbums - アルバム一覧取得")
    class GetAlbums {
        @Test
        @DisplayName("Album-02: アルバム一覧を取得できること")
        void getAlbums_success() {
            // モックデータの作成
            Long mockAlbumId = 1L;
            Albums mockAlbum = new Albums();
            mockAlbum.setId(mockAlbumId);

            // 取得処理のスタブ化
            when(albumRepository.findAll()).thenReturn(List.of(mockAlbum));

            // 対象を呼び出して結果を取得
            List<Albums> result = albumService.getAlbums();

            // 結果が取得できているか確認
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(mockAlbumId);
        }
    }

    @Nested
    @DisplayName("getAlbumById - ID指定でのアルバム取得")
    class GetAlbumById {
        @Test
        @DisplayName("Album-03: ID指定でのアルバム取得ができること")
        void getAlbumById_success() {
            // モックデータの作成
            Long requestId = 1L;
            Albums mockAlbum = new Albums();
            mockAlbum.setId(1L);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));

            // 対象を呼び出して結果を取得
            Albums result = albumService.getAlbumById(requestId);

            // 結果が取得できているか確認
            assertThat(result.getId()).isEqualTo(requestId);
        }

        @Test
        @DisplayName("Album-04: 存在しないメディアIDのリクエストに例外で処理すること")
        void getAlbumById_notFound() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.empty());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.getAlbumById(requestId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getMediaDataByAlbumIds - アルバムID指定でアルバムに紐づくメディア数の取得")
    class GetMediaDataByAlbumIds {
        // 共通モックデータの作成
        Long mockCurrentUserId = 1L;

        // メディア作成ヘルパー
        private Media createMedia(Long id, Long albumId, String mediaType, String thumbnailKey) {
            Media media = new Media();
            media.setId(id);
            media.setAlbumId(albumId);
            media.setMediaType(mediaType);
            media.setThumbnailS3Key(thumbnailKey);
            media.setOriginalFilename("image.png");
            return media;
        }

        @Test
        @DisplayName("Album-05: アルバムID指定でアルバムに紐づく画像動画それぞれのメディア数取得ができること")
        void getMediaDataByAlbumIds_success() {
            // モックデータの作成
            Long mockAlbumId = 1L;
            List<Media> mockMediaList = List.of(
                createMedia(1L, mockAlbumId, "PHOTO", "thumbnail-1.png"),
                createMedia(2L, mockAlbumId, "PHOTO", "thumbnail-2.png"),
                createMedia(3L, mockAlbumId, "VIDEO", "thumbnail-3.png")
            );

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByAlbumIds(List.of(mockAlbumId), mockCurrentUserId))
                .thenReturn(mockMediaList);

            // 対象を実行し結果を取得
            Map<Long, AlbumService.MediaDataResult> result = albumService.getMediaDataByAlbumIds(List.of(mockAlbumId));

            // 返却されたアルバム数を検証
            assertThat(result).hasSize(1);
            // 画像のカウント数を検証
            assertThat(result.get(mockAlbumId).photoCount()).isEqualTo(2);
            // 動画のカウント数を検証
            assertThat(result.get(mockAlbumId).videoCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Album-06: 複数アルバムID指定でメディア取得ができること")
        void getMediaDataByAlbumIds_multipleAlbums() {
            // モックデータの作成
            Long mockAlbumId1 = 1L;
            Long mockAlbumId2 = 2L;
            List<Media> mockMediaList = List.of(
                createMedia(1L, mockAlbumId1, "PHOTO", "thumbnail-1.png"),
                createMedia(2L, mockAlbumId1, "PHOTO", "thumbnail-2.png"),
                createMedia(3L, mockAlbumId2, "PHOTO", "thumbnail-3.png"),
                createMedia(4L, mockAlbumId2, "VIDEO", "thumbnail-4.png")
            );

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByAlbumIds(List.of(mockAlbumId1, mockAlbumId2), mockCurrentUserId))
                .thenReturn(mockMediaList);

            // 対象を実行し結果を取得
            Map<Long, AlbumService.MediaDataResult> result = albumService.getMediaDataByAlbumIds(List.of(mockAlbumId1, mockAlbumId2));

            // 返却されたアルバム数を検証
            assertThat(result).hasSize(2);
            // 画像のカウント数を検証
            assertThat(result.get(mockAlbumId1).photoCount()).isEqualTo(2);
            assertThat(result.get(mockAlbumId2).photoCount()).isEqualTo(1);
            // 動画のカウント数を検証
            assertThat(result.get(mockAlbumId1).videoCount()).isEqualTo(0);
            assertThat(result.get(mockAlbumId2).videoCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Album-07: 空のアルバムIDリストを渡すと空の値が返り取得処理が呼ばれないこと")
        void getMediaDataByAlbumIds_emptyList() {
            // 対象を実行し結果を取得
            Map<Long, AlbumService.MediaDataResult> result = albumService.getMediaDataByAlbumIds(List.of());

            // 返却された値が空であることを確認
            assertThat(result).isEmpty();

            // 後続の取得処理が呼ばれていないことを確認
            verify(mediaRepository, never()).findByAlbumIds(any(), any());
        }

        @Test
        @DisplayName("Album-08: 4件以上のサムネイルカバー画像は3件までで返すこと")
        void getMediaDataByAlbumIds_limitCover() {
            // モックデータの作成
            Long mockAlbumId = 1L;
            List<Media> mediaList = List.of(
                createMedia(1L, mockAlbumId, "PHOTO", "thumbnail-1.png"),
                createMedia(2L, mockAlbumId, "PHOTO", "thumbnail-2.png"),
                createMedia(3L, mockAlbumId, "PHOTO", "thumbnail-3.png"),
                createMedia(4L, mockAlbumId, "PHOTO", "thumbnail-4.png")
            );

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByAlbumIds(List.of(mockAlbumId), mockCurrentUserId)).thenReturn(mediaList);
            when(s3StorageManager.generateDownloadPresignedUrl(any(), any()))
                .thenReturn(URI.create("https://example.com/thumbnail.png"));

            Map<Long, AlbumService.MediaDataResult> result =
                albumService.getMediaDataByAlbumIds(List.of(mockAlbumId));

            // 返却されたアルバム数を検証
            assertThat(result.get(mockAlbumId).photoCount()).isEqualTo(4);
            // URLは3件で返ることを検証
            assertThat(result.get(mockAlbumId).urls()).hasSize(3);
            // URL生成も3回しか呼ばれないことを検証
            verify(s3StorageManager, times(3)).generateDownloadPresignedUrl(any(), any());
        }

        @Test
        @DisplayName("Album-09: サムネイルがないメディアはカバー画像に含めないこと")
        void getMediaDataByAlbumIds_noThumbnail() {
            Long mockAlbumId = 1L;
            List<Media> mediaList = List.of(
                createMedia(1L, mockAlbumId, "PHOTO", null),
                createMedia(2L, mockAlbumId, "PHOTO", "thumbnail.png")
            );

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByAlbumIds(any(), any())).thenReturn(mediaList);
            when(s3StorageManager.generateDownloadPresignedUrl(any(), any()))
                .thenReturn(URI.create("https://example.com/thumbnail.png"));

            Map<Long, AlbumService.MediaDataResult> result =
                albumService.getMediaDataByAlbumIds(List.of(mockAlbumId));

            // 返却されたアルバム数を検証
            assertThat(result.get(mockAlbumId).photoCount()).isEqualTo(2);
            // URLは1件で返ることを検証
            assertThat(result.get(mockAlbumId).urls()).hasSize(1);
            // URL生成もnullを除外し1回しか呼ばれないことを検証
            verify(s3StorageManager, times(1)).generateDownloadPresignedUrl(any(), any());
        }
    }


    @Nested
    @DisplayName("updateAlbum - アルバムのタイトル更新")
    class UpdateAlbum {
        @Test
        @DisplayName("Album-10: アルバムのタイトルを更新できること")
        void updateAlbum_success() {
            // リクエストデータの作成
            Long requestId = 1L;
            String requestTitle = "運動会";
            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(requestId);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));

            // 対象の実行
            albumService.updateAlbum(requestId, requestTitle);

            // 更新処理が呼ばれたことの確認
            verify(albumRepository).update(mockAlbum);
        }
    }

    @Nested
    @DisplayName("deleteAlbum - アルバムの削除")
    class DeleteAlbum {
        @Test
        @DisplayName("Album-11: アルバムの削除ができること")
        void deleteAlbum_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(1L);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));

            // 対象の実行
            albumService.deleteAlbum(requestId);

            // 更新処理が呼ばれたことの確認
            verify(mediaRepository).clearAlbumId(requestId);
            verify(albumRepository).deleteById(requestId);
        }
    }

    @Nested
    @DisplayName("addAlbumMedia - メディアのアルバム追加処理")
    class AddAlbumMedia {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("Album-12: メディアのアルバム追加処理ができること")
        void addAlbumMedia_success() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of(2L);

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(1L);
            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));
            when(mediaRepository.findByIds(eq(requestMediaIds), any())).thenReturn(List.of(mockMedia));

            // 対象の実行
            albumService.addAlbumMedia(requestId, requestMediaIds);

            // 追加処理が呼ばれたことの確認
            verify(mediaRepository).updateAlbumIdByMediaIds(requestMediaIds, requestId);
        }

        @Test
        @DisplayName("Album-13: 空のメディアIDリストでアルバムへのメディア追加処理をリクエストすると例外で処理されること")
        void addAlbumMedia_emptyList() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of();

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.addAlbumMedia(requestId, requestMediaIds))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Album-14: 存在しないメディアIDを含めてアルバムへのメディア追加処理をリクエストすると例外で処理されること")
        void addAlbumMedia_notFound() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of(2L);

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(1L);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));
            when(mediaRepository.findByIds(eq(requestMediaIds), any())).thenReturn(List.of());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.addAlbumMedia(requestId, requestMediaIds))
                .isInstanceOf(ResourceNotFoundException.class);

            // 追加処理が呼ばれていないことの確認
            verify(mediaRepository, never()).updateAlbumIdByMediaIds(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteAlbumMedia - アルバムからのメディア削除処理")
    class DeleteAlbumMedia {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("Album-15: アルバムからのメディア削除処理ができること")
        void deleteAlbumMedia_success() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of(2L);

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(requestId);
            Media mockMedia = new Media();
            mockMedia.setAlbumId(requestId);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));
            when(mediaRepository.findByIds(eq(requestMediaIds), any())).thenReturn(List.of(mockMedia));

            // 対象の実行
            albumService.deleteAlbumMedia(requestId, requestMediaIds);

            // 削除処理が呼ばれていることの確認
            verify(mediaRepository).clearAlbumIdByMediaIds(requestMediaIds);
        }

        @Test
        @DisplayName("Album-16: 空のメディアIDリストでアルバムからのメディア削除処理をリクエストすると例外で処理されること")
        void deleteAlbumMedia_emptyList() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of();

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.deleteAlbumMedia(requestId, requestMediaIds))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Album-17: 存在しないメディアIDを含めてアルバムからのメディア削除処理をリクエストすると例外で処理されること")
        void deleteAlbumMedia_notFound() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of(2L);

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(requestId);

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));
            when(mediaRepository.findByIds(eq(requestMediaIds), any())).thenReturn(List.of());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.deleteAlbumMedia(requestId, requestMediaIds))
                .isInstanceOf(ResourceNotFoundException.class);

            // 削除処理が呼ばれていないことの確認
            verify(mediaRepository, never()).clearAlbumIdByMediaIds(any());
        }

        @Test
        @DisplayName("Album-18: アルバムに属さないメディアを含めてアルバムからのメディア削除処理をリクエストすると例外で処理されること")
        void deleteAlbumMedia_notInAlbum() {
            // リクエストデータの作成
            List<Long> requestMediaIds = List.of(2L);

            // モックデータの作成
            Albums mockAlbum = new Albums();
            mockAlbum.setId(requestId);
            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(albumRepository.findById(requestId)).thenReturn(Optional.of(mockAlbum));
            when(mediaRepository.findByIds(eq(requestMediaIds), any())).thenReturn(List.of(mockMedia));

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> albumService.deleteAlbumMedia(requestId, requestMediaIds))
                .isInstanceOf(IllegalArgumentException.class);

            // 削除処理が呼ばれていないことの確認
            verify(mediaRepository, never()).clearAlbumIdByMediaIds(any());
        }
    }
}
