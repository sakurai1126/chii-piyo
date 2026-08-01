package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.service.FavoriteService;
import link.s_repo.chii_piyo.service.MediaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
public class FavoriteControllerTest extends BaseControllerTest {
    @MockitoBean
    private FavoriteService favoriteService;
    @MockitoBean
    private MediaService mediaService;

    @Nested
    @DisplayName("addFavorite - お気に入りに追加")
    class AddFavorite {
        Long mockRequestId = 1L;
        Long mockCurrentUserId = 2L;

        @Test
        @WithMockUser
        @DisplayName("FavCtrl-01: お気に入りに追加できること")
        void addFavorite_success() throws Exception {
            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // POSTリクエストの送信
            mockMvc.perform(post("/favorites/{mediaId}", mockRequestId))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 登録処理が呼ばれていることを検証
            verify(favoriteService).addFavorite(mockRequestId, mockCurrentUserId);
        }

        @Test
        @WithMockUser
        @DisplayName("FavCtrl-02: 存在しないメディアIDでお気に入りに追加しようとした場合404が返ること")
        void addFavorite_notFound() throws Exception {
            // メディア取得処理のモック化
            when(mediaService.getMedia(mockRequestId))
                .thenThrow(new ResourceNotFoundException("メディアが見つかりません mediaId=" + mockRequestId));

            // POSTリクエストの送信
            mockMvc.perform(post("/favorites/{mediaId}", mockRequestId))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // 登録処理が呼ばれていないことを検証
            verify(favoriteService, never()).addFavorite(any(), any());
        }
    }

    @Nested
    @DisplayName("removeFavorite - お気に入りから削除")
    class RemoveFavorite {
        Long mockRequestId = 1L;
        Long mockCurrentUserId = 2L;

        @Test
        @WithMockUser
        @DisplayName("FavCtrl-03: お気に入りから削除できること")
        void removeFavorite_success() throws Exception {
            // ユーザーID取得処理のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // DELETEリクエストの送信
            mockMvc.perform(delete("/favorites/{mediaId}", mockRequestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(favoriteService).removeFavorite(mockRequestId, mockCurrentUserId);
        }

        @Test
        @WithMockUser
        @DisplayName("FavCtrl-04: 存在しないメディアIDでお気に入りから削除しようとした場合404が返ること")
        void removeFavorite_notFound() throws Exception {
            // ユーザーID取得処理のモック化
            when(mediaService.getMedia(mockRequestId))
                .thenThrow(new ResourceNotFoundException("メディアが見つかりません mediaId=" + mockRequestId));

            // DELETEリクエストの送信
            mockMvc.perform(delete("/favorites/{mediaId}", mockRequestId))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // 登録処理が呼ばれていないことを検証
            verify(favoriteService, never()).removeFavorite(any(), any());
        }
    }
}
