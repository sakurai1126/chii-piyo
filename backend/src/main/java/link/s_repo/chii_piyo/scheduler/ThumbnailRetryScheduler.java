package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.ThumbnailGenerator;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * サムネイル生成失敗を救済する定期実行サービス<br>
 * upload_status が COMPLETED でありながら thumbnail_s3_key が null のメディアを定期的に拾い直し、
 * サムネイル生成を再実行する
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailRetryScheduler {

    private final MediaRepository mediaRepository;
    private final ThumbnailGenerator thumbnailGenerator;

    /**
     * 未生成のサムネイルを定期的に再試行する<br>
     * 直近の失敗だけでなく、過去に失敗したものも順次拾い直される
     */
    @Scheduled(fixedDelayString = "${thumbnail.retry-interval-ms}")
    public void retryMissingThumbnails() {

        // アップロードが完了していてサムネイルのキーがデータ登録されていないもの最大20件取得
        List<Media> targets = mediaRepository.findMissingThumbnails(20L);

        // 対象がなければ何もしない
        if (targets.isEmpty()) return;

        // 対象があればサムネイル生成を再実行する
        log.info("サムネ未生成メディアを再処理 count={}", targets.size());
        for (Media media : targets) {
            thumbnailGenerator.generateThumbnailAsync(
                media.getId(),
                media.getMediaType(),
                media.getS3Key(),
                media.getOriginalFilename(),
                media.getFileSize()
            );
        }
    }
}
