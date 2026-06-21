package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.TrashItemListResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItemResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * TrashItemsエンティティをTrashItemListResponseDtoに変換するロジックを提供する
 */
@Component
public class TrashItemListConverter {
    /**
     * TrashItemsエンティティリストをTrashItemListResponseDtoに変換する
     *
     * @param trashItems TrashItemsのDTOリスト
     * @param earliest   一番近い完全削除までの日数
     * @param totalCount 総件数
     * @param hasNext    次のページがあるかどうか
     * @return TrashItemListResponseDto
     */
    public TrashItemListResponseDto toTrashItemListResponseDto(
        List<TrashItemResponseDto> trashItems, Long earliest, Long totalCount, boolean hasNext) {
        return new TrashItemListResponseDto(trashItems, earliest, totalCount, hasNext);
    }
}
