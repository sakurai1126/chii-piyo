package link.s_repo.chii_piyo.service;


import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.gen.MediaCommentsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaCommentsMapper;
import link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
/**
 * コメント管理サービス<br>
 * コメントの取得・作成およびメディアとのコメント紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaCommentService {
    private final MediaCommentsMapper mediaCommentsMapper;

    /**
     * コメントを新規作成する<br>
     *
     * @param mediaId 追加するコメントに紐づけるメディアのID
     * @param userId  コメントをするユーザーのID
     * @param content コメント本文
     * @return 作成されたコメントエンティティ
     */
    @Transactional
    public MediaComments createMediaComment(Long mediaId, Long userId, String content) {
        MediaComments mediaComments = new MediaComments();

        // コメントエンティティに値をセット
        mediaComments.setMediaId(mediaId);
        mediaComments.setUserId(userId);
        mediaComments.setContent(content);
        mediaComments.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        mediaComments.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // コメントをDBに保存
        mediaCommentsMapper.insert(mediaComments);
        return mediaComments;
    }

    /**
     * 複数メディアに紐づくコメント数を一括取得する<br>
     *
     * @param mediaIds メディアIDのリスト
     * @return mediaIdをキー、そのメディアのコメント数を値とするマップ
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> getCommentCountsByMediaIds(List<Long> mediaIds) {
        // メディアIDリストが空の場合は空のマップを返す
        if (mediaIds.isEmpty()) {
            return Map.of();
        }

        // 指定されたメディアIDに紐づくコメントを一括で取得
        List<MediaComments> mediaComments = mediaCommentsMapper.select(
            c -> c.where(MediaCommentsDynamicSqlSupport.mediaId, isIn(mediaIds))
        );

        // 取得したコメントリストをmediaIdごとにグルーピングし、コメント数をカウントしてマップに変換して返す
        return mediaComments.stream()
            .collect(Collectors.groupingBy(
                MediaComments::getMediaId,
                Collectors.counting()
            ));
    }
}
