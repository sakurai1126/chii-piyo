package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.CareRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 育児記録管理サービス<br>
 * 各種育児記録のデータ管理を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CareRecordService {
    private final CareRecordRepository careRecordRepository;

    /**
     * 育児記録を登録する
     *
     * @param careRecordData 登録データ
     * @param userId         登録をするユーザーのID
     */
    @Transactional
    public void createCareRecord(CareRecordRequestDto careRecordData, Long userId) {
        switch (careRecordData.getRecordType()) {
            case MEAL:
                // 食事記録の記録
                JsonNullable<MealDetailDto> mealDetail = careRecordData.getMealDetail();
                if (mealDetail != null && mealDetail.isPresent()) {
                    saveMealRecord(mealDetail.get(), careRecordData, userId);
                } else {
                    throw new IllegalArgumentException("食事記録の記録詳細が不足しています");
                }
                break;
            case MILK:
                // ミルク記録の記録
                JsonNullable<MilkDetailDto> milkDetail = careRecordData.getMilkDetail();
                if (milkDetail != null && milkDetail.isPresent()) {
                    saveMilkRecord(milkDetail.get(), careRecordData, userId);
                } else {
                    throw new IllegalArgumentException("ミルク記録の記録詳細が不足しています");
                }
                break;
            case DIAPER:
                // 排泄記録の記録
                JsonNullable<DiaperDetailDto> diaperDetail = careRecordData.getDiaperDetail();
                if (diaperDetail != null && diaperDetail.isPresent()) {
                    saveDiaperRecord(diaperDetail.get(), careRecordData, userId);
                } else {
                    throw new IllegalArgumentException("排泄記録の記録詳細が不足しています");
                }
                break;
            case HEALTH:
                // 体調記録の記録
                JsonNullable<HealthDetailDto> healthDetail = careRecordData.getHealthDetail();
                if (healthDetail != null && healthDetail.isPresent()) {
                    saveHealthRecord(healthDetail.get(), careRecordData, userId);
                } else {
                    throw new IllegalArgumentException("体調記録の記録詳細が不足しています");
                }
                break;
            default:
                throw new IllegalArgumentException("不正な記録種別です");
        }
    }

    /**
     * 食事記録を登録する
     */
    private void saveMealRecord(
        MealDetailDto request, CareRecordRequestDto careRecordData, Long userId) {
        // 食事記録
        // 親テーブルに保存する
        Long recordId = saveNewRecord(careRecordData, userId);
        // 食事記録エンティティを作成
        MealDetails mealDetail = new MealDetails();
        // 親テーブルIDをセット
        mealDetail.setCareRecordId(recordId);
        // メモ記録をセット
        mealDetail.setNote(request.getNote());
        careRecordRepository.saveMeal(mealDetail);
    }

    /**
     * ミルク記録を登録する
     */
    private void saveMilkRecord(
        MilkDetailDto request, CareRecordRequestDto careRecordData, Long userId) {
        // 親テーブルに保存する
        Long recordId = saveNewRecord(careRecordData, userId);
        // ミルク記録エンティティを作成
        MilkDetails milkDetail = new MilkDetails();
        // 親テーブルIDをセット
        milkDetail.setCareRecordId(recordId);
        // ミルク記録がある場合セット
        if (request.getAmountMl().isPresent()) {
            milkDetail.setAmountMl(request.getAmountMl().get());
        }
        // メモ記録をセット
        milkDetail.setNote(request.getNote());
        careRecordRepository.saveMilk(milkDetail);
    }

    /**
     * 排泄記録を登録する
     */
    private void saveDiaperRecord(
        DiaperDetailDto request, CareRecordRequestDto careRecordData, Long userId) {
        // 親テーブルに保存する
        Long recordId = saveNewRecord(careRecordData, userId);
        // 排泄記録エンティティを作成
        DiaperDetails diaperDetail = new DiaperDetails();
        // 親テーブルIDをセット
        diaperDetail.setCareRecordId(recordId);
        // 排泄種別をセット
        diaperDetail.setDiaperType(request.getDiaperType().name());
        // メモ記録をセット
        diaperDetail.setNote(request.getNote());
        careRecordRepository.saveDiaper(diaperDetail);
    }

    /**
     * 体調記録を登録する
     */
    private void saveHealthRecord(
        HealthDetailDto request, CareRecordRequestDto careRecordData, Long userId) {
        // 親テーブルに保存する
        Long recordId = saveNewRecord(careRecordData, userId);
        // 体調記録エンティティを作成
        HealthDetails healthDetail = new HealthDetails();
        // 親テーブルIDをセット
        healthDetail.setCareRecordId(recordId);
        // 体温を記録
        if (request.getTemperature().isPresent()) {
            healthDetail.setTemperature(BigDecimal.valueOf(request.getTemperature().get()));
        }
        // メモ記録をセット
        healthDetail.setNote(request.getNote());
        careRecordRepository.saveHealth(healthDetail);
    }

    /**
     * CareRecordsを新規登録してIDを返却する
     *
     * @param careRecordData 登録する育児記録データ
     * @param userId         登録をするユーザーのID
     * @return 登録して生成されたデータID
     */
    private Long saveNewRecord(CareRecordRequestDto careRecordData, Long userId) {
        CareRecords careRecord = new CareRecords();
        careRecord.setRecordType(careRecordData.getRecordType().name());
        careRecord.setRecordedAt(careRecordData.getRecordedAt());
        careRecord.setRecordedBy(userId);
        careRecordRepository.save(careRecord);
        return careRecord.getId();
    }

    /**
     * 育児記録一覧を取得する
     *
     * @param startDate 検索開始日
     * @param endDate   検索終了日
     * @return 記録の一覧データ
     */
    public List<CareRecords> getCareRecords(LocalDate startDate, LocalDate endDate) {
        return careRecordRepository.findRecordsByDate(startDate, endDate);
    }

    /**
     * 育児記録テーブルIDに紐づく食事記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 食事記録エンティティリスト
     */
    public List<MealDetails> getMealRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return careRecordRepository.findMealRecordsByIds(recordIds);
    }

    /**
     * 育児記録テーブルIDに紐づくミルク記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return ミルク記録エンティティリスト
     */
    public List<MilkDetails> getMilkRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return careRecordRepository.findMilkRecordsByIds(recordIds);
    }

    /**
     * 育児記録テーブルIDに紐づく排泄記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 排泄記録エンティティリスト
     */
    public List<DiaperDetails> getDiaperRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return careRecordRepository.findDiaperRecordsByIds(recordIds);
    }

    /**
     * 育児記録テーブルIDに紐づく体調記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 体調記録エンティティリスト
     */
    public List<HealthDetails> getHealthRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return careRecordRepository.findHealthRecordsByIds(recordIds);
    }
}
