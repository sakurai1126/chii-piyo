package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

/**
 * 未完了アップロードの定期クリーンアップ処理用コンポーネント<br>
 * ブラウザ終了や通信断で本体がアップロードされなかったメディアのレコードと、残っている可能性のあるS3オブジェクトを削除する
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaleUploadCleanupScheduler {
    // 1回の実行で処理する最大件数
    private static final long BATCH_SIZE = 100L;

    private final MediaRepository mediaRepository;
    private final S3StorageManager s3StorageManager;
    private final MediaCommentRepository mediaCommentRepository;
    private final FavoriteRepository favoriteRepository;
    private final TagRepository tagRepository;

    // 署名付きURLの有効期限を過ぎるまでの猶予時間
    @Value("${upload.cleanup.grace-minutes}")
    private long graceMinutes;

    /**
     * 完了しないまま放置されたアップロードを定期的に削除する
     */
    @Scheduled(fixedDelayString = "${upload.cleanup.interval-ms}")
    @Transactional
    public void cleanupStaleUploads() {
        // 削除対象のメディアを取得
        OffsetDateTime threshold = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(graceMinutes);
        List<Media> targets = mediaRepository.findStaleUploads(threshold, BATCH_SIZE);

        if (targets.isEmpty()) return;

        // 削除対象のメディアIDとS3キーを抽出
        List<Long> mediaIds = targets.stream().map(Media::getId).toList();
        List<String> s3Keys = targets.stream()
            .flatMap(media -> Stream.of(media.getS3Key(), media.getThumbnailS3Key()))
            .filter(StringUtils::hasText)
            .toList();

        // S3を先に消す。失敗した場合はレコードが残り次回の実行で再試行されるため、孤立オブジェクトを作らない
        // 存在しないキーの削除はS3側でエラーにならない
        s3StorageManager.deleteObjects(s3Keys);

        // メディアおよびアップロード登録時に同時に作られるコメントやタグも合わせて削除
        mediaCommentRepository.deleteByMediaIds(mediaIds);
        favoriteRepository.deleteByMediaIds(mediaIds);
        tagRepository.deleteMediaTagsByMediaIds(mediaIds);
        mediaRepository.deleteByIds(mediaIds);

        log.info("未完了のアップロードを {} 件削除しました。", targets.size());
    }
}
