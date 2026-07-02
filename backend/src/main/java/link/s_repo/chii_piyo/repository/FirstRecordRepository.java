package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.FirstRecordMedia;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.repository.gen.FirstRecordMediaDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FirstRecordMediaMapper;
import link.s_repo.chii_piyo.repository.gen.FirstRecordsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FirstRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FirstRecordRepository {
    private final FirstRecordsMapper firstRecordsMapper;
    private final FirstRecordMediaMapper firstRecordMediaMapper;

    /**
     * はじめて記録をDBに保存
     *
     * @param firstRecord はじめて記録エンティティ
     */
    public void save(FirstRecords firstRecord) {
        firstRecordsMapper.insertSelective(firstRecord);
    }

    /**
     * はじめて記録に紐づくメディアをDBに保存
     *
     * @param firstRecordMediaList はじめて記録メディアエンティティ
     */
    public void saveMedia(List<FirstRecordMedia> firstRecordMediaList) {
        firstRecordMediaMapper.insertMultiple(firstRecordMediaList);
    }

    /**
     * はじめて記録を一覧取得する
     *
     * @return 記録情報リスト
     */
    public List<FirstRecords> findAll() {
        return firstRecordsMapper.select(c -> c.orderBy(FirstRecordsDynamicSqlSupport.achievedDate));
    }

    /**
     * 記録メディア情報を記録IDリストから取得する
     *
     * @param recordIds 記録IDリスト
     * @return 記録メディア情報リスト
     */
    public List<FirstRecordMedia> findMediaByRecordIds(List<Long> recordIds) {
        return firstRecordMediaMapper.select(
            c -> c.where(FirstRecordMediaDynamicSqlSupport.firstRecordId, isIn(recordIds)));
    }
}
