package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.TagMediaCount;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.TagRepository;
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


/**
 * タグ管理サービス<br>
 * タグの取得・作成およびメディアとのタグ紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /**
     * タグを新規作成する<br>
     *
     * @param name 追加するタグ名
     * @return 作成されたタグエンティティ
     */
    @Transactional
    public Tags createTag(String name) {
        Tags tag = new Tags();

        // タグエンティティに値をセット
        tag.setName(name);

        // タグをDBに保存
        tagRepository.save(tag);
        return tag;
    }

    /**
     * タグ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return タグエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<Tags> getTags() {
        return tagRepository.findAllOrderById();
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
        List<Long> tagIds = tagRepository.findMediaTagsByMediaId(mediaId)
            .stream()
            .map(MediaTags::getTagId)
            .toList();

        // タグが空の場合は空リストを渡す
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return tagRepository.findByIds(tagIds);
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
        List<Long> currentTagIds = tagRepository.findMediaTagsByMediaId(mediaId).stream()
            // MediaTagsエンティティからタグIDだけ抜き取りリスト化
            .map(MediaTags::getTagId)
            .toList();

        // 既存タグには存在するが新規タグには存在しないタグIDを抽出
        List<Long> toDeleteTagIds = currentTagIds.stream()
            .filter(tagId -> !tagIds.contains(tagId))
            .toList();

        // 上記のIDのタグを一括で削除する
        if (!toDeleteTagIds.isEmpty()) {
            tagRepository.deleteMediaTagsByMediaIdAndTagIds(mediaId, toDeleteTagIds);
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
            tagRepository.saveMediaTags(toInsertTags);
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
        return tagRepository.findByIds(updatedTagIds);
    }

    /**
     * タグIDごとのメディア数ををMapに格納して返す
     *
     * @return タグID → メディア数のマップ
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> getMediaCountByTagId() {
        return tagRepository.selectMediaCountByTagId()
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
        return tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("タグが見つかりません id=" + id));
    }


    /**
     * タグの名前を更新する
     *
     * @param tagId 対象タグのID
     * @param name  変更する名前
     */
    @Transactional
    public void updateTag(Long tagId, String name) {
        // tagIdからタグを取得
        Tags tag = getTagById(tagId);

        // 新しい名前をセットして更新する
        tag.setName(name);
        tagRepository.update(tag);
    }

    /**
     * タグを削除する
     *
     * @param tagId タグID
     */
    @Transactional
    public void deleteTag(Long tagId) {
        tagRepository.deleteMediaTagsByTagId(tagId);
        tagRepository.delete(tagId);
    }

    /**
     * タグIDのリストから一致するタグの件数を返す
     *
     * @param tagIds タグIDのリスト
     * @return タグIDのリストに一致するタグの件数
     */
    public Long count(List<Long> tagIds) {
        return tagRepository.countByTagIds(tagIds);
    }
}
