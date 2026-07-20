package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItemResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import org.springframework.stereotype.Component;

/**
 * TrashItemエンティティをTrashItemResponseDtoに変換するコンバーター
 */
@Component
public class TrashItemConverter {
    /**
     * TrashItemsエンティティリストをTrashItemResponseDtoに変換する
     *
     * @param trashItem TrashItemエンティティ
     * @return TrashItemResponseDto
     */
    public TrashItemResponseDto toTrashItemResponseDto(
        TrashItems trashItem, MediaResponseDto media) {
        return new TrashItemResponseDto()
            .id(trashItem.getId())
            .media(media)
            .expiresAt(trashItem.getExpiresAt())
            .createdAt(trashItem.getCreatedAt());
    }
}
