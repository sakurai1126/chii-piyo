package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.GrowthRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.repository.GrowthRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GrowthRecordServiceTest {
    @Mock
    private GrowthRecordRepository growthRecordRepository;

    @InjectMocks
    private GrowthRecordService growthRecordService;

    @Nested
    @DisplayName("createGrowthRecord - 成長記録の作成")
    class CreateGrowthRecord {

        @Test
        @DisplayName("Growth-01: 成長記録の作成ができること")
        void createGrowthRecord_success() {
            // リクエストデータの作成
            JsonNullable<Double> requestWeight = JsonNullable.of(10.0);
            JsonNullable<Double> requestHeight = JsonNullable.of(75.0);
            String requestNote = "今月の身長と体重";
            LocalDate requestMeasurementDate = LocalDate.now();

            GrowthRecordRequestDto request = new GrowthRecordRequestDto();
            request.setWeight(requestWeight);
            request.setHeight(requestHeight);
            request.setNote(requestNote);
            request.setMeasurementDate(requestMeasurementDate);

            // 対象の実行
            growthRecordService.createGrowthRecord(request);

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(growthRecordRepository).save(argThat(record ->
                BigDecimal.valueOf(requestWeight.get()).compareTo(record.getWeight()) == 0
                    && BigDecimal.valueOf(requestHeight.get()).compareTo(record.getHeight()) == 0
                    && requestNote.equals(record.getNote())
                    && requestMeasurementDate.equals(record.getMeasurementDate())
            ));
        }
    }

    @Nested
    @DisplayName("getGrowthRecords - 成長記録一覧の取得")
    class GetGrowthRecords {
        @Test
        @DisplayName("Growth-02: 成長記録一覧の取得ができること")
        void getGrowthRecords_success() {
            // リクエストデータの作成
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            // 対象の実行
            growthRecordService.getGrowthRecords(startDate, endDate);

            // 取得処理が呼ばれていることの確認
            verify(growthRecordRepository).findRecordsByDate(startDate, endDate);
        }
    }

    @Nested
    @DisplayName("updateGrowthRecord - 成長記録の更新")
    class UpdateGrowthRecord {
        // 共通リクエストデータの作成
        Long recordId = 1L;

        @Test
        @DisplayName("Growth-03: 成長記録の更新ができること")
        void updateGrowthRecord_success() {
            // リクエストデータの作成
            GrowthRecordRequestDto updateData = new GrowthRecordRequestDto();
            JsonNullable<Double> requestHeight = JsonNullable.of(75.0);
            JsonNullable<Double> requestWeight = JsonNullable.of(10.0);
            LocalDate requestMeasurementDate = LocalDate.now();

            String requestNote = "今月の身長と体重";


            updateData.setHeight(requestHeight);
            updateData.setWeight(requestWeight);
            updateData.setNote(requestNote);
            updateData.setMeasurementDate(requestMeasurementDate);

            // モックデータの作成
            GrowthRecords mockRecord = new GrowthRecords();

            // 取得処理のスタブ化
            when(growthRecordRepository.findById(recordId)).thenReturn(Optional.of(mockRecord));

            // 対象の実行
            growthRecordService.updateGrowthRecord(recordId, updateData);

            // リクエストした詳細情報で詳細の保存処理が呼ばれていることの確認
            verify(growthRecordRepository).updateGrowthRecord(argThat(record ->
                BigDecimal.valueOf(requestWeight.get()).compareTo(record.getWeight()) == 0
                    && BigDecimal.valueOf(requestHeight.get()).compareTo(record.getHeight()) == 0
                    && requestNote.equals(record.getNote())
                    && requestMeasurementDate.equals(record.getMeasurementDate())
            ));
        }

        @Test
        @DisplayName("Growth-04: 存在しないIDで更新リクエストをした場合例外で処理されること")
        void updateGrowthRecord_notFound() {
            // リクエストデータの作成
            GrowthRecordRequestDto updateData = new GrowthRecordRequestDto();

            // 取得処理のスタブ化
            when(growthRecordRepository.findById(recordId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> growthRecordService.updateGrowthRecord(recordId, updateData))
                .isInstanceOf(ResourceNotFoundException.class);
        }

    }

    @Nested
    @DisplayName("deleteGrowthRecord - 成長記録の削除")
    class DeleteGrowthRecord {
        @Test
        @DisplayName("Growth-05: 成長記録の削除ができること")
        void deleteGrowthRecord_success() {
            // リクエストデータの作成
            Long recordId = 1L;

            // 取得処理のスタブ化
            when(growthRecordRepository.findById(recordId)).thenReturn(Optional.of(new GrowthRecords()));

            // 対象の実行
            growthRecordService.deleteGrowthRecord(recordId);

            // 削除処理が呼ばれていることの確認
            verify(growthRecordRepository).delete(recordId);
        }
    }
}
