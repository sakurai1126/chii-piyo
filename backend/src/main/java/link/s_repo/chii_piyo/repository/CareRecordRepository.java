package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.gen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanOrEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;

@Repository
@RequiredArgsConstructor
public class CareRecordRepository {
    private final CareRecordsMapper careRecordsMapper;
    private final MealDetailsMapper mealDetailsMapper;
    private final MilkDetailsMapper milkDetailsMapper;
    private final DiaperDetailsMapper diaperDetailsMapper;
    private final HealthDetailsMapper healthDetailsMapper;

    /**
     * 育児記録をID指定で1件取得する
     *
     * @param id 対象育児記録のID
     * @return 育児記録データ
     */
    public Optional<CareRecords> findById(Long id) {
        return careRecordsMapper.selectByPrimaryKey(id);
    }

    /**
     * 育児記録をDBに保存する
     *
     * @param careRecord 育児記録エンティティ
     */
    public void save(CareRecords careRecord) {
        careRecordsMapper.insertSelective(careRecord);
    }

    /**
     * 食事記録をDBに保存する
     *
     * @param mealDetail 食事記録エンティティ
     */
    public void saveMeal(MealDetails mealDetail) {
        mealDetailsMapper.insertSelective(mealDetail);
    }

    /**
     * ミルク記録をDBに保存する
     *
     * @param milkDetail ミルク記録エンティティ
     */
    public void saveMilk(MilkDetails milkDetail) {
        milkDetailsMapper.insertSelective(milkDetail);
    }

    /**
     * 排泄記録をDBに保存する
     *
     * @param diaperDetail 排泄記録エンティティ
     */
    public void saveDiaper(DiaperDetails diaperDetail) {
        diaperDetailsMapper.insertSelective(diaperDetail);
    }

    /**
     * 体調記録をDBに保存する
     *
     * @param healthDetail 体調記録エンティティ
     */
    public void saveHealth(HealthDetails healthDetail) {
        healthDetailsMapper.insertSelective(healthDetail);
    }

    /**
     * 記録一覧を取得する
     *
     * @param startDate 検索開始日
     * @param endDate   検索終了日
     * @return 記録の一覧データ
     */
    public List<CareRecords> findRecordsByDate(LocalDate startDate, LocalDate endDate) {
        return careRecordsMapper.select(c -> c.where(
                // 記録時間がstartDateより後のものを絞り込み
                CareRecordsDynamicSqlSupport.recordedAt,
                isGreaterThanOrEqualTo(startDate.atStartOfDay(
                    ZoneId.of("Asia/Tokyo")).toOffsetDateTime())
            ).and(
                // 記録時間がendDateより前のものを絞り込み
                CareRecordsDynamicSqlSupport.recordedAt,
                isLessThan(endDate.plusDays(1).atStartOfDay(
                    ZoneId.of("Asia/Tokyo")).toOffsetDateTime())
            )
        );
    }

    /**
     * 育児記録テーブルIDに紐づく食事記録を一件取得する
     *
     * @param recordId 育児記録テーブルID
     * @return 食事記録エンティティリスト
     */
    public Optional<MealDetails> findMealRecordsById(Long recordId) {
        return mealDetailsMapper.selectOne(c ->
            c.where(MealDetailsDynamicSqlSupport.careRecordId, isEqualTo(recordId)));
    }

    /**
     * 育児記録テーブルIDに紐づくミルク記録を一件取得する
     *
     * @param recordId 育児記録テーブルID
     * @return ミルク記録エンティティリスト
     */
    public Optional<MilkDetails> findMilkRecordsById(Long recordId) {
        return milkDetailsMapper.selectOne(c ->
            c.where(MilkDetailsDynamicSqlSupport.careRecordId, isEqualTo(recordId)));
    }

    /**
     * 育児記録テーブルIDに紐づく排泄記録を一件取得する
     *
     * @param recordId 育児記録テーブルID
     * @return 排泄記録エンティティリスト
     */
    public Optional<DiaperDetails> findDiaperRecordsById(Long recordId) {
        return diaperDetailsMapper.selectOne(c ->
            c.where(DiaperDetailsDynamicSqlSupport.careRecordId, isEqualTo(recordId)));
    }

    /**
     * 育児記録テーブルIDに紐づく体調記録を一件取得する
     *
     * @param recordId 育児記録テーブルID
     * @return 体調記録エンティティリスト
     */
    public Optional<HealthDetails> findHealthRecordsById(Long recordId) {
        return healthDetailsMapper.selectOne(c ->
            c.where(HealthDetailsDynamicSqlSupport.careRecordId, isEqualTo(recordId)));
    }

    /**
     * 育児記録テーブルIDに紐づく食事記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 食事記録エンティティリスト
     */
    public List<MealDetails> findMealRecordsByIds(List<Long> recordIds) {
        return mealDetailsMapper.select(c -> c.where(
            MealDetailsDynamicSqlSupport.careRecordId, isIn(recordIds)
        ));
    }

    /**
     * 育児記録テーブルIDに紐づくミルク記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return ミルク記録エンティティリスト
     */
    public List<MilkDetails> findMilkRecordsByIds(List<Long> recordIds) {
        return milkDetailsMapper.select(c -> c.where(
            MilkDetailsDynamicSqlSupport.careRecordId, isIn(recordIds)
        ));
    }

    /**
     * 育児記録テーブルIDに紐づく排泄記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 排泄記録エンティティリスト
     */
    public List<DiaperDetails> findDiaperRecordsByIds(List<Long> recordIds) {
        return diaperDetailsMapper.select(c -> c.where(
            DiaperDetailsDynamicSqlSupport.careRecordId, isIn(recordIds)
        ));
    }

    /**
     * 育児記録テーブルIDに紐づく体調記録を取得する
     *
     * @param recordIds 育児記録テーブルID
     * @return 体調記録エンティティリスト
     */
    public List<HealthDetails> findHealthRecordsByIds(List<Long> recordIds) {
        return healthDetailsMapper.select(c -> c.where(
            HealthDetailsDynamicSqlSupport.careRecordId, isIn(recordIds)
        ));
    }

    /**
     * 親テーブルID指定で記録を削除する
     *
     * @param id 削除対象の親テーブルID
     */
    public void deleteMealByRecordId(Long id) {
        mealDetailsMapper.delete(c ->
            c.where(MealDetailsDynamicSqlSupport.careRecordId, isEqualTo(id)));
    }

    /**
     * 親テーブルID指定で記録を削除する
     *
     * @param id 削除対象の親テーブルID
     */
    public void deleteMilkByRecordId(Long id) {
        milkDetailsMapper.delete(c ->
            c.where(MilkDetailsDynamicSqlSupport.careRecordId, isEqualTo(id)));
    }

    /**
     * 親テーブルID指定で記録を削除する
     *
     * @param id 削除対象の親テーブルID
     */
    public void deleteDiaperByRecordId(Long id) {
        diaperDetailsMapper.delete(c ->
            c.where(DiaperDetailsDynamicSqlSupport.careRecordId, isEqualTo(id)));
    }

    /**
     * 親テーブルID指定で記録を削除する
     *
     * @param id 削除対象の親テーブルID
     */
    public void deleteHealthByRecordId(Long id) {
        healthDetailsMapper.delete(c ->
            c.where(HealthDetailsDynamicSqlSupport.careRecordId, isEqualTo(id)));
    }

    /**
     * ID指定で記録を削除する
     *
     * @param id 削除対象のID
     */
    public void deleteCareRecordById(Long id) {
        careRecordsMapper.deleteByPrimaryKey(id);
    }

    /**
     * 親テーブルID指定で食事記録を更新する
     *
     * @param mealDetail 更新用データ
     */
    public void updateMealDetail(MealDetails mealDetail) {
        mealDetailsMapper.updateByPrimaryKeySelective(mealDetail);
    }

    /**
     * 親テーブルID指定でミルク記録を更新する
     *
     * @param milkDetail 更新用データ
     */
    public void updateMilkDetail(MilkDetails milkDetail) {
        milkDetailsMapper.updateByPrimaryKeySelective(milkDetail);
    }

    /**
     * 親テーブルID指定で排泄記録を更新する
     *
     * @param diaperDetail 更新用データ
     */
    public void updateDiaperDetail(DiaperDetails diaperDetail) {
        diaperDetailsMapper.updateByPrimaryKeySelective(diaperDetail);
    }

    /**
     * 親テーブルID指定で体調記録を更新する
     *
     * @param healthDetail 更新用データ
     */
    public void updateHealthDetail(HealthDetails healthDetail) {
        healthDetailsMapper.updateByPrimaryKeySelective(healthDetail);
    }

    /**
     * ID指定で記録を更新する
     *
     * @param careRecord 更新用データ
     */
    public void updateCareRecord(CareRecords careRecord) {
        careRecordsMapper.updateByPrimaryKeySelective(careRecord);
    }
}
