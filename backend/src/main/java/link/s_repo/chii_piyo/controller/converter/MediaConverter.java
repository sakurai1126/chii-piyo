package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import org.springframework.stereotype.Component;

import java.util.Collections;

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
     * @return MediaResponseDto
     */
    public MediaResponseDto toMediaResponseDto(Media media) {
        // 必須フィールドを揃えてコンストラクタに渡す
        return new MediaResponseDto(
            media.getId(),
            media.getUploadedBy(),
            MediaResponseDto.MediaTypeEnum.fromValue(media.getMediaType()),
            media.getOriginalFilename(),
            media.getContentType(),
            media.getFileSize(),
            media.getWidth(),
            media.getHeight(),
            media.getS3Key(),
            media.getThumbnailS3Key(),
            media.getTakenAt(),
            media.getAlbumId(),
            media.getSharingGroupId(),
            MediaResponseDto.UploadStatusEnum.fromValue(media.getUploadStatus()),
            // TODO タグは後ほど実装
            Collections.emptyList(),
            media.getCreatedAt(),
            media.getUpdatedAt()
        );
    }


}
