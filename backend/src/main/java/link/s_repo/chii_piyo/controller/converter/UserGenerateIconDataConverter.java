package link.s_repo.chii_piyo.controller.converter;

import link.s_repo.chii_piyo.model.gen.UserGenerateIconDataResponseDto;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * APIレスポンスの組み立てを担当するコンバータークラス<br>
 * S3キーと署名付きS3アップロード用URLをUserGenerateIconDataResponseDtoに変換するロジックを提供する
 */
@Component
public class UserGenerateIconDataConverter {
    /**
     * S3キーと署名付きS3アップロード用URLをUserGenerateIconDataResponseDtoに変換する
     *
     * @param s3Key        S3キー
     * @param presignedUrl 署名付きS3アップロード用URL
     * @return UserGenerateIconDataResponseDto
     */
    public UserGenerateIconDataResponseDto toUserGenerateIconDataResponseDto(
        String s3Key,
        URI presignedUrl) {
        return new UserGenerateIconDataResponseDto(
            presignedUrl,
            s3Key
        );
    }
}
