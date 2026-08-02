package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.TrashItemConverter;
import link.s_repo.chii_piyo.controller.converter.TrashItemListConverter;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.model.gen.TrashRestoreRequestDto;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TrashService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrashController.class)
public class TrashControllerTest extends BaseControllerTest {
    @MockitoBean
    private TrashItemConverter trashItemConverter;
    @MockitoBean
    private TrashItemListConverter trashItemListConverter;
    @MockitoBean
    private TrashService trashService;
    @MockitoBean
    private MediaService mediaService;
    @MockitoBean
    private MediaConverter mediaConverter;
    @MockitoBean
    private S3StorageManager s3StorageManager;

    @Nested
    @DisplayName("getTrashItems - ゴミ箱一覧情報の取得")
    class GetTrashItems {
        // リクエストモックデータの作成
        Integer mockOffset = 0;
        Integer mockLimit = 20;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-01: ゴミ箱の一覧情報が取得できること")
        void getTrashItems_success() throws Exception {

            // 残り日数と総件数のモックデータを作成
            Long mockEarliest = 5L;
            Long mockTotalCount = 10L;

            // ゴミ箱アイテムモックデータの作成
            TrashItems trashItem = new TrashItems();
            trashItem.setId(1L);

            // 取得処理のスタブ化
            when(trashService.getTrashItems(mockOffset, mockLimit)).thenReturn(List.of(trashItem));

            // 残り日数取得のスタブ化
            when(trashService.getEarliestDeadline()).thenReturn(mockEarliest);

            // 総件数取得のスタブ化
            when(trashService.getTotalCount()).thenReturn(mockTotalCount);

            // GETリクエストの送信
            mockMvc.perform(get("/trash")
                    .param("offset", mockOffset.toString())
                    .param("limit", mockLimit.toString()))
                .andExpect(status().isOk());

            // 渡したリクエストで取得が行われていることを確認
            verify(trashService).getTrashItems(mockOffset, mockLimit);

            // 残り日数・総件数・hasNextの算出結果がConverterに渡ること
            verify(trashItemListConverter).toTrashItemListResponseDto(
                any(), eq(mockEarliest), eq(mockTotalCount), eq(true));
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-02: 一般ユーザーでゴミ箱の一覧情報取得を試みた場合アクセスが拒否されること")
        void getTrashItems_forbidden() throws Exception {
            // GETリクエストの送信
            mockMvc.perform(get("/trash")
                    .param("offset", mockOffset.toString())
                    .param("limit", mockLimit.toString()))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 取得処理が呼ばれていないことを検証
            verify(trashService, never()).getTrashItems(any(), any());
        }
    }

    @Nested
    @DisplayName("restoreTrashItem - ゴミ箱からメディアの復元")
    class RestoreTrashItem {
        Long mockRequestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-03: ゴミ箱からメディアの復元ができること")
        void restoreTrashItem_success() throws Exception {
            // POSTリクエストの送信
            mockMvc.perform(post("/trash/{id}/restore", mockRequestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 復元処理が呼ばれていることを検証
            verify(trashService).restoreTrashItem(mockRequestId);
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-04: 一般ユーザーでゴミ箱からメディアの復元を試みた場合アクセスが拒否されること")
        void restoreTrashItem_forbidden() throws Exception {
            // POSTリクエストの送信
            mockMvc.perform(post("/trash/{id}/restore", mockRequestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 復元処理が呼ばれていないことを検証
            verify(trashService, never()).restoreTrashItem(mockRequestId);
        }
    }

    @Nested
    @DisplayName("restoreTrashItems - ゴミ箱から複数メディアの復元")
    class RestoreTrashItems {
        List<Long> mockRequestIds = List.of(1L, 2L);

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-05: ゴミ箱から複数メディアの復元ができること")
        void restoreTrashItems_success() throws Exception {
            TrashRestoreRequestDto request = new TrashRestoreRequestDto();
            request.setTrashItemIds(mockRequestIds);

            // POSTリクエストの送信
            mockMvc.perform(post("/trash")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 復元処理が呼ばれていることを検証
            verify(trashService).restoreTrashItems(mockRequestIds);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-06: 空リストで複数メディアの復元リクエストをした場合400で返ること")
        void restoreTrashItems_emptyList() throws Exception {
            TrashRestoreRequestDto request = new TrashRestoreRequestDto();
            request.setTrashItemIds(List.of());

            // POSTリクエストの送信
            mockMvc.perform(post("/trash")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 400 Bad Requestであることを確認
                .andExpect(status().isBadRequest());

            // 復元処理が呼ばれていないことを検証
            verify(trashService, never()).restoreTrashItems(any());
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-07: 一般ユーザーでゴミ箱から複数メディアの復元を試みた場合アクセスが拒否されること")
        void restoreTrashItems_forbidden() throws Exception {
            TrashRestoreRequestDto request = new TrashRestoreRequestDto();
            request.setTrashItemIds(mockRequestIds);

            // POSTリクエストの送信
            mockMvc.perform(post("/trash")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 復元処理が呼ばれていないことを検証
            verify(trashService, never()).restoreTrashItems(any());
        }
    }

    @Nested
    @DisplayName("deleteTrashItem - ゴミ箱からメディアの完全削除")
    class DeleteTrashItem {
        Long mockRequestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-08: ゴミ箱からメディアの完全削除ができること")
        void deleteTrashItem_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash/{id}", mockRequestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(trashService).permanentlyDelete(mockRequestId);
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-09: 一般ユーザーでゴミ箱からメディアの完全削除を試みた場合アクセスが拒否されること")
        void deleteTrashItem_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash/{id}", mockRequestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを検証
            verify(trashService, never()).permanentlyDelete(any());
        }
    }

    @Nested
    @DisplayName("deleteTrashItems - ゴミ箱から複数メディアの完全削除")
    class DeleteTrashItems {
        String mockRequestIdsString = "1,2";
        List<Long> mockRequestIds = List.of(1L, 2L);

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-10: ゴミ箱から複数メディアの完全削除ができること")
        void deleteTrashItems_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash")
                    .param("trashItemIds", mockRequestIdsString))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(trashService).multiplePermanentlyDelete(mockRequestIds);
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-11: 一般ユーザーでゴミ箱から複数メディアの完全削除を試みた場合アクセスが拒否されること")
        void deleteTrashItems_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash")
                    .param("trashItemIds", mockRequestIdsString))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていることを検証
            verify(trashService, never()).multiplePermanentlyDelete(any());
        }
    }

    @Nested
    @DisplayName("emptyTrash - ゴミ箱から全てのメディアの完全削除")
    class EmptyTrash {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TrashCtrl-12: ゴミ箱から全てのメディアの完全削除ができること")
        void emptyTrash_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash/empty"))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(trashService).allDelete();
        }

        @Test
        @WithMockUser
        @DisplayName("TrashCtrl-13: 一般ユーザーでゴミ箱から全てのメディアの完全削除を試みた場合アクセスが拒否されること")
        void emptyTrash_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/trash/empty"))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを検証
            verify(trashService, never()).allDelete();
        }
    }
}
