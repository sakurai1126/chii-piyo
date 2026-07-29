package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.CareRecordListConverter;
import link.s_repo.chii_piyo.model.gen.CareRecordListResponseDto;
import link.s_repo.chii_piyo.model.gen.CareRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.CareRecordResponseDto;
import link.s_repo.chii_piyo.service.CareRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CareRecordController.class)
public class CareRecordControllerTest extends BaseControllerTest {
    @MockitoBean
    private CareRecordService careRecordService;
    @MockitoBean
    private CareRecordListConverter careRecordListConverter;

    @Nested
    @DisplayName("getCareRecords - 育児記録一覧の取得")
    class GetCareRecords {
        LocalDate mockStartDate = LocalDate.of(2026, 1, 1);
        LocalDate mockEndDate = LocalDate.of(2026, 1, 7);

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("CareCtrl-01: 育児記録一覧の取得ができること")
        void getCareRecords_success() throws Exception {
            Long mockResponseItemId = 1L;
            CareRecordResponseDto item = new CareRecordResponseDto()
                .id(mockResponseItemId);
            CareRecordListResponseDto response = new CareRecordListResponseDto(List.of(item));

            // コンバーター処理のモック化
            when(careRecordListConverter.toCareRecordListResponseDto(
                any(), any(), any(), any(), any())).thenReturn(response);

            // GETリクエストの送信
            mockMvc.perform(get("/care-records")
                    .param("startDate", mockStartDate.toString())
                    .param("endDate", mockEndDate.toString()))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // レスポンスのリスト件数が1件であることを検証
                .andExpect(jsonPath("$.length()").value(1))
                // レスポンスのidが指定のものであることを検証
                .andExpect(jsonPath("$.items[0].id").value(mockResponseItemId));

            // 変換処理がコンバーターに渡ることの検証
            verify(careRecordListConverter)
                .toCareRecordListResponseDto(any(), any(), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("CareCtrl-02: 一般ユーザーで育児記録一覧の取得を試みた場合アクセスが拒否されること")
        void getCareRecords_forbidden() throws Exception {
            // GETリクエストの送信
            mockMvc.perform(get("/care-records")
                    .param("startDate", mockStartDate.toString())
                    .param("endDate", mockEndDate.toString()))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 変換処理がコンバーターに渡っていないことの検証
            verify(careRecordListConverter, never())
                .toCareRecordListResponseDto(any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("createCareRecord - 育児記録の作成")
    class CreateCareRecord {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("CareCtrl-03: 育児記録の作成ができること")
        void createCareRecord_success() throws Exception {
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            // リクエストを作成
            CareRecordRequestDto request = new CareRecordRequestDto();

            // 必須項目をセット
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);
            request.setRecordedAt(OffsetDateTime.now());

            // ログインユーザーID取得のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // POSTリクエストの送信
            mockMvc.perform(post("/care-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを確認
            verify(careRecordService).createCareRecord(
                any(CareRecordRequestDto.class), eq(mockCurrentUserId));
        }

        @Test
        @WithMockUser
        @DisplayName("CareCtrl-04: 一般ユーザーで育児記録の作成を試みた場合アクセスが拒否されること")
        void createCareRecord_forbidden() throws Exception {
            // リクエストを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            // 必須項目をセット
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);
            request.setRecordedAt(OffsetDateTime.now());

            // POSTリクエストの送信
            mockMvc.perform(post("/care-records")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていないことを確認
            verify(careRecordService, never()).createCareRecord(any(), any());
        }
    }

    @Nested
    @DisplayName("updateCareRecord - 育児記録の更新")
    class UpdateCareRecord {
        // リクエストを作成
        Long mockCareRecordId = 1L;
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("CareCtrl-05: 育児記録の更新ができること")
        void updateCareRecord_success() throws Exception {
            CareRecordRequestDto request = new CareRecordRequestDto();
            // 必須項目をセット
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);
            request.setRecordedAt(OffsetDateTime.now());

            // PATCHリクエストの送信
            mockMvc.perform(patch("/care-records/{id}", mockCareRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを確認
            verify(careRecordService).updateCareRecord(
                eq(mockCareRecordId), any(CareRecordRequestDto.class));
        }

        @Test
        @WithMockUser
        @DisplayName("CareCtrl-06: 一般ユーザーで育児記録の更新を試みた場合アクセスが拒否されること")
        void updateCareRecord_forbidden() throws Exception {
            // リクエストを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            // 必須項目をセット
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);
            request.setRecordedAt(OffsetDateTime.now());

            // PATCHリクエストの送信
            mockMvc.perform(patch("/care-records/{id}", mockCareRecordId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを確認
            verify(careRecordService, never()).updateCareRecord(any(), any());
        }
    }


    @Nested
    @DisplayName("deleteCareRecord - 育児記録の削除")
    class DeleteCareRecord {
        // モックデータを作成
        Long mockCareRecordId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("CareCtrl-07: 育児記録の削除ができること")
        void deleteCareRecord_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/care-records/{id}", mockCareRecordId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを確認
            verify(careRecordService).deleteCareRecord(mockCareRecordId);
        }

        @Test
        @WithMockUser
        @DisplayName("CareCtrl-08: 一般ユーザーで育児記録の削除を試みた場合アクセスが拒否されること")
        void deleteCareRecord_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/care-records/{id}", mockCareRecordId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを確認
            verify(careRecordService, never()).deleteCareRecord(any());
        }
    }
}



