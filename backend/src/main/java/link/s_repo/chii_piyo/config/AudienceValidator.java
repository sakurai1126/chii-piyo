package link.s_repo.chii_piyo.config;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * JWTのAudienceクレーム(アプリケーションクライアントID)の検証設定<br>
 * Cognitoから発行されたトークンがこのアプリケーション向けに発行されていることを保証する
 */
@RequiredArgsConstructor
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    // CognitoのアプリケーションクライアントIDを使用
    private final String audience;

    @Override
    @NonNull
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        // JWTのAudienceクレームに指定された値が期待するアプリケーションクライアントIDと一致するかを検証
        if (jwt.getAudience() != null && jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }

        // 一致しない場合はエラーコードと説明を含むOAuth2Errorオブジェクトを作成して返す
        OAuth2Error error = new OAuth2Error(
            "invalid_token",
            "Audienceが一致しません",
            null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
