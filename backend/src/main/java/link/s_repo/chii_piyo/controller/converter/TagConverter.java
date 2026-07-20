package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import org.springframework.stereotype.Component;

/**
 * TagsエンティティをTagResponseDtoに変換するコンバーター
 */
@Component
public class TagConverter {
    /**
     * TagsエンティティをTagResponseDtoに変換する
     *
     * @param tag Tagsエンティティ
     * @return TagResponseDto
     */
    public TagResponseDto toTagResponseDto(Tags tag, Long mediaCount) {
        return new TagResponseDto()
            .id(tag.getId())
            .name(tag.getName())
            .createdAt(tag.getCreatedAt())
            .mediaCount(mediaCount);
    }
}
