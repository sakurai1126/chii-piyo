package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.controller.gen.TagsApi;
import link.s_repo.chii_piyo.model.gen.TagRequestDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import link.s_repo.chii_piyo.model.gen.Tags;

import java.util.List;

/**
 * タグ管理コントローラー<br>
 * OpenAPI Generator生成のTagsApiインターフェースを実装し、タグの取得・作成およびメディアとのタグ紐付けに関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController implements TagsApi {

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
        TagResponseDto response = tagConverter.toTagResponseDto(createdTag);
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
        List<TagResponseDto> response = tagService.findAll().stream()
            .map(tagConverter::toTagResponseDto)
            .toList();

        return ResponseEntity.ok(response);
    }

}
