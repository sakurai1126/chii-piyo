package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.FirstRecordMedia;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FirstRecordRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * はじめて記録管理サービス<br>
 * はじめて記録の取得・作成およびメディアとのはじめて記録紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirstRecordService {
    private final FirstRecordRepository firstRecordRepository;
    private final MediaRepository mediaRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * はじめて記録を新規作成する<br>
     *
     * @param insertData 追加するはじめて記録のデータ
     */
    @Transactional
    public void createFirstRecord(FirstRecordRequestDto insertData) {
        FirstRecords firstRecord = new FirstRecords();

        // 各種データセット
        firstRecord.setTitle(insertData.getTitle());
        firstRecord.setComment(insertData.getComment());
        firstRecord.setRecordedDate(insertData.getRecordedDate());

        // リポジトリ層で保存
        firstRecordRepository.save(firstRecord);

        // 画像の保存処理
        saveMedia(firstRecord, insertData);
    }

    /**
     * はじめて記録を一覧取得する
     */
    @Transactional(readOnly = true)
    public List<FirstRecordWithMedia> getFirstRecords() {
        // 記録を全件取得
        List<FirstRecords> records = firstRecordRepository.findAll();
        if (records.isEmpty()) {
            return List.of();
        }

        // FirstRecord の ID リストを抽出
        List<Long> recordIds = records.stream()
            .map(FirstRecords::getId)
            .toList();

        // 記録メディア情報を全件取得
        List<FirstRecordMedia> firstRecordMediaList = firstRecordRepository.findMediaByRecordIds(recordIds);

        // メディアのID情報を抽出
        List<Long> mediaIds = firstRecordMediaList.stream()
            .map(FirstRecordMedia::getMediaId)
            .distinct()
            .toList();

        // メディアを一括取得
        List<Media> mediaList = mediaIds.isEmpty()
            ? List.of()
            : mediaRepository.findByIds(mediaIds, currentUserProvider.getUserId());

        // 検索しやすいようIDをキーにしてMapを作成
        Map<Long, Media> mediaMap = mediaList.stream()
            .collect(Collectors.toMap(Media::getId, media -> media));

        // 記録IDをキーにして、紐づくメディアのリストをグループ化
        // 取得したメディア内のデータでフィルターしてゴミ箱内データのID混入を弾く
        Map<Long, List<Media>> mediaByRecordId = firstRecordMediaList.stream()
            .filter(firstRecordMedia -> mediaMap.containsKey(firstRecordMedia.getMediaId()))
            .collect(Collectors.groupingBy(
                FirstRecordMedia::getFirstRecordId,
                Collectors.mapping(
                    firstRecordMedia -> mediaMap.get(firstRecordMedia.getMediaId()),
                    Collectors.toList()
                )
            ));

        // レコード型で必要情報を返却
        return records.stream()
            .map(record -> new FirstRecordWithMedia(
                record,
                mediaByRecordId.getOrDefault(record.getId(), List.of())
            ))
            .toList();
    }

    /**
     * はじめて記録を更新する
     *
     * @param id         対象の記録ID
     * @param updateData 更新データ
     */
    @Transactional
    public void updateFirstRecord(Long id, FirstRecordRequestDto updateData) {
        // 対象データを取得
        FirstRecords firstRecord = getFirstRecord(id);

        // 対象データに紐づくメディアのデータを一旦すべて削除
        firstRecordRepository.deleteMediaByRecordId(id);

        // 各種データセット
        firstRecord.setTitle(updateData.getTitle());
        firstRecord.setComment(updateData.getComment());
        firstRecord.setRecordedDate(updateData.getRecordedDate());
        firstRecord.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // リポジトリ層で更新
        firstRecordRepository.update(firstRecord);

        // 画像の保存処理
        saveMedia(firstRecord, updateData);
    }

    /**
     * はじめて記録を削除する
     *
     * @param id はじめて記録ID
     */
    @Transactional
    public void deleteFirstRecord(Long id) {
        // 削除前に存在チェック(存在しない場合例外)
        getFirstRecord(id);

        // 対象データに紐づくメディアのデータを削除
        firstRecordRepository.deleteMediaByRecordId(id);

        // 対象データを削除
        firstRecordRepository.deleteById(id);
    }

    /**
     * はじめて記録をID指定で1件取得する
     */
    private FirstRecords getFirstRecord(Long id) {
        return firstRecordRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("はじめて記録が見つかりません " + "id=" + id));
    }

    /**
     * 記録に紐づくメディア情報の保存処理
     *
     * @param firstRecord 紐づくはじめて記録のエンティティ
     * @param requestData 画像情報含むリクエストデータ
     */
    private void saveMedia(FirstRecords firstRecord, FirstRecordRequestDto requestData) {
        // パラメータのメディアIDを抽出
        List<Long> mediaIds = requestData.getMediaIds().stream().distinct().toList();
        if (mediaIds.isEmpty()) {
            return;
        }

        // mediaIdsのメディアの存在チェック
        List<Media> mediaList = mediaRepository.findByIds(mediaIds, currentUserProvider.getUserId());

        // 紐づくメディア情報のリストを作る
        if (mediaList.size() != mediaIds.size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaIds);
        }

        // エンティティをリスト化して作成
        List<FirstRecordMedia> firstRecordMediaList = mediaIds.stream()
            .map(mediaId -> {
                FirstRecordMedia firstRecordMedia = new FirstRecordMedia();
                firstRecordMedia.setFirstRecordId(firstRecord.getId());
                firstRecordMedia.setMediaId(mediaId);
                firstRecordMedia.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                return firstRecordMedia;
            }).toList();

        // メディア情報を一括で保存
        firstRecordRepository.saveMedia(firstRecordMediaList);
    }

    /**
     * はじめて記録を返却する際の記録とメディアをまとめたレコード
     */
    public record FirstRecordWithMedia(
        FirstRecords record, List<Media> mediaList) {
    }
}
