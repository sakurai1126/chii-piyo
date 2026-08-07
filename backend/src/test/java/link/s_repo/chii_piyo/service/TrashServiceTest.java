package link.s_repo.chii_piyo.service;


import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import link.s_repo.chii_piyo.repository.FirstRecordRepository;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import link.s_repo.chii_piyo.repository.TrashRepository;
import link.s_repo.chii_piyo.repository.WordRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrashServiceTest {
    @Mock
    private TrashRepository trashRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private S3StorageManager s3StorageManager;
    @Mock
    private MediaCommentRepository mediaCommentRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private FirstRecordRepository firstRecordRepository;
    @Mock
    private WordRecordRepository wordRecordRepository;

    @InjectMocks
    private TrashService trashService;

    InOrder createInOrder() {
        return inOrder(mediaCommentRepository,
            favoriteRepository,
            tagRepository,
            firstRecordRepository,
            wordRecordRepository,
            trashRepository,
            mediaRepository,
            s3StorageManager);
    }


    @Nested
    @DisplayName("createTrashItem - ゴミ箱データの作成")
    class CreateTrashItem {
        @Test
        @DisplayName("Trash-01: ゴミ箱データの作成ができること")
        void createTrashItem_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 対象の実行
            trashService.createTrashItem(requestId);

            // 作成処理が呼ばれていて予定時刻が30日後のAM2:00に指定されていることの確認
            LocalDate expectedDate = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(30);
            verify(trashRepository).save(argThat(item ->
                requestId.equals(item.getMediaId())
                    && expectedDate.equals(item.getExpiresAt().toLocalDate())
                    && item.getExpiresAt().getHour() == 2
                    && item.getExpiresAt().getMinute() == 0
            ));
        }
    }

    @Nested
    @DisplayName("createTrashItems - ゴミ箱データの複数作成")
    class CreateTrashItems {
        @Test
        @DisplayName("Trash-02: ゴミ箱データの複数作成ができること")
        void createTrashItems_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 対象の実行
            trashService.createTrashItems(List.of(requestId));

            // 作成処理が呼ばれていて予定時刻が30日後のAM2:00に指定されていることの確認
            LocalDate expectedDate = LocalDate.now(ZoneId.of("Asia/Tokyo")).plusDays(30);
            verify(trashRepository).saveAll(argThat(item ->
                item.size() == 1 &&
                    requestId.equals(item.getFirst().getMediaId())
                    && expectedDate.equals(item.getFirst().getExpiresAt().toLocalDate())
                    && item.getFirst().getExpiresAt().getHour() == 2
                    && item.getFirst().getExpiresAt().getMinute() == 0
            ));
        }
    }

    @Nested
    @DisplayName("getTrashItems - ゴミ箱内データの一覧取得")
    class GetTrashItems {
        @Test
        @DisplayName("Trash-03: ゴミ箱内データの一覧取得ができること")
        void getTrashItems_success() {
            // リクエストデータの作成
            Integer requestOffset = 0;
            Integer requestLimit = 20;

            // モックデータの作成
            TrashItems mockItem = new TrashItems();

            // 取得処理のスタブ化
            when(trashRepository.findAll(requestOffset, requestLimit)).thenReturn(List.of(mockItem));

            // 対象の実行
            List<TrashItems> result = trashService.getTrashItems(requestOffset, requestLimit);

            // 結果の検証
            assertThat(result).hasSize(1);

            // 取得処理が呼ばれていることの確認
            verify(trashRepository).findAll(requestOffset, requestLimit);
        }
    }

    @Nested
    @DisplayName("getTotalCount - ゴミ箱内データ総件数の取得")
    class GetTotalCount {
        @Test
        @DisplayName("Trash-04: ゴミ箱内データ総件数の取得ができること")
        void getTotalCount_success() {
            // モックデータの作成
            Long mockCount = 1L;

            // 取得処理のスタブ化
            when(trashRepository.count()).thenReturn(mockCount);

            // 対象の実行
            Long result = trashService.getTotalCount();

            // 結果の検証
            assertThat(result).isEqualTo(mockCount);

            // 取得処理が呼ばれていることの確認
            verify(trashRepository).count();
        }
    }

    @Nested
    @DisplayName("getEarliestDeadline - 完全削除まで最も近いメディアの残り日数の取得")
    class GetEarliestDeadline {
        @Test
        @DisplayName("Trash-05: 完全削除まで最も近いメディアの残り日数の取得ができること")
        void getEarliestDeadline_success() {
            // 残り日数
            Long mockDeadline = 1L;

            // モックデータの作成
            TrashItems mockOldestItem = new TrashItems();

            // 残り日数分を追加した日を削除日時としてセット
            mockOldestItem.setExpiresAt(OffsetDateTime
                .now(ZoneId.of("Asia/Tokyo"))
                .plusDays(mockDeadline));

            // 取得処理のスタブ化
            when(trashRepository.findOldest()).thenReturn(Optional.of(mockOldestItem));

            // 対象の実行
            Long result = trashService.getEarliestDeadline();

            // 結果の検証
            assertThat(result).isEqualTo(mockDeadline);

            // 取得処理が呼ばれていることの確認
            verify(trashRepository).findOldest();
        }

        @Test
        @DisplayName("Trash-06: 当日の残り日数の取得ができること")
        void getEarliestDeadline_today() {
            // モックデータの作成
            TrashItems mockOldestItem = new TrashItems();
            mockOldestItem.setExpiresAt(OffsetDateTime.now(ZoneId.of("Asia/Tokyo")));

            // 取得処理のスタブ化
            when(trashRepository.findOldest()).thenReturn(Optional.of(mockOldestItem));

            // 対象の実行
            Long result = trashService.getEarliestDeadline();

            // 結果の検証
            assertThat(result).isEqualTo(0L);
        }

        @Test
        @DisplayName("Trash-07: ゴミ箱が空の場合nullが返ること")
        void getEarliestDeadline_empty() {
            // 取得処理のスタブ化
            when(trashRepository.findOldest()).thenReturn(Optional.empty());

            // 対象の実行
            Long result = trashService.getEarliestDeadline();

            // 結果の検証
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("restoreTrashItem - ゴミ箱データの削除(メディアの復元)")
    class RestoreTrashItem {
        @Test
        @DisplayName("Trash-08: ゴミ箱データの削除ができること")
        void restoreTrashItem_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 対象の実行
            trashService.restoreTrashItem(requestId);

            // 削除処理が呼ばれていることの確認
            verify(trashRepository).delete(requestId);
        }
    }

    @Nested
    @DisplayName("restoreTrashItems - ゴミ箱データの複数削除(メディアの復元)")
    class RestoreTrashItems {
        @Test
        @DisplayName("Trash-09: ゴミ箱データの複数削除ができること")
        void restoreTrashItems_success() {
            // リクエストデータの作成
            List<Long> requestIds = List.of(1L);

            // 対象の実行
            trashService.restoreTrashItems(requestIds);

            // 削除処理が呼ばれていることの確認
            verify(trashRepository).delete(requestIds);
        }
    }

    @Nested
    @DisplayName("permanentlyDelete - 単一ゴミ箱内メディアデータの完全削除")
    class PermanentlyDelete {
        @Test
        @DisplayName("Trash-10: 単一ゴミ箱内メディアデータの完全削除ができること")
        void permanentlyDelete_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Long mockMediaId = 2L;
            TrashItems mockItem = new TrashItems();
            mockItem.setMediaId(mockMediaId);

            String mockThumbnailS3Key = "thumbnail/image.png";
            String mockS3Key = "media/image.png";
            Media mockMedia = new Media();
            mockMedia.setThumbnailS3Key(mockThumbnailS3Key);
            mockMedia.setS3Key(mockS3Key);

            // 取得処理のスタブ化
            when(trashRepository.findById(requestId)).thenReturn(Optional.of(mockItem));
            when(mediaRepository.findUnscopedById(mockMediaId)).thenReturn(Optional.of(mockMedia));

            // 対象の実行
            trashService.permanentlyDelete(requestId);

            // 各削除処理が順序通りに呼ばれていることの確認
            InOrder inOrder = createInOrder();

            inOrder.verify(mediaCommentRepository).deleteByMediaId(mockMediaId);
            inOrder.verify(favoriteRepository).deleteByMediaId(mockMediaId);
            inOrder.verify(tagRepository).deleteMediaTagsByMediaId(mockMediaId);
            inOrder.verify(firstRecordRepository).deleteMediaByMediaId(mockMediaId);
            inOrder.verify(wordRecordRepository).deleteMediaByMediaId(mockMediaId);

            inOrder.verify(trashRepository).delete(requestId);
            inOrder.verify(mediaRepository).deleteById(mockMediaId);

            inOrder.verify(s3StorageManager).deleteObjects(List.of(mockS3Key, mockThumbnailS3Key));
        }

        @Test
        @DisplayName("Trash-11: 存在しないゴミ箱IDを渡すと例外で処理されること")
        void permanentlyDelete_notFound() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 取得処理のスタブ化
            when(trashRepository.findById(requestId)).thenReturn(Optional.empty());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> trashService.permanentlyDelete(requestId))
                .isInstanceOf(ResourceNotFoundException.class);

            // 各削除処理が呼ばれていないことを確認
            verify(mediaCommentRepository, never()).deleteByMediaId(any());
            verify(favoriteRepository, never()).deleteByMediaId(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaId(any());
            verify(firstRecordRepository, never()).deleteMediaByMediaId(any());
            verify(wordRecordRepository, never()).deleteMediaByMediaId(any());
            verify(trashRepository, never()).delete(any(Long.class));
            verify(mediaRepository, never()).deleteById(any());
            verify(s3StorageManager, never()).deleteObjects(any());
        }

        @Test
        @DisplayName("Trash-12: ゴミ箱は存在するがメディアが存在しないと例外で処理されること")
        void permanentlyDelete_mediaNotFound() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Long mockMediaId = 2L;
            TrashItems mockItem = new TrashItems();
            mockItem.setMediaId(mockMediaId);

            // 取得処理のスタブ化
            when(trashRepository.findById(requestId)).thenReturn(Optional.of(mockItem));
            when(mediaRepository.findUnscopedById(mockMediaId)).thenReturn(Optional.empty());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> trashService.permanentlyDelete(requestId))
                .isInstanceOf(ResourceNotFoundException.class);

            // 各削除処理が呼ばれていないことを確認
            verify(mediaCommentRepository, never()).deleteByMediaId(any());
            verify(favoriteRepository, never()).deleteByMediaId(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaId(any());
            verify(firstRecordRepository, never()).deleteMediaByMediaId(any());
            verify(wordRecordRepository, never()).deleteMediaByMediaId(any());
            verify(trashRepository, never()).delete(any(Long.class));
            verify(mediaRepository, never()).deleteById(any());
            verify(s3StorageManager, never()).deleteObjects(any());
        }

        @Test
        @DisplayName("Trash-13: S3キーとサムネイルキーが空の場合、S3削除処理が呼ばれず正常終了すること")
        void permanentlyDelete_s3DeleteWithoutKey() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            Long mockMediaId = 2L;
            TrashItems mockItem = new TrashItems();
            mockItem.setMediaId(mockMediaId);

            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(trashRepository.findById(requestId)).thenReturn(Optional.of(mockItem));
            when(mediaRepository.findUnscopedById(mockMediaId)).thenReturn(Optional.of(mockMedia));

            // 対象の実行
            trashService.permanentlyDelete(requestId);

            // 各削除処理が順序通りに呼ばれていることの確認
            InOrder inOrder = createInOrder();

            inOrder.verify(mediaCommentRepository).deleteByMediaId(mockMediaId);
            inOrder.verify(favoriteRepository).deleteByMediaId(mockMediaId);
            inOrder.verify(tagRepository).deleteMediaTagsByMediaId(mockMediaId);
            inOrder.verify(firstRecordRepository).deleteMediaByMediaId(mockMediaId);
            inOrder.verify(wordRecordRepository).deleteMediaByMediaId(mockMediaId);

            inOrder.verify(trashRepository).delete(requestId);
            inOrder.verify(mediaRepository).deleteById(mockMediaId);

            // S3削除処理が呼ばれていないことの確認
            verify(s3StorageManager, never()).deleteObjects(any());
        }
    }

    @Nested
    @DisplayName("multiplePermanentlyDelete - 複数ゴミ箱内メディアデータの完全削除")
    class MultiplePermanentlyDelete {
        @Test
        @DisplayName("Trash-14: 複数ゴミ箱内メディアデータの完全削除ができること")
        void multiplePermanentlyDelete_success() {
            // リクエストデータの作成
            List<Long> requestIds = List.of(1L);

            // モックデータの作成
            List<Long> mockMediaIds = List.of(2L);
            TrashItems mockItem = new TrashItems();
            mockItem.setId(requestIds.getFirst());
            mockItem.setMediaId(mockMediaIds.getFirst());

            String mockThumbnailS3Key = "thumbnail/image.png";
            String mockS3Key = "media/image.png";
            Media mockMedia = new Media();
            mockMedia.setThumbnailS3Key(mockThumbnailS3Key);
            mockMedia.setS3Key(mockS3Key);

            // 取得処理のスタブ化
            when(trashRepository.findByIds(requestIds)).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(mockMediaIds)).thenReturn(List.of(mockMedia));

            // 対象の実行
            trashService.multiplePermanentlyDelete(requestIds);

            // 各削除処理が順序通りに呼ばれていることの確認
            InOrder inOrder = createInOrder();

            inOrder.verify(mediaCommentRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(favoriteRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(tagRepository).deleteMediaTagsByMediaIds(mockMediaIds);
            inOrder.verify(firstRecordRepository).deleteMediaByMediaIds(mockMediaIds);
            inOrder.verify(wordRecordRepository).deleteMediaByMediaIds(mockMediaIds);

            inOrder.verify(trashRepository).delete(requestIds);
            inOrder.verify(mediaRepository).deleteByIds(mockMediaIds);

            inOrder.verify(s3StorageManager).deleteObjects(List.of(mockS3Key, mockThumbnailS3Key));
        }

        @Test
        @DisplayName("Trash-15: 該当するゴミ箱データが存在しない")
        void multiplePermanentlyDelete_notFound() {
            // リクエストデータの作成
            List<Long> requestIds = List.of(1L);

            // 取得処理のスタブ化
            when(trashRepository.findByIds(requestIds)).thenReturn(List.of());


            // 例外が吐かれるか確認
            assertThatThrownBy(() -> trashService.multiplePermanentlyDelete(requestIds))
                .isInstanceOf(ResourceNotFoundException.class);


            // 各削除処理が呼ばれていないことを確認
            verify(mediaCommentRepository, never()).deleteByMediaIds(any());
            verify(favoriteRepository, never()).deleteByMediaIds(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(firstRecordRepository, never()).deleteMediaByMediaIds(any());
            verify(wordRecordRepository, never()).deleteMediaByMediaIds(any());

            verify(trashRepository, never()).delete(anyList());
            verify(mediaRepository, never()).deleteByIds(any());

            verify(s3StorageManager, never()).deleteObjects(any());
        }

        @Test
        @DisplayName("Trash-16: S3キーとサムネイルキーが空の場合、S3削除処理が呼ばれず正常終了すること")
        void permanentlyDelete_s3DeleteWithoutKey() {
            // リクエストデータの作成
            List<Long> requestIds = List.of(1L);

            // モックデータの作成
            List<Long> mockMediaIds = List.of(2L);
            TrashItems mockItem = new TrashItems();
            mockItem.setId(requestIds.getFirst());
            mockItem.setMediaId(mockMediaIds.getFirst());

            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(trashRepository.findByIds(requestIds)).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(mockMediaIds)).thenReturn(List.of(mockMedia));

            // 対象の実行
            trashService.multiplePermanentlyDelete(requestIds);


            // 各削除処理が順序通りに呼ばれていることの確認
            InOrder inOrder = createInOrder();

            inOrder.verify(mediaCommentRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(favoriteRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(tagRepository).deleteMediaTagsByMediaIds(mockMediaIds);
            inOrder.verify(firstRecordRepository).deleteMediaByMediaIds(mockMediaIds);
            inOrder.verify(wordRecordRepository).deleteMediaByMediaIds(mockMediaIds);

            inOrder.verify(trashRepository).delete(requestIds);
            inOrder.verify(mediaRepository).deleteByIds(mockMediaIds);

            // S3削除処理が呼ばれていないことの確認
            verify(s3StorageManager, never()).deleteObjects(any());
        }
    }

    @Nested
    @DisplayName("allDelete - 全ゴミ箱内メディアデータの完全削除")
    class AllDelete {
        @Test
        @DisplayName("Trash-17: 全ゴミ箱内メディアデータの完全削除ができること")
        void allDelete_success() {
            // モックデータの作成
            List<Long> mockIds = List.of(1L);
            List<Long> mockMediaIds = List.of(2L);
            TrashItems mockItem = new TrashItems();
            mockItem.setId(mockIds.getFirst());
            mockItem.setMediaId(mockMediaIds.getFirst());

            String mockThumbnailS3Key = "thumbnail/image.png";
            String mockS3Key = "media/image.png";
            Media mockMedia = new Media();
            mockMedia.setThumbnailS3Key(mockThumbnailS3Key);
            mockMedia.setS3Key(mockS3Key);

            // 取得処理のスタブ化
            when(trashRepository.findAll()).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(mockMediaIds)).thenReturn(List.of(mockMedia));

            // 対象の実行
            trashService.allDelete();

            // 各削除処理が順序通りに呼ばれていることの確認
            InOrder inOrder = createInOrder();

            inOrder.verify(mediaCommentRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(favoriteRepository).deleteByMediaIds(mockMediaIds);
            inOrder.verify(tagRepository).deleteMediaTagsByMediaIds(mockMediaIds);
            inOrder.verify(firstRecordRepository).deleteMediaByMediaIds(mockMediaIds);
            inOrder.verify(wordRecordRepository).deleteMediaByMediaIds(mockMediaIds);

            inOrder.verify(trashRepository).delete(mockIds);
            inOrder.verify(mediaRepository).deleteByIds(mockMediaIds);

            inOrder.verify(s3StorageManager).deleteObjects(List.of(mockS3Key, mockThumbnailS3Key));
        }
    }
}
