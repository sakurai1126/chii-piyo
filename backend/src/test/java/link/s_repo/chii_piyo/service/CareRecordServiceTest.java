package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.CareRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareRecordServiceTest {
    @Mock
    private CareRecordRepository careRecordRepository;

    @InjectMocks
    private CareRecordService careRecordService;

    @Nested
    @DisplayName("createCareRecord - 育児記録の作成")
    class CreateCareRecord {
        // 共通リクエストデータの作成
        Long mockUserId = 1L;

        @Test
        @DisplayName("Care-01: 食事記録の作成ができること")
        void createCareRecord_mealSuccess() {
            // リクエスト詳細を作成
            String requestNote = "ごはん";
            MealDetailDto detail = new MealDetailDto();
            detail.setNote(requestNote);

            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);
            request.setMealDetail(JsonNullable.of(detail));

            // 対象の実行
            careRecordService.createCareRecord(request, mockUserId);

            // 食事記録かつリクエストしたユーザーIDで保存処理が呼ばれていることの確認
            verify(careRecordRepository).save(argThat(record ->
                CareRecordRequestDto.RecordTypeEnum.MEAL.toString().equals(record.getRecordType())
                    && mockUserId.equals(record.getRecordedBy())
            ));

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(careRecordRepository).saveMeal(argThat(meal ->
                requestNote.equals(meal.getNote())
            ));
        }

        @Test
        @DisplayName("Care-02: ミルク記録の作成ができること")
        void createCareRecord_milkSuccess() {
            // リクエスト詳細を作成
            String requestNote = "ミルク";
            Integer requestAmountMl = 200;
            MilkDetailDto detail = new MilkDetailDto();
            detail.setNote(requestNote);
            detail.setAmountMl(requestAmountMl);

            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MILK);
            request.setMilkDetail(JsonNullable.of(detail));

            // 対象の実行
            careRecordService.createCareRecord(request, mockUserId);

            // ミルク記録かつリクエストしたユーザーIDで保存処理が呼ばれていることの確認
            verify(careRecordRepository).save(argThat(record ->
                CareRecordRequestDto.RecordTypeEnum.MILK.toString().equals(record.getRecordType())
                    && mockUserId.equals(record.getRecordedBy())
            ));

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(careRecordRepository).saveMilk(argThat(milk ->
                requestNote.equals(milk.getNote()) && requestAmountMl.equals(milk.getAmountMl())
            ));
        }

        @Test
        @DisplayName("Care-03: 排泄記録の作成ができること")
        void createCareRecord_diaperSuccess() {
            // リクエスト詳細を作成
            String requestNote = "おしっこ";

            DiaperDetailDto detail = new DiaperDetailDto();
            detail.setNote(requestNote);
            detail.setDiaperType(DiaperDetailDto.DiaperTypeEnum.WET);

            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.DIAPER);
            request.setDiaperDetail(JsonNullable.of(detail));

            // 対象の実行
            careRecordService.createCareRecord(request, mockUserId);

            // 排泄記録かつリクエストしたユーザーIDで保存処理が呼ばれていることの確認
            verify(careRecordRepository).save(argThat(record ->
                CareRecordRequestDto.RecordTypeEnum.DIAPER.toString().equals(record.getRecordType())
                    && mockUserId.equals(record.getRecordedBy())
            ));

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(careRecordRepository).saveDiaper(argThat(diaper ->
                requestNote.equals(diaper.getNote())
                    && DiaperDetailDto.DiaperTypeEnum.WET.toString().equals(diaper.getDiaperType())
            ));
        }

        @Test
        @DisplayName("Care-04: 体調記録の作成ができること")
        void createCareRecord_healthSuccess() {
            // リクエスト詳細を作成
            String requestNote = "風邪";
            Double requestTemperature = 36.8;

            HealthDetailDto detail = new HealthDetailDto();
            detail.setNote(requestNote);
            detail.setTemperature(JsonNullable.of(requestTemperature));

            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.HEALTH);
            request.setHealthDetail(JsonNullable.of(detail));

            // 対象の実行
            careRecordService.createCareRecord(request, mockUserId);

            // 体調記録かつリクエストしたユーザーIDで保存処理が呼ばれていることの確認
            verify(careRecordRepository).save(argThat(record ->
                CareRecordRequestDto.RecordTypeEnum.HEALTH.toString().equals(record.getRecordType())
                    && mockUserId.equals(record.getRecordedBy())
            ));

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(careRecordRepository).saveHealth(argThat(health ->
                requestNote.equals(health.getNote())
                    && requestTemperature.equals(health.getTemperature().doubleValue())
            ));
        }

        @Test
        @DisplayName("Care-05: 詳細なしの食事記録の作成リクエストが例外で処理されること")
        void createCareRecord_mealMissing() {
            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> careRecordService.createCareRecord(request, mockUserId))
                .isInstanceOf(IllegalArgumentException.class);

            // 保存処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).save(any());
            verify(careRecordRepository, never()).saveMeal(any());
        }

        @Test
        @DisplayName("Care-06: 詳細なしのミルク記録の作成リクエストが例外で処理されること")
        void createCareRecord_milkMissing() {
            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.MILK);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> careRecordService.createCareRecord(request, mockUserId))
                .isInstanceOf(IllegalArgumentException.class);

            // 保存処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).save(any());
            verify(careRecordRepository, never()).saveMilk(any());
        }

        @Test
        @DisplayName("Care-07: 詳細なしの排泄記録の作成リクエストが例外で処理されること")
        void createCareRecord_diaperMissing() {
            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.DIAPER);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> careRecordService.createCareRecord(request, mockUserId))
                .isInstanceOf(IllegalArgumentException.class);

            // 保存処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).save(any());
            verify(careRecordRepository, never()).saveDiaper(any());
        }

        @Test
        @DisplayName("Care-08: 詳細なしの体調記録の作成リクエストが例外で処理されること")
        void createCareRecord_healthMissing() {
            // リクエストデータを作成
            CareRecordRequestDto request = new CareRecordRequestDto();
            request.setRecordType(CareRecordRequestDto.RecordTypeEnum.HEALTH);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> careRecordService.createCareRecord(request, mockUserId))
                .isInstanceOf(IllegalArgumentException.class);

            // 保存処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).save(any());
            verify(careRecordRepository, never()).saveHealth(any());
        }
    }

    @Nested
    @DisplayName("getCareRecords - 育児記録一覧の取得")
    class GetCareRecords {
        @Test
        @DisplayName("Care-09: 育児記録一覧の取得ができること")
        void getCareRecords_success() {
            // リクエストデータの作成
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            // 対象の実行
            careRecordService.getCareRecords(startDate, endDate);

            // 取得処理が呼ばれていることの確認
            verify(careRecordRepository).findRecordsByDate(startDate, endDate);
        }
    }

    @Nested
    @DisplayName("getMealRecords - 食事詳細一覧の取得")
    class GetMealRecords {
        @Test
        @DisplayName("Care-10: 食事詳細一覧の取得ができること")
        void getMealRecords_success() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of(1L, 2L);

            // 対象の実行
            careRecordService.getMealRecords(requestRecordIds);

            // 取得処理が呼ばれていることの確認
            verify(careRecordRepository).findMealRecordsByIds(requestRecordIds);
        }

        @Test
        @DisplayName("Care-11: リクエストIDが空の時の空リストが返ること")
        void getMealRecords_emptyList() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of();

            // 対象の実行
            List<MealDetails> result = careRecordService.getMealRecords(requestRecordIds);

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).findMealRecordsByIds(any());
        }
    }

    @Nested
    @DisplayName("getMilkRecords - ミルク詳細一覧の取得")
    class GetMilkRecords {
        @Test
        @DisplayName("Care-12: ミルク詳細一覧の取得ができること")
        void getMilkRecords_success() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of(1L, 2L);

            // 対象の実行
            careRecordService.getMilkRecords(requestRecordIds);

            // 取得処理が呼ばれていることの確認
            verify(careRecordRepository).findMilkRecordsByIds(requestRecordIds);
        }

        @Test
        @DisplayName("Care-13: リクエストIDが空の時の空リストが返ること")
        void getMilkRecords_emptyList() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of();

            // 対象の実行
            List<MilkDetails> result = careRecordService.getMilkRecords(requestRecordIds);

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).findMilkRecordsByIds(any());
        }
    }

    @Nested
    @DisplayName("getDiaperRecords - 排泄詳細一覧の取得")
    class GetDiaperRecords {
        @Test
        @DisplayName("Care-14: 排泄詳細一覧の取得ができること")
        void getDiaperRecords_success() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of(1L, 2L);

            // 対象の実行
            careRecordService.getDiaperRecords(requestRecordIds);

            // 取得処理が呼ばれていることの確認
            verify(careRecordRepository).findDiaperRecordsByIds(requestRecordIds);
        }

        @Test
        @DisplayName("Care-15: リクエストIDが空の時の空リストが返ること")
        void getDiaperRecords_emptyList() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of();

            // 対象の実行
            List<DiaperDetails> result = careRecordService.getDiaperRecords(requestRecordIds);

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).findDiaperRecordsByIds(any());
        }
    }

    @Nested
    @DisplayName("getHealthRecords - 体調詳細一覧の取得")
    class GetHealthRecords {
        @Test
        @DisplayName("Care-16: 体調詳細一覧の取得ができること")
        void getHealthRecords_success() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of(1L, 2L);

            // 対象の実行
            careRecordService.getHealthRecords(requestRecordIds);

            // 取得処理が呼ばれていることの確認
            verify(careRecordRepository).findHealthRecordsByIds(requestRecordIds);
        }

        @Test
        @DisplayName("Care-17: リクエストIDが空の時の空リストが返ること")
        void getHealthRecords_emptyList() {
            // リクエストデータの作成
            List<Long> requestRecordIds = List.of();

            // 対象の実行
            List<HealthDetails> result = careRecordService.getHealthRecords(requestRecordIds);

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).findHealthRecordsByIds(any());
        }
    }

    @Nested
    @DisplayName("updateCareRecord - 育児記録の更新")
    class UpdateCareRecord {
        // 共通リクエストデータの作成
        Long recordId = 1L;

        @Test
        @DisplayName("Care-18: 食事記録の更新ができること")
        void updateCareRecord_mealSuccess() {
            // リクエスト詳細を作成
            String requestNote = "ごはん";
            MealDetailDto detail = new MealDetailDto();
            detail.setNote(requestNote);

            // リクエストデータ作成
            CareRecordRequestDto updateData = new CareRecordRequestDto();
            updateData.setMealDetail(JsonNullable.of(detail));

            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL.toString());
            MealDetails mockDetail = new MealDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findMealRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.updateCareRecord(recordId, updateData);

            // 更新処理が呼ばれていることの確認
            verify(careRecordRepository).updateMealDetail(
                argThat(meal -> requestNote.equals(meal.getNote()))
            );
            verify(careRecordRepository).updateCareRecord(mockRecord);
        }

        @Test
        @DisplayName("Care-19: ミルク記録の更新ができること")
        void updateCareRecord_milkSuccess() {
            // リクエスト詳細を作成
            String requestNote = "ミルク";
            Integer requestAmountMl = 200;
            MilkDetailDto detail = new MilkDetailDto();
            detail.setNote(requestNote);
            detail.setAmountMl(requestAmountMl);

            // リクエストデータ作成
            CareRecordRequestDto updateData = new CareRecordRequestDto();
            updateData.setMilkDetail(JsonNullable.of(detail));

            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.MILK.toString());
            MilkDetails mockDetail = new MilkDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findMilkRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.updateCareRecord(recordId, updateData);

            // 更新処理が呼ばれていることの確認
            verify(careRecordRepository).updateMilkDetail(
                argThat(milk -> requestNote.equals(milk.getNote())
                    && requestAmountMl.equals(milk.getAmountMl()))
            );
            verify(careRecordRepository).updateCareRecord(mockRecord);
        }

        @Test
        @DisplayName("Care-20: 排泄記録の更新ができること")
        void updateCareRecord_diaperSuccess() {
            // リクエスト詳細を作成
            String requestNote = "おしっこ";
            DiaperDetailDto detail = new DiaperDetailDto();
            detail.setNote(requestNote);
            detail.setDiaperType(DiaperDetailDto.DiaperTypeEnum.WET);

            // リクエストデータ作成
            CareRecordRequestDto updateData = new CareRecordRequestDto();
            updateData.setDiaperDetail(JsonNullable.of(detail));

            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.DIAPER.toString());
            DiaperDetails mockDetail = new DiaperDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findDiaperRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.updateCareRecord(recordId, updateData);

            // 更新処理が呼ばれていることの確認
            verify(careRecordRepository).updateDiaperDetail(
                argThat(diaper -> requestNote.equals(diaper.getNote())
                    && DiaperDetailDto.DiaperTypeEnum.WET.toString().equals(diaper.getDiaperType()))

            );
            verify(careRecordRepository).updateCareRecord(mockRecord);
        }

        @Test
        @DisplayName("Care-21: 体調記録の更新ができること")
        void updateCareRecord_healthSuccess() {
            // リクエスト詳細を作成
            String requestNote = "風邪";
            Double requestTemperature = 36.8;
            HealthDetailDto detail = new HealthDetailDto();
            detail.setNote(requestNote);
            detail.setTemperature(JsonNullable.of(requestTemperature));

            // リクエストデータ作成
            CareRecordRequestDto updateData = new CareRecordRequestDto();
            updateData.setHealthDetail(JsonNullable.of(detail));

            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.HEALTH.toString());
            HealthDetails mockDetail = new HealthDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findHealthRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.updateCareRecord(recordId, updateData);

            // 更新処理が呼ばれていることの確認
            verify(careRecordRepository).updateHealthDetail(
                argThat(health -> requestNote.equals(health.getNote())
                    && requestTemperature.equals(health.getTemperature().doubleValue()))
            );
            verify(careRecordRepository).updateCareRecord(mockRecord);
        }

        @Test
        @DisplayName("Care-22: 存在しないIDの食事記録の更新が例外で処理されること")
        void updateCareRecord_notFound() {
            // リクエスト詳細を作成
            String requestNote = "ごはん";
            MealDetailDto detail = new MealDetailDto();
            detail.setNote(requestNote);

            // リクエストデータ作成
            CareRecordRequestDto updateData = new CareRecordRequestDto();
            updateData.setMealDetail(JsonNullable.of(detail));

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> careRecordService.updateCareRecord(recordId, updateData))
                .isInstanceOf(ResourceNotFoundException.class);

            // 更新処理が呼ばれていないことの確認
            verify(careRecordRepository, never()).updateMealDetail(any());
            verify(careRecordRepository, never()).updateCareRecord(any());
        }
    }

    @Nested
    @DisplayName("deleteCareRecord - 育児記録の削除")
    class DeleteCareRecord {
        // 共通リクエストデータの作成
        Long recordId = 1L;

        @Test
        @DisplayName("Care-23: 食事記録の削除ができること")
        void deleteCareRecord_mealSuccess() {
            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.MEAL.toString());
            MealDetails mockDetail = new MealDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findMealRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.deleteCareRecord(recordId);

            // 削除処理が呼ばれていることの確認
            verify(careRecordRepository).deleteMealByRecordId(recordId);
            verify(careRecordRepository).deleteCareRecordById(recordId);
        }

        @Test
        @DisplayName("Care-24: ミルク記録の削除ができること")
        void deleteCareRecord_milkSuccess() {
            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.MILK.toString());
            MilkDetails mockDetail = new MilkDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findMilkRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.deleteCareRecord(recordId);

            // 削除処理が呼ばれていることの確認
            verify(careRecordRepository).deleteMilkByRecordId(recordId);
            verify(careRecordRepository).deleteCareRecordById(recordId);
        }

        @Test
        @DisplayName("Care-25: 排泄記録の削除ができること")
        void deleteCareRecord_diaperSuccess() {
            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.DIAPER.toString());
            DiaperDetails mockDetail = new DiaperDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findDiaperRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.deleteCareRecord(recordId);

            // 削除処理が呼ばれていることの確認
            verify(careRecordRepository).deleteDiaperByRecordId(recordId);
            verify(careRecordRepository).deleteCareRecordById(recordId);

        }

        @Test
        @DisplayName("Care-26: 体調記録の削除ができること")
        void deleteCareRecord_healthSuccess() {
            // モックデータの作成
            CareRecords mockRecord = new CareRecords();
            mockRecord.setRecordType(CareRecordRequestDto.RecordTypeEnum.HEALTH.toString());
            HealthDetails mockDetail = new HealthDetails();

            // 取得処理のスタブ化
            when(careRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));
            when(careRecordRepository.findHealthRecordsById(recordId)).thenReturn(Optional.of(mockDetail));

            // 対象の実行
            careRecordService.deleteCareRecord(recordId);

            // 削除処理が呼ばれていることの確認
            verify(careRecordRepository).deleteHealthByRecordId(recordId);
            verify(careRecordRepository).deleteCareRecordById(recordId);

        }
    }
}
