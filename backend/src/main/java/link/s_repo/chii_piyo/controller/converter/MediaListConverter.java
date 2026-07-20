package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaListResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MediaエンティティをMediaResponseDtoに変換するコンバーター
 */
@Component
public class MediaListConverter {
    /**
     * MediaエンティティをMediaResponseDtoに変換する
     *
     * @param mediaList  Mediaエンティティのリスト
     * @param totalCount 総件数
     * @param hasNext    次のページがあるかどうか
     * @return MediaResponseDto
     */
    public MediaListResponseDto toMediaListResponseDto(
        List<MediaResponseDto> mediaList, Long totalCount, boolean hasNext) {
        return new MediaListResponseDto()
            .items(mediaList)
            .totalCount(totalCount)
            .hasNext(hasNext);
    }
}
