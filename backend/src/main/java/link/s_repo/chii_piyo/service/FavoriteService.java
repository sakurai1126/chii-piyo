package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Favorites;
import link.s_repo.chii_piyo.repository.gen.FavoritesDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FavoritesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoritesMapper favoritesMapper;

    /**
     * お気に入りに追加したユーザーのIDリストを取得する
     *
     * @param mediaId 対象メディアのID
     * @return お気に入りに追加したユーザーのIDリスト
     */
    @Transactional(readOnly = true)
    public List<Long> getAddFavoriteUserIds(Long mediaId) {
        List<Favorites> favorites =
            favoritesMapper.select(c -> c.where(
                FavoritesDynamicSqlSupport.mediaId,
                isEqualTo(mediaId)
            ));

        return favorites.stream().map(Favorites::getUserId).toList();
    }

    /**
     * ログイン中のユーザーがお気に入りに入れているかどうかを取得する
     *
     * @param mediaId       対象メディアのID
     * @param currentUserId 現在ログイン中のユーザーID
     * @return 現在のユーザーがお気に入りに入れているかどうかの真偽値
     */
    @Transactional(readOnly = true)
    public boolean getCurrentUserIsFavorite(Long mediaId, Long currentUserId) {
        // mediaIdとuserIdが一致するものの件数を取得する
        long count = favoritesMapper.count(c -> c.where(
            FavoritesDynamicSqlSupport.mediaId, isEqualTo(mediaId),
            and(FavoritesDynamicSqlSupport.userId, isEqualTo(currentUserId))
        ));

        // 1件以上かどうかの真偽値を返す
        return count > 0;
    }

    /**
     * お気に入りデータを登録する
     *
     * @param mediaId       対象のメディアID
     * @param currentUserId リクエストを送ったユーザーのID
     */
    @Transactional
    public void addFavorite(Long mediaId, Long currentUserId) {
        // 重複登録を避けるため存在チェックし、存在する場合は即時リターンする
        boolean alreadyFavorited = getCurrentUserIsFavorite(mediaId, currentUserId);
        if (alreadyFavorited) return;

        // 新規オブジェクトを生成し受け取ったデータをセットしてDBに登録する
        Favorites favorite = new Favorites();

        favorite.setMediaId(mediaId);
        favorite.setUserId(currentUserId);

        favoritesMapper.insertSelective(favorite);
    }

    /**
     * お気に入りデータを削除する
     *
     * @param mediaId       対象のメディアID
     * @param currentUserId リクエストを送ったユーザーのID
     */
    @Transactional
    public void removeFavorite(Long mediaId, Long currentUserId) {
        // 受け取ったパラメータに合致するデータを削除する
        favoritesMapper.delete(c -> c.where(FavoritesDynamicSqlSupport.mediaId, isEqualTo(mediaId),
            and(FavoritesDynamicSqlSupport.userId, isEqualTo(currentUserId))));
    }
}
