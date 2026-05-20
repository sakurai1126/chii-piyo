package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaListResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * MediaエンティティをMediaResponseDtoに変換するロジックを提供する
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
        List<MediaResponseDto> mediaList,
        Long totalCount,
        boolean hasNext) {

        return new MediaListResponseDto(
            mediaList,
            totalCount,
            hasNext
        );
    }
}
