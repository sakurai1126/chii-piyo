package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.service.AlbumService;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * AlbumsエンティティをAlbumResponseDtoに変換するロジックを提供する
 */
@Component
public class AlbumConverter {
    /**
     * AlbumsエンティティをAlbumResponseDtoに変換する
     *
     * @param album      Albumエンティティ
     * @param urls       カバーURLのリスト
     * @param mediaCount 画像数と動画数のレコード
     * @return AlbumResponseDto
     */
    public AlbumResponseDto toAlbumResponseDto(
        Albums album, List<String> urls, AlbumService.MediaCountResult mediaCount) {
        return new AlbumResponseDto(
            album.getId(), // ID
            album.getTitle(), // アルバムのタイトル
            urls, // カバーURL一覧
            mediaCount.photoCount(), // 画像数
            mediaCount.videoCount(), // 動画数
            album.getCreatedAt(), // 作成日時
            album.getUpdatedAt() // 更新日時
        );
    }
}
