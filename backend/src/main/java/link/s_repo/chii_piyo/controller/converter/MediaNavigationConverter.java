package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaNavigationResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * MediaエンティティからMediaNavigationResponseDtoに変換するコンバーター
 */
@Component
public class MediaNavigationConverter {
    /**
     * MediaエンティティからMediaNavigationResponseDtoに変換する
     *
     * @param media                 Mediaエンティティ
     * @param thumbnailPresignedUrl サムネイルダウンロード用URL
     * @return MediaNavigationResponseDto
     */
    public MediaNavigationResponseDto toMediaNavigationResponseDto(
        Media media, URI thumbnailPresignedUrl) {
        return new MediaNavigationResponseDto()
            .id(media.getId())
            .mediaType(MediaNavigationResponseDto.MediaTypeEnum.fromValue(media.getMediaType()))
            .thumbnailPresignedUrl(thumbnailPresignedUrl);
    }
}
