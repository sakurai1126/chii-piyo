package link.s_repo.chii_piyo.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.TagMediaCount;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;

import link.s_repo.chii_piyo.repository.MediaTagsCustomMapper;
import link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaTagsMapper;
import link.s_repo.chii_piyo.repository.gen.TagsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.TagsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static link.s_repo.chii_piyo.repository.gen.TagsDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;


/**
 * タグ管理サービス<br>
 * タグの取得・作成およびメディアとのタグ紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagsMapper tagsMapper;
    private final MediaTagsMapper mediaTagsMapper;
    private final MediaTagsCustomMapper mediaTagsCustomMapper;

    /**
     * タグを新規作成する<br>
     *
     * @param name 追加するタグ名
     * @return 作成されたタグエンティティ
     */
    @Transactional
    public Tags createTag(String name) {
        Tags tags = new Tags();

        // タグエンティティに値をセット
        tags.setName(name);
        tags.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // タグをDBに保存
        tagsMapper.insert(tags);
        return tags;
    }

    /**
     * タグ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return タグエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<Tags> getTags() {
        return tagsMapper.select(c -> c.orderBy(id));
    }

    /**
     * メディアに紐づくタグ一覧を取得する。
     *
     * @param mediaId メディアID
     * @return タグのリスト
     */
    @Transactional(readOnly = true)
    public List<Tags> getMediaTags(Long mediaId) {
        // メディアIDと紐づいたタグIDの一覧を取得する
        List<Long> mediaTagIds = mediaTagsMapper.select(
                // "WHERE media_id = #{mediaId}"
                c -> c.where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(mediaId))
            ).stream()
            // MediaTagsエンティティからタグIDだけ抜き取りリスト化
            .map(MediaTags::getTagId)
            .toList();

        // タグが空の場合は空リストを渡す
        if (mediaTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return tagsMapper.select(c -> c.where(id, isIn(mediaTagIds)));
    }


    /**
     * メディアに紐づくタグを一括更新する<br>
     *
     * @param mediaId メディアID
     * @param tagIds  タグIDのリスト
     * @return 更新後のタグエンティティのリスト
     */
    @Transactional
    public List<Tags> syncMediaTags(Long mediaId, List<Long> tagIds) {
        // メディアIDと紐づいたタグIDの一覧を取得する
        List<Long> currentTagIds = mediaTagsMapper.select(
                // "WHERE media_id = #{mediaId}"
                c -> c.where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(mediaId))
            ).stream()
            // MediaTagsエンティティからタグIDだけ抜き取りリスト化
            .map(MediaTags::getTagId)
            .toList();

        // 既存タグには存在するが新規タグには存在しないタグIDを抽出
        List<Long> toDeleteTagIds = currentTagIds.stream()
            .filter(tagId -> !tagIds.contains(tagId))
            .toList();

        // 上記のIDのタグを一括で削除する
        if (!toDeleteTagIds.isEmpty()) {
            mediaTagsMapper.delete(c ->
                // DELETE FROM media_tags WHERE media_id = #{mediaId} AND tag_id IN (?, ?, ...)
                c.where(MediaTagsDynamicSqlSupport.mediaId, isEqualTo(mediaId))
                    .and(MediaTagsDynamicSqlSupport.tagId, isIn(toDeleteTagIds))
            );
        }

        // 既存MediaTagにはなく新規追加タグにはあるタグIDを抽出
        List<MediaTags> toInsertTags = tagIds.stream()
            .filter(tagId -> !currentTagIds.contains(tagId))
            .map(tagId -> {
                MediaTags mediaTag = new MediaTags();
                mediaTag.setTagId(tagId);
                mediaTag.setMediaId(mediaId);
                mediaTag.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                return mediaTag;
            })
            .toList();

        // 上記を一括登録する
        if (!toInsertTags.isEmpty()) {
            mediaTagsMapper.insertMultiple(toInsertTags);
        }

        // 更新後のmedia_tagsからtagIdを取り出す
        List<Long> updatedTagIds = Stream.concat(
            currentTagIds.stream().filter(id -> !toDeleteTagIds.contains(id)),
            toInsertTags.stream().map(MediaTags::getTagId)
        ).toList();

        // タグIDが空の場合は空リストを返す
        if (updatedTagIds.isEmpty()) {
            return List.of();
        }

        // tagIdでTagsを取得して返す
        return tagsMapper.select(
            // "WHERE id IN (?, ?, ...)"
            c -> c.where(TagsDynamicSqlSupport.id, isIn(updatedTagIds))
        );
    }

    /**
     * タグIDごとのメディア数を返す<br>
     * 自動生成コードでは全件取得してから計算し高負荷となるためカスタムマッパーを使用してDB側で集計
     * 返り値をMapに格納して返す
     *
     * @return タグID → メディア数のマップ
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> getMediaCountByTagId() {
        return mediaTagsCustomMapper.selectMediaCountByTagId()
            .stream()
            .collect(Collectors.toMap(
                TagMediaCount::getTagId,
                TagMediaCount::getMediaCount
            ));
    }

    /**
     * タグをID指定で1件取得する
     *
     * @param id 対象タグのID
     * @return タグデータ
     */
    @Transactional(readOnly = true)
    public Tags getTagById(Long id) {
        return tagsMapper.selectByPrimaryKey(id)
            .orElseThrow(() -> new ResourceNotFoundException("タグが見つかりません id=" + id));
    }


    /**
     * タグの名前を更新する
     *
     * @param tagId 対象タグのID
     * @param name 変更する名前
     */
    @Transactional
    public void updateTag(Long tagId, String name) {
        // tagIdからタグを取得
        Tags tag = getTagById(tagId);

        // 新しい名前をセットして更新する
        tag.setName(name);
        tagsMapper.updateByPrimaryKeySelective(tag);
    }

    /**
     * タグを削除する
     *
     * @param tagId タグID
     */
    @Transactional
    public void deleteTag(Long tagId) {
        mediaTagsMapper.delete(c -> c.where(MediaTagsDynamicSqlSupport.tagId, isEqualTo(tagId)));
        tagsMapper.delete(c -> c.where(id, isEqualTo(tagId)));
    }
}
