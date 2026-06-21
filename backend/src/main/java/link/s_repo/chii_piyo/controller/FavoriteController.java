package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.gen.FavoriteManagementApi;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.FavoriteService;
import link.s_repo.chii_piyo.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


/**
 * お気に入り管理コントローラー<br>
 * OpenAPI Generator生成のMediaApiインターフェースを実装し、メタデータ登録とアップロード状態更新のAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FavoriteController implements FavoriteManagementApi {
    private final FavoriteService favoriteService;
    private final MediaService mediaService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * POST /favorites/{mediaId}<br>
     * メディアをログイン中のユーザーのお気に入りに追加
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId        対象のメディアID
     * @return 201ステータス
     */
    @Override
    public ResponseEntity<Void> addFavorite(String xRequestedWith, Long mediaId) {
        // 認証情報から現在のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();

        // メディアの存在チェック
        mediaService.getMedia(mediaId);

        // サービス層でデータを登録
        favoriteService.addFavorite(mediaId, currentUserId);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /favorites/{mediaId}<br>
     * メディアをお気に入りから削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId        対象のメディアID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> removeFavorite(String xRequestedWith, Long mediaId) {
        // 認証情報から現在のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();

        // メディアの存在チェック
        mediaService.getMedia(mediaId);

        // サービス層でデータを削除
        favoriteService.removeFavorite(mediaId, currentUserId);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }
}
