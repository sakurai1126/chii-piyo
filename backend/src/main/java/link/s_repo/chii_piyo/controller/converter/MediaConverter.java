package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaNavigationResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * MediaエンティティをMediaResponseDtoに変換するロジックを提供する
 */
@Component
public class MediaConverter {

    /**
     * MediaエンティティをMediaResponseDtoに変換する
     *
     * @param media Mediaエンティティ
     * @param tags  メディアに紐づくタグのリスト
     * @return MediaResponseDto
     */
    public MediaResponseDto toMediaResponseDto(
        Media media,
        List<TagResponseDto> tags,
        URI presignedUrl,
        URI thumbnailPresignedUrl,
        Boolean isFavorite,
        Long commentCount,
        MediaNavigationResponseDto nextMedia,
        MediaNavigationResponseDto secondNextMedia,
        MediaNavigationResponseDto previousMedia,
        MediaNavigationResponseDto secondPreviousMedia,
        List<Long> addFavoriteUserIds
    ) {

        // 必須フィールドを揃えてコンストラクタに渡す
        MediaResponseDto dto = new MediaResponseDto(
            media.getId(), // メディアID
            media.getUploadedBy(), // アップロードしたユーザーID
            MediaResponseDto.MediaTypeEnum.
                fromValue(media.getMediaType()), // メディアの種類（画像、動画）
            media.getOriginalFilename(), // オリジナルファイル名
            media.getContentType(), // コンテンツタイプ
            media.getFileSize(), // ファイルサイズ
            media.getWidth(), // 横幅
            media.getHeight(), // 高さ
            media.getTakenAt(), // 撮影日時
            media.getAlbumId(), // 関連するアルバムID
            media.getSharingGroupId(), // 関連する共有グループID
            MediaResponseDto.UploadStatusEnum.
                fromValue(media.getUploadStatus()), // アップロードステータス
            media.getCreatedAt(), // 作成日時
            media.getUpdatedAt() // 更新日時
        );

        dto.setPresignedUrl(presignedUrl); // ダウンロード用署名付きURL
        dto.setThumbnailPresignedUrl(JsonNullable.of(thumbnailPresignedUrl)); // サムネイルのダウンロード用署名付きURL
        dto.setTags(tags);// タグのリスト
        dto.setIsFavorite(isFavorite); // お気に入りフラグ
        dto.setCommentCount(commentCount); // コメントの数
        dto.setNextMedia(nextMedia); // 次のメディアのID
        dto.setSecondNextMedia(secondNextMedia); // 2つ後のメディアのID
        dto.setPreviousMedia(previousMedia); // 前のメディアのID
        dto.setSecondPreviousMedia(secondPreviousMedia); // 2つ前のメディアのID
        dto.setAddFavoriteUserIds(Optional.ofNullable(addFavoriteUserIds).orElse(List.of()));

        return dto;
    }
}
