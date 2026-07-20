package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.FirstRecordMedia;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.repository.gen.FirstRecordMediaDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FirstRecordMediaMapper;
import link.s_repo.chii_piyo.repository.gen.FirstRecordsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.FirstRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * はじめて記録関連のリポジトリ<br>
 * はじめて記録に関するDB操作を提供
 */
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
        firstRecord.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        firstRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        firstRecordsMapper.insertSelective(firstRecord);
    }

    /**
     * はじめて記録に紐づくメディアをDBに保存
     *
     * @param firstRecordMediaList はじめて記録メディアエンティティ
     */
    public void saveMedia(List<FirstRecordMedia> firstRecordMediaList) {
        firstRecordMediaList.forEach(item -> item.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC)));
        firstRecordMediaMapper.insertMultiple(firstRecordMediaList);
    }

    /**
     * はじめて記録を一覧取得する
     *
     * @return 記録情報リスト
     */
    public List<FirstRecords> findAll() {
        return firstRecordsMapper.select(c -> c.orderBy(FirstRecordsDynamicSqlSupport.recordedDate.descending()));
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

    /**
     * はじめて記録を取得する
     *
     * @param id 対象はじめて記録のID
     * @return 記録情報
     */
    public Optional<FirstRecords> findById(Long id) {
        return firstRecordsMapper.selectByPrimaryKey(id);
    }

    /**
     * はじめて記録を更新する
     *
     * @param firstRecord はじめて記録エンティティ
     */
    public void update(FirstRecords firstRecord) {
        firstRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        firstRecordsMapper.updateByPrimaryKey(firstRecord);
    }

    /**
     * はじめて記録を削除する
     *
     * @param id 対象はじめて記録のID
     */
    public void deleteById(Long id) {
        firstRecordsMapper.deleteByPrimaryKey(id);
    }

    /**
     * はじめて記録のメディア情報を削除する
     *
     * @param recordId 対象はじめて記録のID
     */
    public void deleteMediaByRecordId(Long recordId) {
        firstRecordMediaMapper.delete(
            c -> c.where(FirstRecordMediaDynamicSqlSupport.firstRecordId, isEqualTo(recordId)));
    }

    /**
     * はじめて記録のメディア情報をメディアIDから削除する
     *
     * @param mediaId 対象メディアのID
     */
    public void deleteMediaByMediaId(Long mediaId) {
        firstRecordMediaMapper.delete(
            c -> c.where(FirstRecordMediaDynamicSqlSupport.mediaId, isEqualTo(mediaId)));
    }

    /**
     * はじめて記録のメディア情報をメディアIDリストから削除する
     *
     * @param mediaIds 対象メディアのIDリスト
     */
    public void deleteMediaByMediaIds(List<Long> mediaIds) {
        firstRecordMediaMapper.delete(
            c -> c.where(FirstRecordMediaDynamicSqlSupport.mediaId, isIn(mediaIds)));
    }
}
