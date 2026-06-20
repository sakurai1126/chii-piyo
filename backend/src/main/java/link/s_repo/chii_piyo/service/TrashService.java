package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.gen.TrashItemsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

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


}
