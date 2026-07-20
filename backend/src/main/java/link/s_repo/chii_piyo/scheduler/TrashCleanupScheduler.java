package link.s_repo.chii_piyo.scheduler;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import link.s_repo.chii_piyo.repository.TrashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

/**
 * ゴミ箱の定期クリーンアップ処理用コンポーネント
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TrashCleanupScheduler {
    private final TrashRepository trashRepository;
    private final MediaRepository mediaRepository;
    private final S3StorageManager s3StorageManager;
    private final MediaCommentRepository mediaCommentRepository;
    private final FavoriteRepository favoriteRepository;
    private final TagRepository tagRepository;

    /**
     * ゴミ箱の定期クリーンアップ処理<br>
     * 毎日午前4時に実行し、期限切れのゴミ箱データを完全削除する
     */
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Tokyo")
    public void cleanupExpiredTrashItems() {
        log.info("ゴミ箱データの自動削除バッチを開始します。");

        // 削除予定日時（expiresAt）が現在時刻以前のアイテムを取得
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Tokyo"));
        List<TrashItems> expiredItems = trashRepository.findExpiredItems(now);

        // 削除対象がない場合即時リターン
        if (expiredItems.isEmpty()) {
            log.info("削除対象のゴミ箱データはありませんでした。");
            return;
        }

        // 削除処理
        try {
            List<Long> ids = expiredItems.stream().map(TrashItems::getId).toList();
            List<Long> mediaIds = expiredItems.stream().map(TrashItems::getMediaId).toList();

            // Mediaテーブルから削除対象のレコードを取得
            List<Media> mediaList = mediaRepository.findUnscopedByIds(mediaIds);

            // S3キーとサムネイルS3キーを取り出しリスト化
            List<String> s3Keys = mediaList.stream()
                .flatMap(media -> Stream.of(media.getS3Key(), media.getThumbnailS3Key()))
                .filter(StringUtils::hasText)
                .toList();

            // 各種データを削除
            mediaCommentRepository.deleteByMediaIds(mediaIds);
            favoriteRepository.deleteByMediaIds(mediaIds);
            tagRepository.deleteMediaTagsByMediaIds(mediaIds);
            trashRepository.delete(ids);
            mediaRepository.deleteByIds(mediaIds);

            // S3上のデータの一括削除
            if (!s3Keys.isEmpty()) {
                s3StorageManager.deleteObjects(s3Keys);
            }
            log.info("{} 件のゴミ箱データを完全に削除しました。", expiredItems.size());
        } catch (Exception e) {
            log.error("ゴミ箱データの自動削除中にエラーが発生しました。", e);
        }
    }
}
