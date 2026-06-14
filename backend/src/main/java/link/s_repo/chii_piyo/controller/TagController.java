package link.s_repo.chii_piyo.controller;


import link.s_repo.chii_piyo.controller.converter.TagConverter;

import link.s_repo.chii_piyo.controller.gen.TagManagementApi;
import link.s_repo.chii_piyo.model.gen.MediaTagsUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.TagRequestDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

/**
 * タグ管理コントローラー<br>
 * OpenAPI Generator生成のTagManagementApiインターフェースを実装し、タグの取得・作成およびメディアとのタグ紐付けに関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController implements TagManagementApi {

    private final TagService tagService;
    private final TagConverter tagConverter;

    /**
     * POST /tags<br>
     * タグを作成する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param tagData        アップロードリクエストDTO
     * @return 作成されたタグの情報
     */
    @Override
    public ResponseEntity<TagResponseDto> createTag(String xRequestedWith, TagRequestDto tagData) {
        // サービス層でタグを作成する
        Tags createdTag = tagService.createTag(tagData.getName());

        // 作成されたタグをDTOに変換してレスポンスする
        TagResponseDto response = tagConverter.toTagResponseDto(createdTag, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /tags<br>
     * タグ一覧を取得する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return タグの一覧
     */
    @Override
    public ResponseEntity<List<TagResponseDto>> getTags(String xRequestedWith) {
        // サービス層でエンティティを取得し、コンバータでDTOに変換する
        List<Tags> tags = tagService.getTags();
        Map<Long, Long> mediaCountMap = tagService.getMediaCountByTagId();

        List<TagResponseDto> response = tags.stream()
            .map(tag -> tagConverter.toTagResponseDto(
                tag,
                mediaCountMap.getOrDefault(tag.getId(), 0L)
            ))
            .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /media/{mediaId}/tags<br>
     * メディアのタグを一括更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param mediaId        メディアID
     * @param mediaTagsData  紐付けるタグIDの一覧
     * @return 更新後のタグ一覧
     */
    @Override
    public ResponseEntity<List<TagResponseDto>> updateMediaTags(
        String xRequestedWith, Long mediaId, MediaTagsUpdateRequestDto mediaTagsData) {

        // サービス層でタグを更新する
        List<Tags> updatedTags = tagService.syncMediaTags(mediaId, mediaTagsData.getTagIds());

        // 更新されたタグをDTOに変換してレスポンスする
        List<TagResponseDto> response = updatedTags.stream()
            .map(tag -> tagConverter.toTagResponseDto(tag, null))
            .toList();
        return ResponseEntity.ok(response);
    }


    /**
     * PUT /tags/{tagId}<br>
     * タグを更新する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param tagId          タグID
     * @param tagData        タグの更新データ
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateTag(
        String xRequestedWith, Long tagId, TagRequestDto tagData) {
        // タグ名が空の場合は400 Bad Requestを返す
        if (tagData.getName() == null || tagData.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // サービス層でタグを更新する
        tagService.updateTag(tagId, tagData.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /tags/{tagId}<br>
     * タグを削除する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param tagId          タグID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteTag(String xRequestedWith, Long tagId) {
        // サービス層でタグを削除する
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
