package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.CareRecords;
import link.s_repo.chii_piyo.model.gen.DiaperDetails;
import link.s_repo.chii_piyo.model.gen.HealthDetails;
import link.s_repo.chii_piyo.model.gen.MealDetails;
import link.s_repo.chii_piyo.model.gen.MilkDetails;
import link.s_repo.chii_piyo.repository.gen.CareRecordsMapper;
import link.s_repo.chii_piyo.repository.gen.DiaperDetailsMapper;
import link.s_repo.chii_piyo.repository.gen.HealthDetailsMapper;
import link.s_repo.chii_piyo.repository.gen.MealDetailsMapper;
import link.s_repo.chii_piyo.repository.gen.MilkDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
