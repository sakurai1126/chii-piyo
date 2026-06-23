package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.gen.TrashItemsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.TrashItemsMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.dynamic.sql.dsl.CountDSLCompleter;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualTo;

@Repository
@RequiredArgsConstructor
public class TrashRepository {
    private final TrashItemsMapper trashItemsMapper;

    /**
     * ゴミ箱データをID指定で1件取得する
     *
     * @param id 対象のゴミ箱データのID
     * @return ゴミ箱データエンティティ
     */
    public Optional<TrashItems> findById(Long id) {
        return trashItemsMapper.selectByPrimaryKey(id);
    }

    /**
     * ゴミ箱データをIDリスト指定で複数件取得する
     *
     * @param ids 対象のゴミ箱データのIDリスト
     * @return ゴミ箱データエンティティリスト
     */
    public List<TrashItems> findByIds(List<Long> ids) {
        return trashItemsMapper.select(c -> c.where(TrashItemsDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * ゴミ箱データを全件取得する
     *
     * @return ゴミ箱データエンティティリスト
     */
    public List<TrashItems> findAll() {
        return trashItemsMapper.select(SelectDSLCompleter.allRows());
    }

    /**
     * ゴミ箱データを新規作成する
     *
     * @param trashItem ゴミ箱データエンティティ
     */
    public void save(TrashItems trashItem) {
        trashItemsMapper.insertSelective(trashItem);
    }

    /**
     * ゴミ箱データを一括新規作成する
     *
     * @param trashItems ゴミ箱データエンティティリスト
     */
    public void saveAll(List<TrashItems> trashItems) {
        trashItemsMapper.insertMultiple(trashItems);
    }

    /**
     * ゴミ箱内のアイテム一覧を取得
     *
     * @param offset 取得開始位置
     * @param limit  取得件数
     * @return ゴミ箱内のアイテム一覧
     */
    public List<TrashItems> findAll(Integer offset, Integer limit) {
        return trashItemsMapper.select(c -> c
            .orderBy(TrashItemsDynamicSqlSupport.expiresAt)
            .limit(limit)
            .offset(offset)
        );
    }


    /**
     * ゴミ箱データを削除する
     *
     * @param id 対象のゴミ箱データID
     */
    public void delete(Long id) {
        trashItemsMapper.deleteByPrimaryKey(id);
    }

    /**
     * 複数ゴミ箱データを削除する
     *
     * @param ids 対象のゴミ箱データIDリスト
     */
    public void delete(List<Long> ids) {
        trashItemsMapper.delete(c -> c.where(TrashItemsDynamicSqlSupport.id, isIn(ids)));
    }


    /**
     * ゴミ箱内のアイテムの総件数を取得
     *
     * @return ゴミ箱内のアイテム総件数
     */
    public Long count() {
        return trashItemsMapper.count(CountDSLCompleter.allRows());
    }

    /**
     * 最も古いアイテム（削除が一番近いもの）を1件取得する
     *
     * @return 対象のゴミ箱データ
     */
    public Optional<TrashItems> findOldest() {
        return trashItemsMapper.selectOne(
            c -> c.orderBy(TrashItemsDynamicSqlSupport.expiresAt).limit(1)
        );
    }

    /**
     * 期限切れのアイテムを取得する
     *
     * @param now 現在時刻
     * @return 対象のゴミ箱データリスト
     */
    public List<TrashItems> findExpiredItems(OffsetDateTime now) {
        return trashItemsMapper.select(c ->
            c.where(TrashItemsDynamicSqlSupport.expiresAt, isLessThanOrEqualTo(now))
        );
    }
}
