package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.gen.MediaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * サムネイル生成失敗を救済する定期実行サービス<br>
 * upload_status が COMPLETED でありながら thumbnail_s3_key が null のメディアを定期的に拾い直し、
 * サムネイル生成を再実行する
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailRetryService {

    private final MediaMapper mediaMapper;
    private final ThumbnailService thumbnailService;

    /**
     * 未生成のサムネイルを定期的に再試行する<br>
     * 直近の失敗だけでなく、過去に失敗したものも順次拾い直される
     */
    @Scheduled(fixedDelayString = "${thumbnail.retry-interval-ms}")
    public void retryMissingThumbnails() {
        // アップロードが完了していてサムネイルのキーがデータ登録されていないものを最大20件取得
        List<Media> targets = mediaMapper.select(c -> c
            .where(uploadStatus, isEqualTo("COMPLETED"))
            .and(thumbnailS3Key, isNull())
            .limit(20)
        );

        // 対象がなければ何もしない
        if (targets.isEmpty()) return;

        // 対象があればサムネイル生成を再実行する
        log.info("サムネ未生成メディアを再処理 count={}", targets.size());
        for (Media media : targets) {
            thumbnailService.generateThumbnailAsync(
                media.getId(),
                media.getMediaType(),
                media.getS3Key(),
                media.getOriginalFilename(),
                media.getFileSize()
            );
        }
    }
}
