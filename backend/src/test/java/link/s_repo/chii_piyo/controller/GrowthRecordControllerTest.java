package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.GrowthRecordConverter;
import link.s_repo.chii_piyo.model.gen.GrowthRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.service.GrowthRecordService;
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

@WebMvcTest(GrowthRecordController.class)
public class GrowthRecordControllerTest extends BaseControllerTest {
    @MockitoBean
    private GrowthRecordService growthRecordService;
    @MockitoBean
    private GrowthRecordConverter growthRecordConverter;

    @Nested
    @DisplayName("getGrowthRecords - 身長・体重記録一覧の取得")
    class GetGrowthRecords {
        @Test
        @WithMockUser
        @DisplayName("GrowthCtrl-01: 身長・体重記録一覧の取得ができること")
        void getGrowthRecords_success() throws Exception {
            LocalDate mockStartDate = LocalDate.of(2026, 1, 1);
            LocalDate mockEndDate = LocalDate.of(2026, 1, 7);

            // モックデータの作成
            Long mockResponseItemId = 1L;

            //
            GrowthRecords mockRecord = new GrowthRecords();
            mockRecord.setId(mockResponseItemId);

            // サービス層から返るモックデータの作成
            GrowthRecordResponseDto mockResponseDto = new GrowthRecordResponseDto()
                .id(mockResponseItemId);

            // 取得処理のスタブ化
            when(growthRecordService.getGrowthRecords(mockStartDate, mockEndDate))
                .thenReturn(List.of(mockRecord));

            // コンバーター処理のスタブ化
            when(growthRecordConverter.toGrowthRecordResponseDto(mockRecord))
                .thenReturn(mockResponseDto);

            // GETリクエストの送信
            mockMvc.perform(get("/growth-records")
                    .param("startDate", mockStartDate.toString())
                    .param("endDate", mockEndDate.toString()))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // レスポンスのリスト件数が1件であることを検証
                .andExpect(jsonPath("$.length()").value(1))
                // レスポンスのidが指定のものであることを検証
                .andExpect(jsonPath("$[0].id").value(mockResponseItemId));

            // 変換処理がコンバーターに渡ることの検証
            verify(growthRecordConverter).toGrowthRecordResponseDto(mockRecord);
        }
    }

    @Nested
    @DisplayName("createGrowthRecord - 身長・体重記録の作成")
    class CreateGrowthRecord {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GrowthCtrl-02: 身長・体重記録の作成ができること")
        void createGrowthRecord_success() throws Exception {
            // リクエストを作成
            GrowthRecordRequestDto request = new GrowthRecordRequestDto();

            // 必須項目をセット
            request.setMeasurementDate(LocalDate.of(2026, 1, 1));
            request.setNote("1歳の誕生日の身長");

            // POSTリクエストの送信
            mockMvc.perform(post("/growth-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを確認
            verify(growthRecordService).createGrowthRecord(request);
        }

        @Test
        @WithMockUser
        @DisplayName("GrowthCtrl-03: 一般ユーザーで身長・体重記録の作成を試みた場合アクセスが拒否されること")
        void createGrowthRecord_forbidden() throws Exception {
            // リクエストを作成
            GrowthRecordRequestDto request = new GrowthRecordRequestDto();

            // 必須項目をセット
            request.setMeasurementDate(LocalDate.of(2026, 1, 1));
            request.setNote("1歳の誕生日の身長");

            // POSTリクエストの送信
            mockMvc.perform(post("/growth-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていないことを確認
            verify(growthRecordService, never()).createGrowthRecord(any());
        }
    }

    @Nested
    @DisplayName("updateGrowthRecord - 身長・体重記録の更新")
    class UpdateGrowthRecord {
        Long mockRequestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GrowthCtrl-04: 身長・体重記録の更新ができること")
        void updateGrowthRecord_success() throws Exception {
            // リクエストを作成
            GrowthRecordRequestDto request = new GrowthRecordRequestDto();

            // 必須項目をセット
            request.setMeasurementDate(LocalDate.of(2026, 1, 1));
            request.setNote("1歳の誕生日の身長");

            // PUTリクエストの送信
            mockMvc.perform(put("/growth-records/{id}", mockRequestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 作成処理が呼ばれていることを確認
            verify(growthRecordService).updateGrowthRecord(mockRequestId, request);
        }

        @Test
        @WithMockUser
        @DisplayName("GrowthCtrl-05: 一般ユーザーで身長・体重記録の更新を試みた場合アクセスが拒否されること")
        void updateGrowthRecord_forbidden() throws Exception {
            // リクエストを作成
            GrowthRecordRequestDto request = new GrowthRecordRequestDto();

            // 必須項目をセット
            request.setMeasurementDate(LocalDate.of(2026, 1, 1));
            request.setNote("1歳の誕生日の身長");

            // PUTリクエストの送信
            mockMvc.perform(put("/growth-records/{id}", mockRequestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていないことを確認
            verify(growthRecordService, never()).updateGrowthRecord(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteGrowthRecord - 身長・体重記録の削除")
    class DeleteGrowthRecord {
        Long mockRequestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GrowthCtrl-06: 身長・体重記録の削除ができること")
        void deleteGrowthRecord_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/growth-records/{id}", mockRequestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを確認
            verify(growthRecordService).deleteGrowthRecord(mockRequestId);
        }

        @Test
        @WithMockUser
        @DisplayName("GrowthCtrl-07: 一般ユーザーで身長・体重記録の削除を試みた場合アクセスが拒否されること")
        void deleteGrowthRecord_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/growth-records/{id}", mockRequestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを確認
            verify(growthRecordService, never()).deleteGrowthRecord(any());
        }
    }
}
