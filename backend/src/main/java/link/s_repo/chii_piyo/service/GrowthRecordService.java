package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.GrowthRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.repository.GrowthRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 成長記録管理サービス<br>
 * 成長記録の処理のロジックを担う
 */
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

    /**
     * 成長記録一覧を取得する
     *
     * @param startDate 検索開始日
     * @param endDate   検索終了日
     * @return 身長・体重記録エンティティ一覧
     */
    @Transactional(readOnly = true)
    public List<GrowthRecords> getGrowthRecords(LocalDate startDate, LocalDate endDate) {
        return growthRecordRepository.findRecordsByDate(startDate, endDate);
    }

    /**
     * ID指定で成長記録データを一件取得する
     *
     * @param id 対象のリソースID
     * @return 成長記録エンティティ
     */
    private GrowthRecords getGrowthRecord(Long id) {
        return growthRecordRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("成長記録が見つかりません " + "id=" + id));
    }

    /**
     * 成長記録を更新する
     *
     * @param id         成長記録ID
     * @param updateData 更新用データ
     */
    @Transactional
    public void updateGrowthRecord(Long id, GrowthRecordRequestDto updateData) {
        GrowthRecords growthRecord = getGrowthRecord(id);
        growthRecord.setMeasurementDate(updateData.getMeasurementDate());

        // 身長の更新 ※未入力はnullに更新
        Double height = (updateData.getHeight() != null && updateData.getHeight().isPresent())
            ? updateData.getHeight().get() : null;
        growthRecord.setHeight(height != null ? BigDecimal.valueOf(height) : null);

        // 体重の更新 ※未入力はnullに更新
        Double weight = (updateData.getWeight() != null && updateData.getWeight().isPresent())
            ? updateData.getWeight().get() : null;
        growthRecord.setWeight(weight != null ? BigDecimal.valueOf(weight) : null);

        growthRecord.setNote(updateData.getNote());
        growthRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // リポジトリ層で更新
        growthRecordRepository.updateGrowthRecord(growthRecord);
    }

    /**
     * 成長記録を削除する
     *
     * @param id 成長記録ID
     */
    @Transactional
    public void deleteGrowthRecord(Long id) {
        // 削除前に存在チェックしつつしない場合例外を投げる
        getGrowthRecord(id);

        // リポジトリ層で削除する
        growthRecordRepository.delete(id);
    }
}
