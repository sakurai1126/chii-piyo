package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.repository.gen.WordRecordMediaDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.WordRecordMediaMapper;
import link.s_repo.chii_piyo.repository.gen.WordRecordsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.WordRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Repository
@RequiredArgsConstructor
public class WordRecordRepository {

    private final WordRecordsMapper wordRecordsMapper;
    private final WordRecordMediaMapper wordRecordMediaMapper;

    /**
     * ことばの記録をDBに保存
     *
     * @param wordRecord ことばの記録エンティティ
     */
    public void save(WordRecords wordRecord) {
        wordRecordsMapper.insertSelective(wordRecord);
    }

    /**
     * ことばの記録に紐づくメディアをDBに保存
     *
     * @param wordRecordMediaList ことばの記録メディアエンティティ
     */
    public void saveMedia(List<WordRecordMedia> wordRecordMediaList) {
        wordRecordMediaMapper.insertMultiple(wordRecordMediaList);
    }

    /**
     * ことばの記録を一覧取得する
     *
     * @return 記録情報リスト
     */
    public List<WordRecords> findAll() {
        return wordRecordsMapper.select(
            c -> c.orderBy(WordRecordsDynamicSqlSupport.recordedDate.descending()));
    }

    /**
     * 記録メディア情報を記録IDリストから取得する
     *
     * @param recordIds 記録IDリスト
     * @return 記録メディア情報リスト
     */
    public List<WordRecordMedia> findMediaByRecordIds(List<Long> recordIds) {
        return wordRecordMediaMapper.select(
            c -> c.where(WordRecordMediaDynamicSqlSupport.wordRecordId, isIn(recordIds)));
    }

    /**
     * ことばの記録を取得する
     *
     * @param id 対象ことばの記録のID
     * @return 記録情報
     */
    public Optional<WordRecords> findById(Long id) {
        return wordRecordsMapper.selectByPrimaryKey(id);
    }

    /**
     * ことばの記録を更新する
     *
     * @param wordRecord ことばの記録エンティティ
     */
    public void update(WordRecords wordRecord) {
        wordRecordsMapper.updateByPrimaryKey(wordRecord);
    }

    /**
     * ことばの記録を削除する
     *
     * @param id 対象ことばの記録のID
     */
    public void deleteById(Long id) {
        wordRecordsMapper.deleteByPrimaryKey(id);
    }

    /**
     * ことばの記録のメディア情報を削除する
     *
     * @param recordId 対象ことばの記録のID
     */
    public void deleteMediaByRecordId(Long recordId) {
        wordRecordMediaMapper.delete(
            c -> c.where(WordRecordMediaDynamicSqlSupport.wordRecordId, isEqualTo(recordId)));
    }

    /**
     * ことばの記録のメディア情報をメディアIDから削除する
     *
     * @param mediaId 対象メディアのID
     */
    public void deleteMediaByMediaId(Long mediaId) {
        wordRecordMediaMapper.delete(
            c -> c.where(WordRecordMediaDynamicSqlSupport.mediaId, isEqualTo(mediaId)));
    }

    /**
     * ことばの記録のメディア情報をメディアIDリストから削除する
     *
     * @param mediaIds 対象メディアのIDリスト
     */
    public void deleteMediaByMediaIds(List<Long> mediaIds) {
        wordRecordMediaMapper.delete(
            c -> c.where(WordRecordMediaDynamicSqlSupport.mediaId, isIn(mediaIds)));
    }
}
