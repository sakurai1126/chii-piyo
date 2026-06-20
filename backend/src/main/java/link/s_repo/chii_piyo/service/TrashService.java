package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.gen.TrashItemsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.TrashItemsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * ゴミ箱管理サービス<br>
 * メディアのゴミ箱データの作成・削除を行う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrashService {
    private final TrashItemsMapper trashItemsMapper;

    /**
     * IDを受け取りゴミ箱データを作成する
     *
     * @param id 対象メディアのID
     */
    public void createTrashItem(Long id) {
        TrashItems trashItem = new TrashItems();
        trashItem.setMediaId(id);
        // 削除予定日時に日本時間で30日後のAM2:00を指定
        trashItem.setExpiresAt(
            OffsetDateTime.now(ZoneId.of("Asia/Tokyo"))
                .plusDays(30)
                .with(LocalTime.of(2, 0))
        );
        trashItem.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        trashItemsMapper.insertSelective(trashItem);
    }

    /**
     * IDリストを受け取りゴミ箱データを作成する
     *
     * @param mediaIds 対象メディアのIDリスト
     */
    @Transactional
    public void createTrashItems(List<Long> mediaIds) {
        List<TrashItems> trashItems = mediaIds.stream().map(mediaId -> {
            TrashItems trashItem = new TrashItems();
            trashItem.setMediaId(mediaId);
            // 削除予定日時に日本時間で30日後のAM2:00を指定
            trashItem.setExpiresAt(
                OffsetDateTime.now(ZoneId.of("Asia/Tokyo"))
                    .plusDays(30)
                    .with(LocalTime.of(2, 0))
            );
            trashItem.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            return trashItem;
        }).toList();
        trashItemsMapper.insertMultiple(trashItems);
    }

    /**
     * ゴミ箱内のアイテム一覧を取得
     *
     * @param offset 取得開始位置
     * @param limit  取得件数
     * @return ゴミ箱内のアイテム一覧
     */
    public List<TrashItems> getTrashItems(Integer offset, Integer limit) {
        return trashItemsMapper.select(c -> c
            .orderBy(TrashItemsDynamicSqlSupport.expiresAt)
            .limit(limit)
            .offset(offset)
        );
    }

    /**
     * ゴミ箱内のアイテムの総件数を取得
     *
     * @return ゴミ箱内のアイテム総件数
     */
    public Long getTotalCount() {
        return trashItemsMapper.count(CountDSLCompleter.allRows());
    }

    /**
     * 完全削除まで最も近いメディアの残り日数を取得する
     *
     * @return 日数の数値
     */
    public Long getEarliestDeadline() {
        // expiresAt順で1件取得
        Optional<TrashItems> earliestItem = trashItemsMapper.selectOne(
            c -> c.orderBy(TrashItemsDynamicSqlSupport.expiresAt).limit(1)
        );

        if (earliestItem.isPresent()) {
            TrashItems item = earliestItem.get();
            // 今日の日付の取得
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));

            // 最も近い削除日時を取得
            LocalDate expireDate = item.getExpiresAt().toLocalDate();

            // 残り日数を計算して返却
            return ChronoUnit.DAYS.between(today, expireDate);
        } else {
            return null;
        }
    }
}
