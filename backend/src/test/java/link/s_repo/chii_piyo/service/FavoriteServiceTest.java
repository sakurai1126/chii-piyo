package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Favorites;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FavoriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Nested
    @DisplayName("addFavorite - お気に入り登録")
    class AddFavorite {
        // 共通リクエストデータの作成
        Long requestMediaId = 1L;
        Long mockCurrentUserId = 2L;

        @Test
        @DisplayName("Fav-01: お気に入り登録ができること")
        void addFavorite_success() {
            // 判定処理のスタブ化
            when(favoriteRepository.countByMediaIdAndUserId(requestMediaId, mockCurrentUserId)).thenReturn(0L);

            // 対象の実行
            favoriteService.addFavorite(requestMediaId, mockCurrentUserId);

            // 登録処理が呼ばれたことの確認
            verify(favoriteRepository).save(any(Favorites.class));
        }

        @Test
        @DisplayName("Fav-02: 追加済みの場合即時リターンされること")
        void addFavorite_alreadyFavorited() {
            // 判定処理のスタブ化
            when(favoriteRepository.countByMediaIdAndUserId(requestMediaId, mockCurrentUserId)).thenReturn(1L);

            // 対象の実行
            favoriteService.addFavorite(requestMediaId, mockCurrentUserId);

            // 登録処理が呼ばれていないことの確認
            verify(favoriteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeFavorite - お気に入り解除")
    class RemoveFavorite {
        @Test
        @DisplayName("Fav-03: お気に入り解除ができること")
        void removeFavorite_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long mockCurrentUserId = 2L;

            // 対象の実行
            favoriteService.removeFavorite(requestMediaId, mockCurrentUserId);

            // 解除処理が呼ばれたことの確認
            verify(favoriteRepository).deleteByMediaIdAndUserId(requestMediaId, mockCurrentUserId);
        }
    }

    @Nested
    @DisplayName("getCurrentUserIsFavorite - お気に入りに追加済かの判定")
    class GetCurrentUserIsFavorite {
        // 共通リクエストデータの作成
        Long requestMediaId = 1L;
        Long mockCurrentUserId = 2L;

        @Test
        @DisplayName("Fav-04: 追加済の判定ができること")
        void getCurrentUserIsFavorite_isFavorite() {
            // 判定処理のスタブ化
            when(favoriteRepository.countByMediaIdAndUserId(requestMediaId, mockCurrentUserId)).thenReturn(1L);

            // 対象を実行し結果を取得
            boolean result = favoriteService.getCurrentUserIsFavorite(requestMediaId, mockCurrentUserId);

            // 結果の検証
            assertThat(result).isEqualTo(true);
        }

        @Test
        @DisplayName("Fav-05: 未追加の判定ができること")
        void getCurrentUserIsFavorite_notFavorite() {
            // 判定処理のスタブ化
            when(favoriteRepository.countByMediaIdAndUserId(requestMediaId, mockCurrentUserId)).thenReturn(0L);

            // 対象を実行し結果を取得
            boolean result = favoriteService.getCurrentUserIsFavorite(requestMediaId, mockCurrentUserId);

            // 結果の検証
            assertThat(result).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("getAddFavoriteUserIds - お気に入りに追加したユーザーのIDリスト取得")
    class GetAddFavoriteUserIds {
        @Test
        @DisplayName("Fav-06: お気に入りに追加したユーザーのIDリスト取得ができること")
        void getAddFavoriteUserIds_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;

            // モックデータの作成
            Long mockUserId = 2L;
            Favorites mockFavorites = new Favorites();
            mockFavorites.setUserId(mockUserId);

            // 取得処理のスタブ化
            when(favoriteRepository.findByMediaId(requestMediaId)).thenReturn(List.of(mockFavorites));

            // 対象を実行し結果を取得
            List<Long> result = favoriteService.getAddFavoriteUserIds(requestMediaId);

            // 結果の検証
            assertThat(result.getFirst()).isEqualTo(mockUserId);
        }
    }

    @Nested
    @DisplayName("getFavoriteList - 複数メディアのお気に入りの追加状況取得")
    class GetFavoriteList {
        @Test
        @DisplayName("Fav-07: 複数メディアのお気に入りの追加状況取得ができること")
        void getFavoriteList_success() {
            // モックデータの取得
            Long mockMediaId = 1L;
            Long mockFavoritesId = 2L;
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);
            Favorites mockFavorites = new Favorites();
            mockFavorites.setId(mockFavoritesId);

            // 取得処理のスタブ化
            when(favoriteRepository.findByMediaIds(List.of(mockMediaId))).thenReturn(List.of(mockFavorites));

            // 対象を実行し結果を取得
            List<Favorites> result = favoriteService.getFavoriteList(List.of(mockMedia));

            // 結果の検証
            assertThat(result.getFirst().getId()).isEqualTo(mockFavoritesId);
        }

        @Test
        @DisplayName("Fav-08: 結果が空の場合空リストが返ること")
        void getFavoriteList_emptyList() {
            // 対象を実行し結果を取得
            List<Favorites> result = favoriteService.getFavoriteList(List.of());

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(favoriteRepository, never()).findByMediaIds(any());
        }
    }
}
