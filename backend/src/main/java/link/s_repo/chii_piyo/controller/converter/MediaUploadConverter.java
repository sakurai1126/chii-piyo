package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaUploadResponseDto;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * メディアIDとS3アップロード用URLをMediaUploadResponseDtoに変換するコンバーター
 */
@Component
public class MediaUploadConverter {
    /**
     * メディアIDとS3アップロード用URLをMediaUploadResponseDtoに変換する
     *
     * @param mediaId      メディアID
     * @param presignedUrl S3アップロード用URL
     * @return MediaUploadResponseDto
     */
    public MediaUploadResponseDto toMediaUploadResponseDto(Long mediaId, URI presignedUrl) {
        return new MediaUploadResponseDto()
            .mediaId(mediaId)
            .presignedUrl(presignedUrl);
    }
}

