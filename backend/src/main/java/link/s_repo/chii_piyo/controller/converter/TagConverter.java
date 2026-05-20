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
    public TagResponseDto toTagResponseDto(Tags tag, Long mediaCount) {
        TagResponseDto dto = new TagResponseDto(
                tag.getId(), // ID
                tag.getName(), // タグ名
                tag.getCreatedAt() // 作成日時
        );

        dto.setMediaCount(mediaCount); // タグが紐づいているメディアの数
        return dto;
    }
}
