package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.repository.gen.WordRecordMediaMapper;
import link.s_repo.chii_piyo.repository.gen.WordRecordsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
