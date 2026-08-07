package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import link.s_repo.chii_piyo.repository.TrashRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TrashCleanupSchedulerTest {
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

    @InjectMocks
    private TrashCleanupScheduler trashCleanupScheduler;

    @Nested
    @DisplayName("cleanupExpiredTrashItems - ゴミ箱の定期クリーンアップ処理")
    @ExtendWith(OutputCaptureExtension.class)
    class CleanupExpiredTrashItems {
        // 共通モックデータの作成
        Long mockId = 1L;
        Long mockMediaId = 2L;

        @Test
        @DisplayName("Sched-01: 期限切れアイテムが存在する場合、ゴミ箱の定期クリーンアップ処理ができること")
        void cleanupExpiredTrashItems_success() {
            // モックデータの作成
            TrashItems mockItem = new TrashItems();
            mockItem.setId(mockId);
            mockItem.setMediaId(mockMediaId);

            String mockS3Key = "media/image.png";
            String mockThumbnailS3Key = "thumbnail/image.png";
            Media mockMedia = new Media();
            mockMedia.setS3Key(mockS3Key);
            mockMedia.setThumbnailS3Key(mockThumbnailS3Key);

            // 取得処理のスタブ化
            when(trashRepository.findExpiredItems(any(OffsetDateTime.class))).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(List.of(mockMediaId))).thenReturn(List.of(mockMedia));

            // 対象の実行
            trashCleanupScheduler.cleanupExpiredTrashItems();

            // 各削除処理が呼ばれていることの確認
            verify(mediaCommentRepository).deleteByMediaIds(List.of(mockMediaId));
            verify(favoriteRepository).deleteByMediaIds(List.of(mockMediaId));
            verify(tagRepository).deleteMediaTagsByMediaIds(List.of(mockMediaId));
            verify(trashRepository).delete(List.of(mockId));
            verify(mediaRepository).deleteByIds(List.of(mockMediaId));
            verify(s3StorageManager).deleteObjects(List.of(mockS3Key, mockThumbnailS3Key));
        }

        @Test
        @DisplayName("Sched-02: 期限切れアイテムが存在しない場合、削除処理が呼ばれないこと")
        void cleanupExpiredTrashItems_empty() {
            // 取得処理のスタブ化
            when(trashRepository.findExpiredItems(any(OffsetDateTime.class))).thenReturn(List.of());

            // 対象の実行
            trashCleanupScheduler.cleanupExpiredTrashItems();

            // 各削除処理が呼ばれていないことの確認
            verify(mediaRepository, never()).findUnscopedByIds(any());
            verify(mediaCommentRepository, never()).deleteByMediaIds(any());
            verify(favoriteRepository, never()).deleteByMediaIds(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(trashRepository, never()).delete(anyList());
            verify(mediaRepository, never()).deleteByIds(any());
            verify(s3StorageManager, never()).deleteObjects(any());
        }

        @Test
        @DisplayName("Sched-03: S3キーとサムネイルキーが空の場合、S3削除処理が呼ばれずに正常終了すること")
        void cleanupExpiredTrashItems_s3DeleteWithoutKey() {
            // モックデータの作成
            TrashItems mockItem = new TrashItems();
            mockItem.setId(mockId);
            mockItem.setMediaId(mockMediaId);

            Media mockMedia = new Media();

            // 取得処理のスタブ化
            when(trashRepository.findExpiredItems(any(OffsetDateTime.class))).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(List.of(mockMediaId))).thenReturn(List.of(mockMedia));

            // 対象の実行
            trashCleanupScheduler.cleanupExpiredTrashItems();

            // 各削除処理が呼ばれていることの確認
            verify(mediaCommentRepository).deleteByMediaIds(List.of(mockMediaId));
            verify(favoriteRepository).deleteByMediaIds(List.of(mockMediaId));
            verify(tagRepository).deleteMediaTagsByMediaIds(List.of(mockMediaId));
            verify(trashRepository).delete(List.of(mockId));
            verify(mediaRepository).deleteByIds(List.of(mockMediaId));

            // S3削除処理が呼ばれていないことの確認
            verify(s3StorageManager, never()).deleteObjects(any());
        }

        @Test
        @DisplayName("Sched-04: 削除処理中に例外が発生してもエラーログを出力して正常終了すること")
        void cleanupExpiredTrashItems_catchError(CapturedOutput output) {
            // モックデータの作成
            TrashItems mockItem = new TrashItems();
            mockItem.setId(mockId);
            mockItem.setMediaId(mockMediaId);

            // 取得処理のスタブ化
            when(trashRepository.findExpiredItems(any(OffsetDateTime.class))).thenReturn(List.of(mockItem));
            when(mediaRepository.findUnscopedByIds(List.of(mockMediaId))).thenThrow(new RuntimeException("DBアクセスエラー"));

            // 対象を実行し、例外が吐かれず終了することを検証
            assertDoesNotThrow(() -> trashCleanupScheduler.cleanupExpiredTrashItems());

            // エラーログが正しく出力されていることの検証
            assertThat(output.getOut()).contains("ゴミ箱データの自動削除中にエラーが発生しました。");
            assertThat(output.getOut()).contains("DBアクセスエラー");
        }
    }
}
