package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.TagMediaCount;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaTagsMapper;
import link.s_repo.chii_piyo.repository.gen.TagsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.TagsMapper;
import link.s_repo.chii_piyo.repository.mapper.MediaTagsCustomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static link.s_repo.chii_piyo.repository.gen.TagsDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

/**
 * タグ関連のリポジトリ<br>
 * タグに関するDB操作を提供
 */
@Repository
@RequiredArgsConstructor
public class TagRepository {
    private final TagsMapper tagsMapper;
    private final MediaTagsMapper mediaTagsMapper;
    private final MediaTagsCustomMapper mediaTagsCustomMapper;

    /**
     * タグをID指定で1件取得する
     *
     * @param id 対象のタグのID
     * @return タグデータ
     */
    public Optional<Tags> findById(Long id) {
        return tagsMapper.selectByPrimaryKey(id);
    }

    /**
     * タグ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return タグエンティティの一覧
     */
    public List<Tags> findAllOrderById() {
        return tagsMapper.select(c -> c.orderBy(id));
    }

    /**
     * タグをIDリスト指定で取得する
     *
     * @param ids 対象のタグIDリスト
     * @return タグエンティティリスト
     */
    public List<Tags> findByIds(List<Long> ids) {
        return tagsMapper.select(c -> c.where(TagsDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * タグを新規作成する
     *
     * @param tag タグエンティティ
     */
    public void save(Tags tag) {
        tag.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        tagsMapper.insertSelective(tag);
    }

    /**
     * タグを更新する
     *
     * @param tag タグエンティティ
     */
    public void update(Tags tag) {
        tagsMapper.updateByPrimaryKeySelective(tag);
    }

    /**
     * タグを削除する
     *
     * @param id 対象のタグID
     */
    public void delete(Long id) {
        tagsMapper.deleteByPrimaryKey(id);
    }

    /**
     * タグIDのリストに一致するタグの件数を取得
     *
     * @param ids タグIDのリスト
     * @return タグIDのリストに一致するタグの件数
     */
    public Long countByTagIds(List<Long> ids) {
        return tagsMapper.count(c -> c.where(TagsDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * メディアへのタグ登録データ一覧を取得する
     *
     * @param id メディアID
     */
    public List<MediaTags> findMediaTagsByMediaId(Long id) {
        return mediaTagsMapper.select(
            c -> c.where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(id))
        );
    }

    /**
     * メディアへのタグ登録データを一括保存する
     *
     * @param mediaTags メディアへのタグ登録データリスト
     */
    public void saveMediaTags(List<MediaTags> mediaTags) {
        mediaTags.forEach(item -> item.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC)));
        mediaTagsMapper.insertMultiple(mediaTags);
    }

    /**
     * メディアIDとタグのIDに紐づくメディアタグ登録データを削除する
     *
     * @param mediaId 対象のメディアID
     * @param tagIds  タグIDリスト
     */
    public void deleteMediaTagsByMediaIdAndTagIds(Long mediaId, List<Long> tagIds) {
        mediaTagsMapper.delete(c -> c
            .where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(mediaId))
            .and(MediaTagsDynamicSqlSupport.tagId, isIn(tagIds))
        );
    }

    /**
     * タグのIDに紐づくメディアタグ登録データを削除する
     *
     * @param id 対象のタグID
     */
    public void deleteMediaTagsByTagId(Long id) {
        mediaTagsMapper.delete(c -> c.where(MediaTagsDynamicSqlSupport.tagId, isEqualTo(id)));
    }

    /**
     * メディアのIDに紐づくメディアタグ登録データを削除する
     *
     * @param id 対象のメディアID
     */
    public void deleteMediaTagsByMediaId(Long id) {
        mediaTagsMapper.delete(c -> c.where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(id)));
    }

    /**
     * メディアのIDリストに紐づくメディアタグ登録データを削除する
     *
     * @param ids 対象のメディアIDリスト
     */
    public void deleteMediaTagsByMediaIds(List<Long> ids) {
        mediaTagsMapper.delete(c -> c.where(MediaTagsDynamicSqlSupport.mediaId, isIn(ids)));
    }

    /**
     * タグIDごとのメディア数を返す<br>
     * 自動生成コードでは全件取得してから計算し高負荷となるためカスタムマッパーを使用してDB側で集計
     *
     * @return タグIDとそのタグが紐付いているメディア数
     */
    public List<TagMediaCount> selectMediaCountByTagId() {
        return mediaTagsCustomMapper.selectMediaCountByTagId();
    }
}

