package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class StaleUploadCleanupSchedulerTest {
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
    private StaleUploadCleanupScheduler staleUploadCleanupScheduler;

    @BeforeEach
    void setUp() {
        // @Valueで注入される猶予時間はテストでは注入されないため直接設定する
        ReflectionTestUtils.setField(staleUploadCleanupScheduler, "graceMinutes", 60L);
    }

    @Nested
    @DisplayName("cleanupStaleUploads - 未完了アップロードの定期クリーンアップ処理")
    class CleanupStaleUploads {
        // 共通モックデータの作成
        Long mockMediaId = 1L;

        @Test
        @DisplayName("Sched-07: 未完了のメディアが存在する場合、S3オブジェクトを削除してステータスを変更にし、レコードは削除しないこと")
        void cleanupStaleUploads_success() {
            // モックデータの作成
            String mockS3Key = "media/image.png";
            String mockThumbnailS3Key = "thumbnail/image.png";
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);
            mockMedia.setS3Key(mockS3Key);
            mockMedia.setThumbnailS3Key(mockThumbnailS3Key);

            // 取得処理のスタブ化
            when(mediaRepository.findStaleUploads(any(OffsetDateTime.class), anyLong()))
                .thenReturn(List.of(mockMedia));

            // 対象の実行
            staleUploadCleanupScheduler.cleanupStaleUploads();

            // S3削除がステータス更新より先に呼ばれていることの確認
            InOrder inOrder = inOrder(s3StorageManager, mediaRepository);
            inOrder.verify(s3StorageManager).deleteObjects(List.of(mockS3Key, mockThumbnailS3Key));
            inOrder.verify(mediaRepository).markDeleting(List.of(mockMediaId));

            // 遅れて完了する転送を回収できるよう、同じ実行ではレコードを削除しないことの確認
            verify(mediaRepository, never()).deleteByIds(any());
            verify(mediaCommentRepository, never()).deleteByMediaIds(any());
            verify(favoriteRepository, never()).deleteByMediaIds(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
        }

        @Test
        @DisplayName("Sched-08: 未完了のメディアが存在しない場合、削除処理が呼ばれないこと")
        void cleanupStaleUploads_empty() {
            // 取得処理のスタブ化
            when(mediaRepository.findStaleUploads(any(OffsetDateTime.class), anyLong())).thenReturn(List.of());

            // 対象の実行
            staleUploadCleanupScheduler.cleanupStaleUploads();

            // 各削除処理が呼ばれていないことの確認
            verify(s3StorageManager, never()).deleteObjects(any());
            verify(mediaCommentRepository, never()).deleteByMediaIds(any());
            verify(favoriteRepository, never()).deleteByMediaIds(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(mediaRepository, never()).deleteByIds(any());
        }

        @Test
        @DisplayName("Sched-09: S3削除に失敗した場合、DBを削除せず例外が伝播すること")
        void cleanupStaleUploads_s3DeleteError() {
            // モックデータの作成
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);
            mockMedia.setS3Key("media/image.png");

            // 取得処理のスタブ化
            when(mediaRepository.findStaleUploads(any(OffsetDateTime.class), anyLong()))
                .thenReturn(List.of(mockMedia));
            doThrow(new RuntimeException("S3削除エラー")).when(s3StorageManager).deleteObjects(any());

            // 対象を実行し、例外が伝播することを検証
            assertThatThrownBy(() -> staleUploadCleanupScheduler.cleanupStaleUploads())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("S3削除エラー");

            // ステータスが変わらず次回再試行されるよう、更新と削除が呼ばれていないことの確認
            verify(mediaRepository, never()).markDeleting(any());
            verify(mediaCommentRepository, never()).deleteByMediaIds(any());
            verify(favoriteRepository, never()).deleteByMediaIds(any());
            verify(tagRepository, never()).deleteMediaTagsByMediaIds(any());
            verify(mediaRepository, never()).deleteByIds(any());
        }

        @Test
        @DisplayName("Sched-10: DELETINGのメディアが存在する場合、S3オブジェクトを再削除してから関連データとメディアを削除すること")
        void cleanupStaleUploads_deleteMarked() {
            // モックデータの作成
            String mockS3Key = "media/image.png";
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);
            mockMedia.setS3Key(mockS3Key);

            // 取得処理のスタブ化
            when(mediaRepository.findDeletingUploads(anyLong())).thenReturn(List.of(mockMedia));

            // 対象の実行
            staleUploadCleanupScheduler.cleanupStaleUploads();

            // S3削除がDB削除より先に呼ばれていることの確認
            InOrder inOrder = inOrder(s3StorageManager, mediaCommentRepository, favoriteRepository,
                tagRepository, mediaRepository);
            inOrder.verify(s3StorageManager).deleteObjects(List.of(mockS3Key));
            inOrder.verify(mediaCommentRepository).deleteByMediaIds(List.of(mockMediaId));
            inOrder.verify(favoriteRepository).deleteByMediaIds(List.of(mockMediaId));
            inOrder.verify(tagRepository).deleteMediaTagsByMediaIds(List.of(mockMediaId));
            inOrder.verify(mediaRepository).deleteByIds(List.of(mockMediaId));
        }
    }
}
