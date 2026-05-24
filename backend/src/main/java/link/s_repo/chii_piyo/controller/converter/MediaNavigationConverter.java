package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaNavigationResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * メディアのナビゲーション情報を組み立てるコンバータークラス<br>
 * MediaエンティティからMediaNavigationResponseDtoに変換するロジックを提供
 */
@Component
public class MediaNavigationConverter {

    public MediaNavigationResponseDto toMediaNavigationResponseDto(
        Media media, URI thumbnailPresignedUrl) {
        MediaNavigationResponseDto dto = new MediaNavigationResponseDto(
            media.getId(),
            MediaNavigationResponseDto.MediaTypeEnum.
                fromValue(media.getMediaType())
        );

        // サムネイルのダウンロード用署名付きURL
        dto.setThumbnailPresignedUrl(JsonNullable.of(thumbnailPresignedUrl));

        return dto;
    }
}
