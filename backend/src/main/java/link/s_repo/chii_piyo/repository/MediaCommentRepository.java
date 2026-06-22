package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.repository.gen.MediaCommentsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaCommentsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Repository
@RequiredArgsConstructor
public class MediaCommentRepository {
    private final MediaCommentsMapper mediaCommentsMapper;

    /**
     * コメントをIDで1件取得する
     *
     * @param id 対象コメントのID
     * @return メディアコメントエンティティ
     */
    public Optional<MediaComments> findById(Long id) {
        return mediaCommentsMapper.selectByPrimaryKey(id);
    }

    /**
     * コメントをDBに保存する
     *
     * @param mediaComments コメントエンティティ
     */
    public void save(MediaComments mediaComments) {
        mediaCommentsMapper.insertSelective(mediaComments);
    }


    /**
     * コメントをメディアIDで複数件取得する
     *
     * @param mediaId 対象メディアのID
     * @return メディアコメントエンティティのリスト
     */
    public List<MediaComments> findByMediaId(Long mediaId) {
        return mediaCommentsMapper.select(
            c -> c.where(MediaCommentsDynamicSqlSupport.mediaId, isEqualTo(mediaId))
        );
    }

    /**
     * コメントをメディアIDリストで複数件取得する
     *
     * @param mediaIds 対象メディアのIDリスト
     * @return メディアコメントエンティティのリスト
     */
    public List<MediaComments> findByMediaIds(List<Long> mediaIds) {
        return mediaCommentsMapper.select(
            c -> c.where(MediaCommentsDynamicSqlSupport.mediaId, isIn(mediaIds))
        );
    }

    /**
     * コメントを削除する
     */
    public void delete(Long id) {
        mediaCommentsMapper.deleteByPrimaryKey(id);
    }

    /**
     * メディアIDに紐づくコメントを削除する
     */
    public void deleteByMediaId(Long mediaId) {
        mediaCommentsMapper.delete(c -> c.where(MediaCommentsDynamicSqlSupport.mediaId, isEqualTo(mediaId)));
    }

    /**
     * メディアIDリストに紐づくコメントを削除する
     */
    public void deleteByMediaIds(List<Long> mediaIds) {
        mediaCommentsMapper.delete(c ->
            c.where(MediaCommentsDynamicSqlSupport.mediaId, isIn(mediaIds)));
    }
}
