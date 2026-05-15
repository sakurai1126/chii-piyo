package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public MediaResponseDto toMediaResponseDto(Media media, List<TagResponseDto> tags) {
        // 必須フィールドを揃えてコンストラクタに渡す
        return new MediaResponseDto(
            media.getId(), // メディアID
            media.getUploadedBy(), // アップロードしたユーザーID
            MediaResponseDto.MediaTypeEnum.
                fromValue(media.getMediaType()), // メディアの種類（画像、動画）
            media.getOriginalFilename(), // オリジナルファイル名
            media.getContentType(), // コンテンツタイプ
            media.getFileSize(), // ファイルサイズ
            media.getWidth(), // 横幅
            media.getHeight(), // 高さ
            media.getS3Key(), // S3キー
            media.getThumbnailS3Key(), // サムネイルのS3キー
            media.getTakenAt(), // 撮影日時
            media.getAlbumId(), // 関連するアルバムID
            media.getSharingGroupId(), // 関連する共有グループID
            MediaResponseDto.UploadStatusEnum.
                fromValue(media.getUploadStatus()), // アップロードステータス
            tags, // タグのリスト
            media.getCreatedAt(), // 作成日時
            media.getUpdatedAt() // 更新日時
        );
    }
}
