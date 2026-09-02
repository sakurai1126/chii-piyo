package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.ThumbnailGenerator;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.MediaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThumbnailRetrySchedulerTest {
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private ThumbnailGenerator thumbnailGenerator;

    @InjectMocks
    private ThumbnailRetryScheduler thumbnailRetryScheduler;

    @Nested
    @DisplayName("retryMissingThumbnails - サムネイル再生成処理")
    class RetryMissingThumbnails {
        @Test
        @DisplayName("Sched-05: サムネイル再生成処理ができること")
        void retryMissingThumbnails_success() {
            // モックデータの作成
            Media mockMedia = new Media();
            mockMedia.setId(1L);
            mockMedia.setMediaType("image/png");
            mockMedia.setS3Key("media/image.png");
            mockMedia.setOriginalFilename("image.png");
            mockMedia.setFileSize(100L);

            // 取得処理のスタブ化
            when(mediaRepository.findMissingThumbnails(20L))
                .thenReturn(List.of(mockMedia));

            // 対象の実行
            thumbnailRetryScheduler.retryMissingThumbnails();

            // 保存処理が呼び出されているか確認
            verify(thumbnailGenerator).generateThumbnailAsync(
                mockMedia.getId(),
                mockMedia.getMediaType(),
                mockMedia.getS3Key(),
                mockMedia.getOriginalFilename(),
                mockMedia.getFileSize()
            );
        }

        @Test
        @DisplayName("Sched-06: サムネイル未生成のメディアが存在しない場合サムネイル再生成処理が実行されないこと")
        void retryMissingThumbnails_noItem() {
            // 取得処理のスタブ化
            when(mediaRepository.findMissingThumbnails(20L))
                .thenReturn(List.of());

            // 対象の実行
            thumbnailRetryScheduler.retryMissingThumbnails();

            // 保存処理が呼び出されていないことを確認
            verify(thumbnailGenerator, never()).generateThumbnailAsync(
                any(), any(), any(), any(), any());
        }
    }
}
