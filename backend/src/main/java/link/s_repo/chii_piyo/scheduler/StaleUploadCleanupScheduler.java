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

    // 署名付きURLの有効期限を過ぎた後に、開始済みの転送が終わるまでの猶予待機時間
    @Value("${upload.cleanup.grace-minutes}")
    private long graceMinutes;

    /**
     * 完了しないまま放置されたアップロードを定期的に削除する
     */
    @Scheduled(cron = "${upload.cleanup.cron}", zone = "Asia/Tokyo")
    @Transactional
    public void cleanupStaleUploads() {
        // 今回印を付けるものを同じ実行で消さないため、前回の一段階目で印を付けたものを先に処理する。
        deleteMarkedUploads();
        markStaleUploads();
    }

    /**
     * 一段階目: 放置されたアップロードのS3オブジェクトを削除し、レコードはDELETINGにして残す<br>
     * 署名付きURLの失効前に始まった転送は猶予を過ぎて完了することがあり、経過時間だけでは転送の終了を断定できない<br>
     * そのためレコードを残し、次回の二段階目で再度S3を削除して遅れて作られたオブジェクトを回収する
     */
    private void markStaleUploads() {
        // 削除対象のメディアをロック付きで取得する。トランザクション終了まで完了通知による更新は待機する
        OffsetDateTime threshold = OffsetDateTime.now(ZoneOffset.UTC)
            .minus(S3StorageManager.UPLOAD_URL_EXPIRY)
            .minusMinutes(graceMinutes);
        List<Media> targets = mediaRepository.findStaleUploads(threshold, BATCH_SIZE);

        if (targets.isEmpty()) return;

        // 上限ちょうどの場合は残りが翌日に持ち越されるため、未完了の蓄積に気付けるよう警告を出す
        if (targets.size() >= BATCH_SIZE) {
            log.warn("未完了のアップロードが1回の処理上限 {} 件に達しました。残りは次回に持ち越されます。", BATCH_SIZE);
        }

        // S3を先に消す。失敗した場合はステータスが変わらず次回の実行で再試行される
        // ※存在しないキーの削除はS3側でエラーにならない
        s3StorageManager.deleteObjects(extractS3Keys(targets));
        mediaRepository.markDeleting(targets.stream().map(Media::getId).toList());

        log.info("未完了のアップロードを {} 件削除対象にしました。", targets.size());
    }

    /**
     * 二段階目: 一段階目から1周期以上経過したメディアのS3オブジェクトを再度削除し、レコードを削除する<br>
     * 一段階目の後に遅れて完了した転送のオブジェクトはここで回収される
     */
    private void deleteMarkedUploads() {
        List<Media> targets = mediaRepository.findDeletingUploads(BATCH_SIZE);

        if (targets.isEmpty()) return;

        List<Long> mediaIds = targets.stream().map(Media::getId).toList();

        // S3を先に消す。失敗した場合はレコードが残り次回の実行で再試行されるため、孤立オブジェクトを作らない
        s3StorageManager.deleteObjects(extractS3Keys(targets));

        // メディアおよびアップロード登録時に同時に作られるコメントやタグも合わせて削除
        mediaCommentRepository.deleteByMediaIds(mediaIds);
        favoriteRepository.deleteByMediaIds(mediaIds);
        tagRepository.deleteMediaTagsByMediaIds(mediaIds);
        mediaRepository.deleteByIds(mediaIds);

        log.info("未完了のアップロードを {} 件削除しました。", targets.size());
    }

    /**
     * 本体とサムネイルのS3キーを抽出する
     *
     * @param medias 対象のメディアリスト
     * @return 空を除いたS3キーのリスト
     */
    private List<String> extractS3Keys(List<Media> medias) {
        return medias.stream()
            .flatMap(media -> Stream.of(media.getS3Key(), media.getThumbnailS3Key()))
            .filter(StringUtils::hasText)
            .toList();
    }
}
