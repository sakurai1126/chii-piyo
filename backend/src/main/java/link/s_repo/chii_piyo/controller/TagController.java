package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.TagConverter;

import link.s_repo.chii_piyo.controller.gen.TagManagementApi;
import link.s_repo.chii_piyo.model.gen.MediaTagsUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.TagRequestDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * タグ管理コントローラー<br>
 * タグの取得・作成およびメディアとのタグ紐付けに関するAPIエンドポイントを提供
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController implements TagManagementApi {
    private final TagService tagService;
    private final TagConverter tagConverter;
    private final MediaService mediaService;

    /**
     * POST /tags<br>
     * タグを作成する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param tagData        アップロードリクエストDTO
     * @return 201ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createTag(String xRequestedWith, TagRequestDto tagData) {
        // サービス層でタグを作成する
        tagService.createTag(tagData.getName());

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /tags<br>
     * タグ一覧を取得する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
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
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param mediaId        メディアID
     * @param mediaTagsData  紐付けるタグIDの一覧
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMediaTags(
        String xRequestedWith, Long mediaId, MediaTagsUpdateRequestDto mediaTagsData) {
        // メディアの存在チェック
        mediaService.getMedia(mediaId);

        // サービス層でタグを更新する
        tagService.syncMediaTags(mediaId, mediaTagsData.getTagIds());

        // 204ステータスを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /tags/{tagId}<br>
     * タグを更新する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param tagId          タグID
     * @param tagData        タグの更新データ
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
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
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param tagId          タグID
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(String xRequestedWith, Long tagId) {
        // サービス層でタグを削除する
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
