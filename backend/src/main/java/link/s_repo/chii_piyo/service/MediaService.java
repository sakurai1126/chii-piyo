package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.MediaAccessDeniedException;
import link.s_repo.chii_piyo.exception.MediaNotFoundException;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaMapper;
import link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * メディア管理サービス<br>
 * 写真・動画のメタデータ登録およびS3との連携を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaMapper mediaMapper;
    private final S3Service s3Service;
    private final ThumbnailService thumbnailService;

    // S3のプレフィックスをまとめて管理
    private static final String MEDIA_PREFIX = "media";
    // S3キー生成時に使用する日付フォーマット
    private static final DateTimeFormatter S3_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * メディア総件数を取得する
     *
     * @return 総件数の数値
     */
    @Transactional(readOnly = true)
    public Long countMedia(
        String mediaType,
        Long albumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate) {
        return mediaMapper.count(c -> {
            // 絞り込み条件設定を構築
            List<AndOrCriteriaGroup> conditions = buildMediaFilterConditions(mediaType, albumId, tagId, sharingGroupId, startDate, endDate);

            if (!conditions.isEmpty()) {
                c.where(conditions);
            }

            return c;
        });
    }

    /**
     * メディアの一覧を取得する
     *
     * @param offset 取得位置
     * @param limit  最大件数
     * @return メディアのリスト
     */
    public List<Media> getMediaList(
        Integer offset,
        Integer limit,
        String mediaType,
        Long albumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate) {
        // ページネーション込みで一覧取得
        return mediaMapper.select(c -> {
                // 絞り込み条件設定を構築
                List<AndOrCriteriaGroup> conditions = buildMediaFilterConditions(mediaType, albumId, tagId, sharingGroupId, startDate, endDate);

                if (!conditions.isEmpty()) {
                    c.where(conditions);
                }

                c.orderBy(MediaDynamicSqlSupport.createdAt.descending());
                return c.limit(limit).offset(offset);
            }
        );
    }

    private List<AndOrCriteriaGroup> buildMediaFilterConditions(
        String mediaType,
        Long albumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate) {

        // 絞り込み条件構築用のリストを用意し各条件が存在する時は追加していく
        List<AndOrCriteriaGroup> conditions = new ArrayList<>();
        if (albumId != null) {
            conditions.add(and(MediaDynamicSqlSupport.albumId, isEqualTo(albumId)));
        }

        if ("PHOTO".equals(mediaType) || "VIDEO".equals(mediaType)) {
            conditions.add(and(MediaDynamicSqlSupport.mediaType, isEqualTo(mediaType)));
        }

        if (sharingGroupId != null) {
            conditions.add(and(MediaDynamicSqlSupport.sharingGroupId, isEqualTo(sharingGroupId)));
        }

        if (tagId != null && !tagId.isEmpty()) {
            // タグIDで絞り込むため、MediaTagsテーブルとサブクエリで結合して条件を追加
            // タグIDの一致するMediaTagsレコードをサブクエリで取得しMediaIdを抽出
            // 上記のMediaIdとMediaテーブルのidで絞り込む
            conditions.add(and(MediaDynamicSqlSupport.id,
                isIn(select(MediaTagsDynamicSqlSupport.mediaId)
                    .from(MediaTagsDynamicSqlSupport.mediaTags)
                    .where(MediaTagsDynamicSqlSupport.tagId, isIn(tagId))
                ))
            );
        }

        if (startDate != null) {
            // createdAt >= startDate で検索
            // atStartOfDayでLocalDateTimeに変換、toOffsetDateTimeでOffsetDateTimeに変換して検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isGreaterThanOrEqualTo(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        if (endDate != null) {
            // createdAt < endDate で検索
            // plusDays(1)で翌日にして対象日末までを検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isLessThan(endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        return conditions;
    }


    /**
     * メディアレコードを作成し、署名付きアップロードURLを返却する<br>
     * upload_status は PROCESSING で初期化される
     *
     * @param userId           アップロードを実行するユーザーID
     * @param mediaType        メディア種別 (PHOTO / VIDEO)
     * @param originalFilename 元のファイル名
     * @param contentType      MIMEタイプ
     * @param fileSize         ファイルサイズ (バイト)
     * @param width            幅
     * @param height           高さ
     * @param takenAt          撮影日
     * @param albumId          所属アルバムID
     * @param sharingGroupId   共有範囲グループID
     * @return 作成したメディアレコードと署名付きURL
     */
    @Transactional
    public CreateMediaResult createMedia(
        Long userId,
        String mediaType,
        String originalFilename,
        String contentType,
        Long fileSize,
        Integer width,
        Integer height,
        LocalDate takenAt,
        Long albumId,
        Long sharingGroupId
    ) {
        // S3キーを生成
        String s3Key = buildS3Key(originalFilename);

        // Mediaエンティティを構築
        Media media = new Media();
        media.setUploadedBy(userId);
        media.setMediaType(mediaType);
        media.setOriginalFilename(originalFilename);
        media.setContentType(contentType);
        media.setFileSize(fileSize);
        media.setWidth(width);
        media.setHeight(height);
        media.setS3Key(s3Key);
        media.setTakenAt(takenAt);
        media.setAlbumId(albumId);
        media.setSharingGroupId(sharingGroupId);
        // アップロード進行中の状態でステータスを登録
        media.setUploadStatus("PROCESSING");
        media.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // DBに保存 (insertSelectiveでnullカラムをスキップ)
        mediaMapper.insertSelective(media);

        // 署名付きアップロードURLを発行
        String presignedUrl = s3Service.generateUploadPresignedUrl(s3Key, contentType);

        return new CreateMediaResult(media, presignedUrl);
    }

    /**
     * メディアのアップロードステータスを更新する<br>
     * 自分自身がアップロードしたメディアのみ更新可能
     *
     * @param mediaId      対象のメディアID
     * @param userId       実行ユーザーID
     * @param uploadStatus 更新後のステータス (COMPLETED / FAILED / PROCESSING)
     * @return 更新後のメディア情報
     * @throws MediaNotFoundException     対象メディアが存在しない場合
     * @throws MediaAccessDeniedException アップロード者以外が更新しようとした場合
     */
    @Transactional
    public Media updateUploadStatus(Long mediaId, Long userId, String uploadStatus) {
        // 対象メディアを取得
        Media media = mediaMapper.selectOne(c -> c.where(id, isEqualTo(mediaId)))
            .orElseThrow(() -> new MediaNotFoundException("メディアが見つかりません mediaId=" + mediaId));

        // アップロード者本人かを確認
        if (!media.getUploadedBy().equals(userId)) {
            throw new MediaAccessDeniedException("このメディアを更新する権限がありません mediaId=" + mediaId);
        }

        // ステータスのみを更新
        media.setUploadStatus(uploadStatus);
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // updateByPrimaryKeySelectiveでnullカラムをスキップして更新
        mediaMapper.updateByPrimaryKeySelective(media);

        // COMPLETEDに変わったところからサムネイル生成を非同期で起動
        if ("COMPLETED".equals(uploadStatus)) {
            thumbnailService.generateThumbnailAsync(
                mediaId,
                media.getMediaType(),
                media.getS3Key(),
                media.getOriginalFilename(),
                media.getFileSize()
            );
        }

        return media;
    }

    /**
     * S3キーを構築する<br>
     * 形式: media/yyyy/MM/dd/{UUID}_{元のファイル名}
     *
     * @param originalFilename 元のファイル名
     * @return S3キー
     */
    private String buildS3Key(String originalFilename) {
        String today = LocalDate.now().format(S3_DATE_FORMAT);
        // 同一名でファイルが衝突しないようUUIDを付与
        String uniqueId = UUID.randomUUID().toString();
        // 元のファイル名は安全のためサニタイズ
        String safeName = sanitizeFilename(originalFilename);
        return String.format("%s/%s/%s_%s", MEDIA_PREFIX, today, uniqueId, safeName);
    }

    /**
     * ファイル名をS3キー用にサニタイズする<br>
     * パス区切り文字や制御文字を除去する
     *
     * @param filename ファイル名
     * @return サニタイズ後のファイル名
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unknown";
        }
        // パス区切り文字をアンダースコアに置換し、安全な文字のみを許可
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * createMediaの戻り値<br>
     * Mediaエンティティと署名付きURLをまとめて返すための内部クラス
     */
    public record CreateMediaResult(Media media, String presignedUrl) {
    }
}
