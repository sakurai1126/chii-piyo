package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.CareRecords;
import link.s_repo.chii_piyo.model.gen.DiaperDetails;
import link.s_repo.chii_piyo.model.gen.HealthDetails;
import link.s_repo.chii_piyo.model.gen.MealDetails;
import link.s_repo.chii_piyo.model.gen.MilkDetails;
import link.s_repo.chii_piyo.repository.gen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
}
