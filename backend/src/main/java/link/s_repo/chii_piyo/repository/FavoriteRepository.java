package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.Favorites;
import link.s_repo.chii_piyo.repository.gen.FavoritesDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FavoritesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

/**
 * お気に入り関連のリポジトリ<br>
 * お気に入りに関するDB操作を提供
 */
@Repository
@RequiredArgsConstructor
public class FavoriteRepository {
    private final FavoritesMapper favoritesMapper;

    /**
     * メディアIDに一致するお気に入りを取得
     *
     * @param mediaId 対象のメディアID
     * @return お気に入りエンティティリスト
     */
    public List<Favorites> findByMediaId(Long mediaId) {
        return favoritesMapper.select(c -> c.where(
            FavoritesDynamicSqlSupport.mediaId,
            isEqualTo(mediaId)
        ));
    }

    /**
     * メディアIDリストに一致するお気に入りを取得
     *
     * @param mediaIds 対象のメディアIDリスト
     * @return お気に入りエンティティリスト
     */
    public List<Favorites> findByMediaIds(List<Long> mediaIds) {
        return favoritesMapper.select(c -> c.where(
            FavoritesDynamicSqlSupport.mediaId,
            isIn(mediaIds)
        ));
    }

    /**
     * メディアIdとユーザーIdが一致するものの件数を取得する
     *
     * @param mediaId 対象のメディアID
     * @param userId  対象のユーザーID
     * @return 一致したデータの件数
     */
    public Long countByMediaIdAndUserId(Long mediaId, Long userId) {
        return favoritesMapper.count(c -> c.where(
            FavoritesDynamicSqlSupport.mediaId, isEqualTo(mediaId),
            and(FavoritesDynamicSqlSupport.userId, isEqualTo(userId))
        ));
    }

    /**
     * お気に入りをDBに保存する
     *
     * @param favorite お気に入りエンティティ
     */
    public void save(Favorites favorite) {
        favoritesMapper.insertSelective(favorite);
    }

    /**
     * メディアIdとユーザーIdが一致するデータを削除する
     *
     * @param mediaId 対象のメディアID
     * @param userId  対象のユーザーID
     */
    public void deleteByMediaIdAndUserId(Long mediaId, Long userId) {
        favoritesMapper.delete(c -> c.where(FavoritesDynamicSqlSupport.mediaId, isEqualTo(mediaId),
            and(FavoritesDynamicSqlSupport.userId, isEqualTo(userId))));
    }

    /**
     * メディアIdが一致するデータを削除する
     *
     * @param mediaId 対象のメディアID
     */
    public void deleteByMediaId(Long mediaId) {
        favoritesMapper.delete(c -> c.where(FavoritesDynamicSqlSupport.mediaId, isEqualTo(mediaId)));
    }

    /**
     * メディアIdリストが一致するデータを削除する
     *
     * @param mediaIds 対象のメディアIDリスト
     */
    public void deleteByMediaIds(List<Long> mediaIds) {
        favoritesMapper.delete(
            c -> c.where(FavoritesDynamicSqlSupport.mediaId, isIn(mediaIds)));
    }
}
