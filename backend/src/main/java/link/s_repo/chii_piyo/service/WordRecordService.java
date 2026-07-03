package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.WordRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordRecordService {
    private final WordRecordRepository wordRecordRepository;
    private final MediaRepository mediaRepository;

    /**
     * ことば記録を新規作成する<br>
     *
     * @param insertData 追加することば記録のデータ
     */
    @Transactional
    public void createWordRecord(WordRecordRequestDto insertData) {
        WordRecords wordRecord = new WordRecords();

        // 各種データセット
        wordRecord.setWord(insertData.getWord());
        wordRecord.setComment(insertData.getComment());
        wordRecord.setRecordedDate(insertData.getRecordedDate());

        // リポジトリ層で保存
        wordRecordRepository.save(wordRecord);

        // 画像の保存処理
        saveMedia(wordRecord, insertData);
    }

    /**
     * 記録に紐づくメディア情報の保存処理
     *
     * @param wordRecord 紐づくことばの記録のエンティティ
     * @param requestData 画像情報含むリクエストデータ
     */
    private void saveMedia(WordRecords wordRecord, WordRecordRequestDto requestData) {
        // パラメータのメディアIDを抽出
        List<Long> mediaIds = requestData.getMediaIds().stream().distinct().toList();
        if (mediaIds.isEmpty()) {
            return;
        }

        // mediaIdsのメディアの存在チェック
        List<Media> mediaList = mediaRepository.findByIds(mediaIds);

        // 紐づくメディア情報のリストを作る
        if (mediaList.size() != mediaIds.size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaIds);
        }

        // エンティティをリスト化して作成
        List<WordRecordMedia> wordRecordMediaList = mediaIds.stream()
            .map(mediaId -> {
                WordRecordMedia wordRecordMedia = new WordRecordMedia();
                wordRecordMedia.setWordRecordId(wordRecord.getId());
                wordRecordMedia.setMediaId(mediaId);
                wordRecordMedia.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                return wordRecordMedia;
            }).toList();

        // メディア情報を一括で保存
        wordRecordRepository.saveMedia(wordRecordMediaList);
    }

}
