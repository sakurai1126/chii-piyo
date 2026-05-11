package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;

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
import java.util.List;
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
    public List<Tags> findAll() {
        return tagsMapper.select(c -> c.orderBy(id));
    }

    /**
     * メディアに紐づくタグを一括更新する<br>
     * <p>
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
}
