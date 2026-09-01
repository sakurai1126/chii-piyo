package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaNavigationResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * メディアレスポンスに必要な各情報を受け取りMediaResponseDtoに変換するコンバーター
 */
@Component
public class MediaConverter {
    /**
     * MediaResponseDtoに変換する
     *
     * @param media                 メディアエンティティ
     * @param tags                  メディアに紐づくタグのリスト
     * @param presignedUrl          ダウンロード用URL
     * @param thumbnailPresignedUrl サムネイルのダウンロード用URL
     * @param isFavorite            お気に入りフラグ
     * @param commentCount          コメント数
     * @param nextMedia             次のメディア
     * @param secondNextMedia       2つ後のメディア
     * @param previousMedia         前のメディア
     * @param secondPreviousMedia   2つ前のメディア
     * @param addFavoriteUserIds    お気に入りに追加したユーザーのIDリスト
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
        return new MediaResponseDto()
            .id(media.getId()) // メディアID
            .uploadedBy(media.getUploadedBy()) // アップロードしたユーザーID
            .mediaType(MediaResponseDto.MediaTypeEnum.fromValue(media.getMediaType())) // メディアの種類（画像、動画）
            .originalFilename(media.getOriginalFilename()) // オリジナルファイル名
            .contentType(media.getContentType()) // コンテンツタイプ
            .fileSize(media.getFileSize()) // ファイルサイズ
            .width(media.getWidth()) // 横幅
            .height(media.getHeight()) // 高さ
            .takenAt(media.getTakenAt()) // 撮影日時
            .albumId(media.getAlbumId()) // 関連するアルバムID
            .sharingGroupId(media.getSharingGroupId()) // 関連する共有グループID
            .uploadStatus(MediaResponseDto.UploadStatusEnum.fromValue(media.getUploadStatus())) // アップロードステータス
            .createdAt(media.getCreatedAt()) // 作成日時
            .updatedAt(media.getUpdatedAt()) // 更新日時
            .presignedUrl(presignedUrl) // ダウンロード用URL
            .thumbnailPresignedUrl(thumbnailPresignedUrl) // サムネイルのダウンロード用URL
            .tags(tags) // タグのリスト
            .isFavorite(isFavorite) // お気に入りフラグ
            .commentCount(commentCount) // コメントの数
            .nextMedia(nextMedia) // 次のメディアのID
            .secondNextMedia(secondNextMedia) // 2つ後のメディアのID
            .previousMedia(previousMedia) // 前のメディアのID
            .secondPreviousMedia(secondPreviousMedia) // 2つ前のメディアのID
            .addFavoriteUserIds(Optional.ofNullable(addFavoriteUserIds).orElse(List.of())); // お気に入りに追加したユーザーのIDリスト
    }
}
