package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.service.AlbumService.MediaDataResult;
import org.springframework.stereotype.Component;

/**
 * AlbumsエンティティをAlbumResponseDtoに変換するコンバーター
 */
@Component
public class AlbumConverter {
    /**
     * AlbumsエンティティをAlbumResponseDtoに変換する
     *
     * @param album     Albumエンティティ
     * @param mediaData 画像数と動画数とカバーURLのリストのレコード
     * @return AlbumResponseDto
     */
    public AlbumResponseDto toAlbumResponseDto(Albums album, MediaDataResult mediaData) {
        return new AlbumResponseDto()
            .id(album.getId())
            .title(album.getTitle())
            .coverMediaUrls(mediaData.urls())
            .photoCount(mediaData.photoCount())
            .videoCount(mediaData.videoCount())
            .createdAt(album.getCreatedAt())
            .updatedAt(album.getUpdatedAt());
    }
}
