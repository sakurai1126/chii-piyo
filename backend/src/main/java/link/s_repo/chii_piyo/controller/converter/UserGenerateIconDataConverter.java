package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.UserGenerateIconDataResponseDto;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * S3キーとS3アップロード用URLをUserGenerateIconDataResponseDtoに変換するコンバーター
 */
@Component
public class UserGenerateIconDataConverter {
    /**
     * S3キーとS3アップロード用URLをUserGenerateIconDataResponseDtoに変換する
     *
     * @param s3Key        S3キー
     * @param presignedUrl S3アップロード用URL
     * @return UserGenerateIconDataResponseDto
     */
    public UserGenerateIconDataResponseDto toUserGenerateIconDataResponseDto(
        String s3Key, URI presignedUrl) {
        return new UserGenerateIconDataResponseDto()
            .presignedUrl(presignedUrl)
            .s3key(s3Key);
    }
}
