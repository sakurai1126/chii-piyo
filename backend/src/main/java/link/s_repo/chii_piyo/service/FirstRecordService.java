package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.FirstRecordMedia;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FirstRecordRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
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
        firstRecord.setAchievedDate(insertData.getAchievedDate());

        // リポジトリ層で保存
        firstRecordRepository.save(firstRecord);

        // パラメータのメディアIDを抽出
        List<Long> mediaIds = insertData.getMediaIds().stream().distinct().toList();
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
     * はじめて記録を一覧取得する
     */
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
        List<Media> mediaList = mediaIds.isEmpty() ? List.of() : mediaRepository.findByIds(mediaIds);

        // 検索しやすいようIDをキーにしてMapを作成
        Map<Long, Media> mediaMap = mediaList.stream()
            .collect(Collectors.toMap(Media::getId, media -> media));

        // 記録IDをキーにして、紐づくメディアのリストをグループ化
        Map<Long, List<Media>> mediaByRecordId = firstRecordMediaList.stream()
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
     * はじめて記録を返却する際の記録とメディアをまとめたレコード
     */
    public record FirstRecordWithMedia(
        FirstRecords record, List<Media> mediaList) {
    }
}
