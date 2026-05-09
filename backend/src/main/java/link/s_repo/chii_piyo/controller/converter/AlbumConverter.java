package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * AlbumsエンティティをAlbumResponseDtoに変換するロジックを提供する
 */
@Component
public class AlbumConverter {
    /**
     * AlbumsエンティティをAlbumResponseDtoに変換する
     *
     * @param album Albumエンティティ
     * @return AlbumResponseDto
     */
    public AlbumResponseDto toAlbumResponseDto(Albums album) {
        return new AlbumResponseDto(
            album.getId(),
            album.getTitle(),
            Collections.emptyList(), // 最初の画像のURL
            0, // 画像数
            0, // 動画数
            album.getCreatedAt(),
            album.getUpdatedAt()
        );
    }
}
