package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
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
 * 各種育児記録の処理のロジックを担う
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
                break;
        }
    }

    /**
     * 食事記録を登録する
     *
     * @param request        リクエストデータ
     * @param careRecordData 育児記録データ
     * @param userId         ユーザーID
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
     *
     * @param request        リクエストデータ
     * @param careRecordData 育児記録データ
     * @param userId         ユーザーID
     */
    private void saveMilkRecord(
        MilkDetailDto request, CareRecordRequestDto careRecordData, Long userId) {
        // 親テーブルに保存する
        Long recordId = saveNewRecord(careRecordData, userId);
        // ミルク記録エンティティを作成
        MilkDetails milkDetail = new MilkDetails();
        // 親テーブルIDをセット
        milkDetail.setCareRecordId(recordId);
        // ミルク記録をセット
        milkDetail.setAmountMl(request.getAmountMl());
        // メモ記録をセット
        milkDetail.setNote(request.getNote());
        careRecordRepository.saveMilk(milkDetail);
    }

    /**
     * 排泄記録を登録する
     *
     * @param request        リクエストデータ
     * @param careRecordData 育児記録データ
     * @param userId         ユーザーID
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
     *
     * @param request        リクエストデータ
     * @param careRecordData 育児記録データ
     * @param userId         ユーザーID
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
    @Transactional(readOnly = true)
    public List<CareRecords> getCareRecords(LocalDate startDate, LocalDate endDate) {
        return careRecordRepository.findRecordsByDate(startDate, endDate);
    }

    /**
     * 育児記録テーブルIDに紐づく食事記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 食事記録エンティティリスト
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<HealthDetails> getHealthRecords(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        return careRecordRepository.findHealthRecordsByIds(recordIds);
    }

    /**
     * ID指定で育児記録データを一件取得する
     *
     * @param id 対象のリソースID
     * @return 育児記録エンティティ
     */
    private CareRecords getCareRecord(Long id) {
        return careRecordRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("育児記録が見つかりません " + "id=" + id));
    }

    /**
     * ID指定で食事記録を一件取得する
     *
     * @param id 対象の親テーブルID
     * @return 育児記録エンティティ
     */
    private MealDetails getMealDetail(Long id) {
        return careRecordRepository.findMealRecordsById(id).orElseThrow(() ->
            new ResourceNotFoundException("紐づく食事記録が見つかりません id=" + id));
    }

    /**
     * ID指定でミルク記録を一件取得する
     *
     * @param id 対象の親テーブルID
     * @return 育児記録エンティティ
     */
    private MilkDetails getMilkDetail(Long id) {
        return careRecordRepository.findMilkRecordsById(id).orElseThrow(() ->
            new ResourceNotFoundException("紐づくミルク記録が見つかりません id=" + id));
    }

    /**
     * ID指定で排泄記録を一件取得する
     *
     * @param id 対象の親テーブルID
     * @return 育児記録エンティティ
     */
    private DiaperDetails getDiaperDetail(Long id) {
        return careRecordRepository.findDiaperRecordsById(id).orElseThrow(() ->
            new ResourceNotFoundException("紐づく排泄記録が見つかりません id=" + id));
    }

    /**
     * ID指定で体調記録を一件取得する
     *
     * @param id 対象の親テーブルID
     * @return 育児記録エンティティ
     */
    private HealthDetails getHealthDetail(Long id) {
        return careRecordRepository.findHealthRecordsById(id).orElseThrow(() ->
            new ResourceNotFoundException("紐づく体調記録が見つかりません id=" + id));
    }

    /**
     * 育児記録を更新する
     *
     * @param id         育児記録ID
     * @param updateData 更新データ
     */
    @Transactional
    public void updateCareRecord(Long id, CareRecordRequestDto updateData) {
        // 更新前に存在チェックしつつ親テーブルを取得
        CareRecords careRecord = getCareRecord(id);

        // 記録種別を取得
        String recordType = careRecord.getRecordType();

        // 種別ごとに詳細データを取得しつつ更新を行う
        switch (recordType) {
            case "MEAL":
                // 食事記録を取得
                MealDetails mealDetail = getMealDetail(id);
                // updateDataから更新データを取得し存在する場合更新
                JsonNullable<MealDetailDto> updateMealDetail = updateData.getMealDetail();
                if (updateMealDetail != null && updateMealDetail.isPresent()) {
                    // メモを更新
                    mealDetail.setNote(updateMealDetail.get().getNote());
                    // リポジトリ層でDBに更新を保存
                    careRecordRepository.updateMealDetail(mealDetail);
                }
                break;
            case "MILK":
                // ミルク記録を取得
                MilkDetails milkDetail = getMilkDetail(id);
                // updateDataから更新データを取得し存在する場合更新
                JsonNullable<MilkDetailDto> updateMilkDetail = updateData.getMilkDetail();
                if (updateMilkDetail != null && updateMilkDetail.isPresent()) {
                    // メモを更新
                    milkDetail.setNote(updateMilkDetail.get().getNote());
                    // ミルク量を更新
                    milkDetail.setAmountMl(updateMilkDetail.get().getAmountMl());
                    // リポジトリ層でDBに更新を保存
                    careRecordRepository.updateMilkDetail(milkDetail);
                }
                break;
            case "DIAPER":
                // 排泄記録を取得
                DiaperDetails diaperDetail = getDiaperDetail(id);
                // updateDataから更新データを取得し存在する場合更新
                JsonNullable<DiaperDetailDto> updateDiaperDetail = updateData.getDiaperDetail();
                if (updateDiaperDetail != null && updateDiaperDetail.isPresent()) {
                    // メモを更新
                    diaperDetail.setNote(updateDiaperDetail.get().getNote());
                    // 種別を更新
                    diaperDetail.setDiaperType(updateDiaperDetail.get().getDiaperType().name());
                    // リポジトリ層でDBに更新を保存
                    careRecordRepository.updateDiaperDetail(diaperDetail);
                }
                break;
            case "HEALTH":
                // 体調記録を取得
                HealthDetails healthDetail = getHealthDetail(id);
                // updateDataから更新データを取得し存在する場合更新
                JsonNullable<HealthDetailDto> updateHealthDetail = updateData.getHealthDetail();
                if (updateHealthDetail != null && updateHealthDetail.isPresent()) {
                    // メモを更新
                    healthDetail.setNote(updateHealthDetail.get().getNote());
                    // 体温を更新
                    Double temp = updateHealthDetail.get().getTemperature().orElse(null);
                    healthDetail.setTemperature(temp != null ? BigDecimal.valueOf(temp) : null);
                    // リポジトリ層でDBに更新を保存
                    careRecordRepository.updateHealthDetail(healthDetail);
                }
                break;
            default:
                // 4つのうちどれにも該当しない場合
                throw new IllegalArgumentException("不正な記録種別のため更新できません type=" + careRecord.getRecordType());
        }

        // 更新可能な箇所を書き換え
        careRecord.setRecordedAt(updateData.getRecordedAt());

        // CareRecordsを更新
        careRecordRepository.updateCareRecord(careRecord);
    }

    /**
     * 育児記録を削除する
     *
     * @param id 育児記録ID
     */
    @Transactional
    public void deleteCareRecord(Long id) {
        // 削除前に存在チェックしつつ親テーブルを取得
        CareRecords careRecord = getCareRecord(id);

        // 記録種別を取得
        String recordType = careRecord.getRecordType();

        // 種別ごとに詳細データを確認しつつ削除を行う
        switch (recordType) {
            case "MEAL":
                // 削除前に存在チェックし該当がない場合は例外を投げる
                getMealDetail(id);
                // リポジトリ層でDBから削除
                careRecordRepository.deleteMealByRecordId(id);
                break;
            case "MILK":
                // 削除前に存在チェックし該当がない場合は例外を投げる
                getMilkDetail(id);
                // リポジトリ層でDBから削除
                careRecordRepository.deleteMilkByRecordId(id);
                break;
            case "DIAPER":
                // 削除前に存在チェックし該当がない場合は例外を投げる
                getDiaperDetail(id);
                // リポジトリ層でDBから削除
                careRecordRepository.deleteDiaperByRecordId(id);
                break;
            case "HEALTH":
                // 削除前に存在チェックし該当がない場合は例外を投げる
                getHealthDetail(id);
                // リポジトリ層でDBから削除
                careRecordRepository.deleteHealthByRecordId(id);
                break;
            default:
                // 4つのうちどれにも該当しない場合
                throw new IllegalArgumentException("不正な記録種別のため削除できません type=" + careRecord.getRecordType());
        }

        // CareRecordsを削除
        careRecordRepository.deleteCareRecordById(id);
    }
}
