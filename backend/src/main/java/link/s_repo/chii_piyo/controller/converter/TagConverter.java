package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import org.springframework.stereotype.Component;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * TagsエンティティをTagResponseDtoに変換するロジックを提供する
 */
@Component
public class TagConverter {

    /**
     * TagsエンティティをTagResponseDtoに変換する
     *
     * @param tag Tagsエンティティ
     * @return TagResponseDto
     */
    public TagResponseDto toTagResponseDto(Tags tag) {
        return new TagResponseDto(
            tag.getId(),
            tag.getName(),
            tag.getCreatedAt()
        );
    }
}
