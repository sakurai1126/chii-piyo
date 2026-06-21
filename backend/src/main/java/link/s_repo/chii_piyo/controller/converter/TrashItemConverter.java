package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItemResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import org.springframework.stereotype.Component;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * TrashItemエンティティをTrashItemResponseDtoに変換するロジックを提供する
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
        TrashItems trashItem,
        MediaResponseDto media) {
        return new TrashItemResponseDto(
            trashItem.getId(),
            media,
            trashItem.getExpiresAt(),
            trashItem.getCreatedAt()
        );
    }
}
