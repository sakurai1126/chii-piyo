package link.s_repo.chii_piyo.controller.converter;
import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.service.AlbumService.MediaDataResult;
import org.springframework.stereotype.Component;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * AlbumsエンティティをAlbumResponseDtoに変換するロジックを提供する
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
    public AlbumResponseDto toAlbumResponseDto(
        Albums album, MediaDataResult mediaData) {
        return new AlbumResponseDto(
            album.getId(), // ID
            album.getTitle(), // アルバムのタイトル
            mediaData.urls(), // カバーURL一覧
            mediaData.photoCount(), // 画像数
            mediaData.videoCount(), // 動画数
            album.getCreatedAt(), // 作成日時
            album.getUpdatedAt() // 更新日時
        );
    }
}
