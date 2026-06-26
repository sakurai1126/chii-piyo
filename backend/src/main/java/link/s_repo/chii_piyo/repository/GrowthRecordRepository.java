package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.repository.gen.GrowthRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GrowthRecordRepository {
    private final GrowthRecordsMapper growthRecordsMapper;

    /**
     * 成長記録データを新規作成する
     *
     * @param growthRecord 成長記録データエンティティ
     */
    public void save(GrowthRecords growthRecord) {
        growthRecordsMapper.insertSelective(growthRecord);
    }
}
