package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.GrowthRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.repository.GrowthRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrowthRecordService {
    private final GrowthRecordRepository growthRecordRepository;

    /**
     * 成長記録を登録する
     *
     * @param growthRecordData 登録データ
     */
    @Transactional
    public void createGrowthRecord(GrowthRecordRequestDto growthRecordData) {
        GrowthRecords record = new GrowthRecords();
        // 測定日、身長、体重をセット
        record.setMeasurementDate(growthRecordData.getMeasurementDate());

        // 体重記録がある場合セット
        JsonNullable<Double> weight = growthRecordData.getWeight();
        if (weight != null && weight.isPresent()) {
            record.setWeight(BigDecimal.valueOf(weight.get()));
        }

        // 身長記録がある場合セット
        JsonNullable<Double> height = growthRecordData.getHeight();
        if (height != null && height.isPresent()) {
            record.setHeight(BigDecimal.valueOf(height.get()));
        }

        // メモ記録がある場合セット
        record.setNote(growthRecordData.getNote());

        growthRecordRepository.save(record);
    }
}
