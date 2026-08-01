package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.FirstRecordConverter;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.service.FirstRecordService;
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


@WebMvcTest(FirstRecordController.class)
public class FirstRecordControllerTest extends BaseControllerTest {
    @MockitoBean
    private FirstRecordService firstRecordService;
    @MockitoBean
    private FirstRecordConverter firstRecordConverter;
    @MockitoBean
    private S3StorageManager s3StorageManager;
    @MockitoBean
    private MediaConverter mediaConverter;

    @Nested
    @DisplayName("getFirstRecords - はじめて記録一覧の取得")
    class GetFirstRecords {
        @Test
        @WithMockUser
        @DisplayName("FirstCtrl-01: はじめて記録一覧の取得ができること")
        void getFirstRecords_success() throws Exception {
            // モックデータの作成
            Long mockResponseItemId = 1L;
            FirstRecords mockRecord = new FirstRecords();
            mockRecord.setId(mockResponseItemId);

            // 取得処理で取得するデータのモック
            FirstRecordService.FirstRecordWithMedia mockItem =
                new FirstRecordService.FirstRecordWithMedia(mockRecord, List.of());

            // レスポンスデータのモック
            FirstRecordResponseDto mockResponseDto = new FirstRecordResponseDto()
                .id(mockResponseItemId);

            // 取得処理のスタブ化
            when(firstRecordService.getFirstRecords())
                .thenReturn(List.of(mockItem));

            // コンバーター処理のスタブ化
            when(firstRecordConverter.toFirstRecordResponseDto(any(), any()))
                .thenReturn(mockResponseDto);

            // GETリクエストの送信
            mockMvc.perform(get("/first-records"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // レスポンスのリスト件数が1件であることを検証
                .andExpect(jsonPath("$.length()").value(1))
                // レスポンスのidが指定のものであることを検証
                .andExpect(jsonPath("$[0].id").value(mockResponseItemId));

            // 変換処理がコンバーターに渡ることの検証
            verify(firstRecordConverter).toFirstRecordResponseDto(any(), any());
        }
    }

    @Nested
    @DisplayName("createFirstRecord - はじめて記録の作成")
    class CreateFirstRecord {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("FirstCtrl-02: はじめて記録の作成ができること")
        void createFirstRecord_success() throws Exception {
            // リクエストデータのモック
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("つかまり立ち");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのつかまり立ち");
            request.setMediaIds(List.of(1L, 2L));

            // POSTリクエストの送信
            mockMvc.perform(post("/first-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを確認
            verify(firstRecordService).createFirstRecord(request);
        }

        @Test
        @WithMockUser
        @DisplayName("FirstCtrl-03: 一般ユーザーではじめて記録の作成を試みた場合アクセスが拒否されること")
        void createFirstRecord_forbidden() throws Exception {
            // リクエストデータのモック
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("つかまり立ち");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのつかまり立ち");
            request.setMediaIds(List.of(1L, 2L));

            // POSTリクエストの送信
            mockMvc.perform(post("/first-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていることを確認
            verify(firstRecordService, never()).createFirstRecord(any());
        }
    }

    @Nested
    @DisplayName("updateFirstRecord - はじめて記録の更新")
    class UpdateFirstRecord {
        Long mockFirstRecordId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("FirstCtrl-04: はじめて記録の更新ができること")
        void updateFirstRecord_success() throws Exception {
            // リクエストデータのモック
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("つかまり立ち");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのつかまり立ち");
            request.setMediaIds(List.of(1L, 2L));

            // PUTリクエストの送信
            mockMvc.perform(put("/first-records/{id}", mockFirstRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを確認
            verify(firstRecordService).updateFirstRecord(mockFirstRecordId, request);
        }

        @Test
        @WithMockUser
        @DisplayName("FirstCtrl-05: 一般ユーザーではじめて記録の更新を試みた場合アクセスが拒否されること")
        void updateFirstRecord_forbidden() throws Exception {
            // リクエストデータのモック
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("つかまり立ち");
            request.setRecordedDate(LocalDate.of(2026, 1, 1));
            request.setComment("はじめてのつかまり立ち");
            request.setMediaIds(List.of(1L, 2L));

            // PUTリクエストの送信
            mockMvc.perform(put("/first-records/{id}", mockFirstRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを確認
            verify(firstRecordService, never()).updateFirstRecord(any(), any());
        }
    }


    @Nested
    @DisplayName("deleteFirstRecord - はじめて記録の削除")
    class DeleteFirstRecord {
        Long mockFirstRecordId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("FirstCtrl-06: はじめて記録の削除ができること")
        void deleteFirstRecord_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/first-records/{id}", mockFirstRecordId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを確認
            verify(firstRecordService).deleteFirstRecord(mockFirstRecordId);
        }

        @Test
        @WithMockUser
        @DisplayName("FirstCtrl-07: 一般ユーザーではじめて記録一覧の削除を試みた場合アクセスが拒否されること")
        void deleteFirstRecord_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/first-records/{id}", mockFirstRecordId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを確認
            verify(firstRecordService, never()).deleteFirstRecord(any());
        }
    }
}

