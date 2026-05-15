package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.gen.MediaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * Media テーブルへの更新処理を提供するリポジトリ<br>
 * 非同期メソッドと同一クラス内ではトランザクションがプロキシを経由せず効かないため、<br>
 * 別Beanとして切り出しThumbnailServiceから注入して使用する用途
 */
@Repository
@RequiredArgsConstructor
public class MediaUpdateRepository {
    private final MediaMapper mediaMapper;

    /**
     * thumbnail_s3_key を更新する
     */
    @Transactional
    public void updateThumbnailKey(Long mediaId, String thumbnailS3Key) {
        Media media = mediaMapper.selectOne(c -> c.where(id, isEqualTo(mediaId)))
            .orElseThrow(() -> new IllegalStateException("メディアが見つかりません mediaId=" + mediaId));
        media.setThumbnailS3Key(thumbnailS3Key);
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        mediaMapper.updateByPrimaryKeySelective(media);
    }
}
