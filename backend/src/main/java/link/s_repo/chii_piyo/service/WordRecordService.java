package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.WordRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        wordRecord.setTitle(insertData.getTitle());
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
     * @param wordRecord  紐づくことばの記録のエンティティ
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

    /**
     * ことばの記録を一覧取得する
     */
    @Transactional(readOnly = true)
    public List<WordRecordWithMedia> getWordRecords() {
        // 記録を全件取得
        List<WordRecords> records = wordRecordRepository.findAll();
        if (records.isEmpty()) {
            return List.of();
        }

        // WordRecord の ID リストを抽出
        List<Long> recordIds = records.stream()
            .map(WordRecords::getId)
            .toList();

        // 記録メディア情報を全件取得
        List<WordRecordMedia> wordRecordMediaList =
            wordRecordRepository.findMediaByRecordIds(recordIds);

        // メディアのID情報を抽出
        List<Long> mediaIds = wordRecordMediaList.stream()
            .map(WordRecordMedia::getMediaId)
            .distinct()
            .toList();

        // メディアを一括取得
        List<Media> mediaList = mediaIds.isEmpty() ? List.of() : mediaRepository.findByIds(mediaIds);

        // 検索しやすいようIDをキーにしてMapを作成
        Map<Long, Media> mediaMap = mediaList.stream()
            .collect(Collectors.toMap(Media::getId, media -> media));

        // 記録IDをキーにして、紐づくメディアのリストをグループ化
        Map<Long, List<Media>> mediaByRecordId = wordRecordMediaList.stream()
            .collect(Collectors.groupingBy(
                WordRecordMedia::getWordRecordId,
                Collectors.mapping(
                    wordRecordMedia -> mediaMap.get(wordRecordMedia.getMediaId()),
                    Collectors.toList()
                )
            ));

        // レコード型で必要情報を返却
        return records.stream()
            .map(record -> new WordRecordService.WordRecordWithMedia(
                record,
                mediaByRecordId.getOrDefault(record.getId(), List.of())
            ))
            .toList();
    }

    /**
     * ことばの記録を更新する
     *
     * @param id         対象の記録ID
     * @param updateData 更新データ
     */
    @Transactional
    public void updateWordRecord(Long id, WordRecordRequestDto updateData) {
        // 対象データを取得
        WordRecords wordRecord = getWordRecord(id);

        // 対象データに紐づくメディアのデータを一旦すべて削除
        wordRecordRepository.deleteMediaByRecordId(id);

        // 各種データセット
        wordRecord.setTitle(updateData.getTitle());
        wordRecord.setComment(updateData.getComment());
        wordRecord.setRecordedDate(updateData.getRecordedDate());
        wordRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // リポジトリ層で更新
        wordRecordRepository.update(wordRecord);

        // 画像の保存処理
        saveMedia(wordRecord, updateData);
    }

    /**
     * ことばの記録を削除する
     *
     * @param id ことばの記録ID
     */
    @Transactional
    public void deleteWordRecord(Long id) {
        // 削除前に存在チェック(存在しない場合例外)
        getWordRecord(id);

        // 対象データに紐づくメディアのデータを削除
        wordRecordRepository.deleteMediaByRecordId(id);

        // 対象データを削除
        wordRecordRepository.deleteById(id);
    }

    /**
     * ことばの記録をID指定で1件取得する
     */
    private WordRecords getWordRecord(Long id) {
        return wordRecordRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("ことばの記録が見つかりません " + "id=" + id));
    }

    /**
     * ことばの記録を返却する際の記録とメディアをまとめたレコード
     */
    public record WordRecordWithMedia(
        WordRecords record, List<Media> mediaList) {
    }
}
