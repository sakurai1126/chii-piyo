package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * コメント管理サービス<br>
 * コメントの処理のロジックを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaCommentService {
    private final MediaCommentRepository mediaCommentRepository;

    /**
     * コメントをIDで1件取得する<br>
     *
     * @param commentId 対象コメントのID
     * @return メディアコメントエンティティ
     */
    @Transactional(readOnly = true)
    public MediaComments getMediaComment(Long commentId) {
        return mediaCommentRepository.findById(commentId).orElseThrow(() ->
            new ResourceNotFoundException("コメントが見つかりません commentId=" + commentId));
    }

    /**
     * コメントを新規作成する<br>
     *
     * @param mediaId 追加するコメントに紐づけるメディアのID
     * @param userId  コメントをするユーザーのID
     * @param content コメント本文
     */
    @Transactional
    public void createMediaComment(Long mediaId, Long userId, String content) {
        MediaComments mediaComments = new MediaComments();

        // コメントエンティティに値をセット
        mediaComments.setMediaId(mediaId);
        mediaComments.setUserId(userId);
        mediaComments.setContent(content);

        // コメントをDBに保存
        mediaCommentRepository.save(mediaComments);
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
        List<MediaComments> mediaComments = mediaCommentRepository.findByMediaIds(mediaIds);

        // 取得したコメントリストをmediaIdごとにグルーピングし、コメント数をカウントしてマップに変換して返す
        return mediaComments.stream()
            .collect(Collectors.groupingBy(
                MediaComments::getMediaId,
                Collectors.counting()
            ));
    }

    /**
     * メディアに紐づくコメントを一覧取得する
     *
     * @param mediaId 対象メディアのID
     * @return コメントのリスト
     */
    @Transactional(readOnly = true)
    public List<MediaComments> getMediaComments(Long mediaId) {
        return mediaCommentRepository.findByMediaId(mediaId);
    }

    /**
     * コメントを削除する
     *
     * @param comment       対象のコメント
     * @param currentUserId リクエストをしたユーザーID
     */
    @Transactional
    public void deleteMediaComment(MediaComments comment, Long currentUserId) {
        // 現在のユーザーのリクエストかを判別
        if (!comment.getUserId().equals(currentUserId)) {
            throw new ResourceAccessDeniedException("他のユーザーのコメントは削除できません");
        }

        // 削除処理
        mediaCommentRepository.delete(comment.getId());
    }
}
