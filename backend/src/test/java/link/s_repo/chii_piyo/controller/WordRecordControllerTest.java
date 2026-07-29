package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.WordRecordConverter;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.WordRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.service.WordRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordRecordController.class)
public class WordRecordControllerTest extends BaseControllerTest {
    @MockitoBean
    private WordRecordService wordRecordService;
    @MockitoBean
    private WordRecordConverter wordRecordConverter;
    @MockitoBean
    private S3StorageManager s3StorageManager;
    @MockitoBean
    private MediaConverter mediaConverter;

    @Nested
    @DisplayName("getWordRecords - ことばの記録一覧の取得")
    class GetWordRecords {
        @Test
        @WithMockUser
        @DisplayName("WordCtrl-01: ことばの記録一覧の取得ができること")
        void getWordRecords_success() throws Exception {
            // モックデータの作成
            Long mockResponseItemId = 1L;
            WordRecords mockRecord = new WordRecords();
            mockRecord.setId(mockResponseItemId);

            // 取得処理で取得するデータのモック
            WordRecordService.WordRecordWithMedia mockItem =
                new WordRecordService.WordRecordWithMedia(mockRecord, List.of());

            // レスポンスデータのモック
            WordRecordResponseDto mockResponseDto = new WordRecordResponseDto()
                .id(mockResponseItemId);

            // 取得処理のモック化
            when(wordRecordService.getWordRecords())
                .thenReturn(List.of(mockItem));

            // コンバーター処理のモック化
            when(wordRecordConverter.toWordRecordResponseDto(any(), any()))
                .thenReturn(mockResponseDto);

            // GETリクエストの送信
            mockMvc.perform(get("/word-records"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // レスポンスのリスト件数が1件であることを検証
                .andExpect(jsonPath("$.length()").value(1))
                // レスポンスのidが指定のものであることを検証
                .andExpect(jsonPath("$[0].id").value(mockResponseItemId));

            // 変換処理がコンバーターに渡ることの検証
            verify(wordRecordConverter).toWordRecordResponseDto(any(), any());
        }
    }

    @Nested
    @DisplayName("createWordRecord - ことばの記録の作成")
    class CreateWordRecord {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("WordCtrl-02: ことばの記録の作成ができること")
        void createWordRecord_success() throws Exception {
            // リクエストデータのモック
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのことば");
            request.setMediaIds(List.of(1L, 2L));

            // POSTリクエストの送信
            mockMvc.perform(post("/word-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを確認
            verify(wordRecordService).createWordRecord(request);
        }

        @Test
        @WithMockUser
        @DisplayName("WordCtrl-03: 一般ユーザーでことばの記録の作成を試みた場合アクセスが拒否されること")
        void createWordRecord_forbidden() throws Exception {
            // リクエストデータのモック
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのことば");
            request.setMediaIds(List.of(1L, 2L));

            // POSTリクエストの送信
            mockMvc.perform(post("/word-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていることを確認
            verify(wordRecordService, never()).createWordRecord(any());
        }
    }

    @Nested
    @DisplayName("updateWordRecord - ことばの記録の更新")
    class UpdateWordRecord {
        Long mockWordRecordId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("WordCtrl-04: ことばの記録の更新ができること")
        void updateWordRecord_success() throws Exception {
            // リクエストデータのモック
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのことば");
            request.setMediaIds(List.of(1L, 2L));

            // PUTリクエストの送信
            mockMvc.perform(put("/word-records/{id}", mockWordRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを確認
            verify(wordRecordService).updateWordRecord(mockWordRecordId, request);
        }

        @Test
        @WithMockUser
        @DisplayName("WordCtrl-05: 一般ユーザーでことばの記録の更新を試みた場合アクセスが拒否されること")
        void updateWordRecord_forbidden() throws Exception {
            // リクエストデータのモック
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのことば");
            request.setMediaIds(List.of(1L, 2L));

            // PUTリクエストの送信
            mockMvc.perform(put("/word-records/{id}", mockWordRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを確認
            verify(wordRecordService, never()).updateWordRecord(any(), any());
        }
    }


    @Nested
    @DisplayName("deleteWordRecord - ことばの記録の削除")
    class DeleteWordRecord {
        Long mockWordRecordId = 1L;
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("WordCtrl-06: ことばの記録の削除ができること")
        void deleteWordRecord_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/word-records/{id}", mockWordRecordId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを確認
            verify(wordRecordService).deleteWordRecord(mockWordRecordId);
        }

        @Test
        @WithMockUser
        @DisplayName("WordCtrl-07: 一般ユーザーでことばの記録の削除を試みた場合アクセスが拒否されること")
        void deleteWordRecord_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/word-records/{id}", mockWordRecordId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを確認
            verify(wordRecordService, never()).deleteWordRecord(any());
        }
    }
}

