package link.s_repo.chii_piyo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AudienceValidatorTest {
    @Nested
    @DisplayName("validate - JWTのAudienceクレーム検証")
    class Validate {
        // テスト対象の生成
        String audience = "12345";
        private final AudienceValidator audienceValidator = new AudienceValidator(audience);

        @Test
        @DisplayName("Auth-01: audienceが一致するJWTを渡すことで成功判定が返ること")
        void validate_success() {
            // JWTのモックを生成
            Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .audience(List.of(audience))
                .build();

            // 検証の実行
            OAuth2TokenValidatorResult result = audienceValidator.validate(mockJwt);

            // 結果の検証
            assertThat(result.hasErrors()).isFalse();
        }

        @Test
        @DisplayName("Auth-02: audienceが一致しないJWTを渡すことで失敗判定が返ること")
        void validate_difference() {
            // JWTのモックを生成
            Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .audience(List.of("difference"))
                .build();

            // 検証の実行
            OAuth2TokenValidatorResult result = audienceValidator.validate(mockJwt);

            // 結果の検証
            assertThat(result.hasErrors()).isTrue();
        }

        @Test
        @DisplayName("Auth-03: audienceがnullのJWTを渡すことで失敗判定が返ること")
        void validate_null() {
            // JWTのモックを生成
            Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("sub", "user-123")
                .build();

            // 検証の実行
            OAuth2TokenValidatorResult result = audienceValidator.validate(mockJwt);

            // 結果の検証
            assertThat(result.hasErrors()).isTrue();
        }
    }

}
