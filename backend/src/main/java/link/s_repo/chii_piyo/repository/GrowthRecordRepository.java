package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.GrowthRecords;
import link.s_repo.chii_piyo.repository.gen.GrowthRecordsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.GrowthRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanOrEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;

/**
 * 成長記録関連のリポジトリ<br>
 * 成長記録に関するDB操作を提供
 */
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
        growthRecord.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        growthRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        growthRecordsMapper.insertSelective(growthRecord);
    }

    /**
     * 成長記録一覧を取得する
     *
     * @param startDate 検索開始日
     * @param endDate   検索終了日
     * @return 身長・体重記録エンティティ一覧
     */
    public List<GrowthRecords> findRecordsByDate(LocalDate startDate, LocalDate endDate) {
        return growthRecordsMapper.select(c -> c.where(
                // 記録日がstartDateより後のものを絞り込み
                GrowthRecordsDynamicSqlSupport.measurementDate,
                isGreaterThanOrEqualTo(startDate)
            ).and(
                // 記録日がendDateより前のものを絞り込み
                GrowthRecordsDynamicSqlSupport.measurementDate,
                isLessThan(endDate.plusDays(1))
            ).orderBy(GrowthRecordsDynamicSqlSupport.measurementDate.descending())
        );
    }

    /**
     * IDに紐づく成長記録を一件取得する
     *
     * @param id 対象のID
     * @return 成長記録エンティティ
     */
    public Optional<GrowthRecords> findById(Long id) {
        return growthRecordsMapper.selectByPrimaryKey(id);
    }

    /**
     * ID指定で成長記録を削除する
     *
     * @param id 削除対象のID
     */
    public void delete(Long id) {
        growthRecordsMapper.deleteByPrimaryKey(id);
    }

    /**
     * ID指定で記録を更新する
     *
     * @param growthRecord 更新用データ
     */
    public void updateGrowthRecord(GrowthRecords growthRecord) {
        growthRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        growthRecordsMapper.updateByPrimaryKey(growthRecord);
    }
}
