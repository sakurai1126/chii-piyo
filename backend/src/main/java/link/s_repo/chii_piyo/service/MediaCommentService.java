package link.s_repo.chii_piyo.service;


import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.repository.gen.MediaCommentsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
}
