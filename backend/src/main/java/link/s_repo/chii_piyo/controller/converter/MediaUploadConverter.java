package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.MediaUploadResponseDto;
import org.springframework.stereotype.Component;

import java.net.URI;


/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * メディアIDと署名付きS3アップロード用URLをMediaUploadResponseDtoに変換するロジックを提供する
 */
@Component
public class MediaUploadConverter {
    /**
     * メディアIDと署名付きS3アップロード用URLをMediaUploadResponseDtoに変換する
     *
     * @param mediaId        メディアID
     * @param presignedUrl 署名付きS3アップロード用URL
     * @return MediaUploadResponseDto
     */
    public MediaUploadResponseDto toMediaUploadResponseDto(Long mediaId, URI presignedUrl) {
        return new MediaUploadResponseDto(
            mediaId,
            presignedUrl
        );
    }
}

