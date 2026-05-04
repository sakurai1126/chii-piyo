package link.s_repo.chii_piyo.config;

import link.s_repo.chii_piyo.security.CustomAuthenticationEntryPoint;
import link.s_repo.chii_piyo.security.CustomAccessDeniedHandler;
import link.s_repo.chii_piyo.security.CustomJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // 環境変数からapplication.yaml経由で取得した値をセット
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    @Value("${aws.cognito.audience}")
    private String audience;
    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    // カスタムJWTコンバータ、認証エントリポイント、アクセス拒否ハンドラーを用意
    private final CustomJwtAuthenticationConverter customJwtAuthConverter;
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * セキュリティフィルターチェーン定義<br>
     * HTTPセキュリティの設定を行い、OAuth2リソースサーバーとしてJWT認証を使用するように構成
     * CSRF保護を無効化し、セッション管理をステートレスに設定<br>
     * JWTの検証と認証にカスタムコンバータとハンドラーを使用
     *
     * @param http HttpSecurityオブジェクトを受け取りセキュリティ設定を構築
     * @return セキュリティフィルターチェーンを構築して返す
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            // CORS設定を有効化
            .cors(Customizer.withDefaults())
            // JWTステートレス認証のためCSRFは不要
            .csrf(AbstractHttpConfigurer::disable)
            // セッションを生成しないステートレス認証を設定
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // ヘルスチェックエンドポイントのみ全てのユーザーに許可するよう認可ルールを設定
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            // JWTの検証と認証情報のDB登録、共通ApiResponse形式で返却するために認証失敗時のエントリポイントとアクセス拒否時のハンドラーをそれぞれカスタマイズ
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthConverter))
                .authenticationEntryPoint(customAuthEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            );

        return http.build();
    }

    /**
     * CORS設定
     * フロントからのブラウザ経由リクエストを許可するため設定する
     * 全てのアクセスに対して、application.yamlで指定したオリジンからのリクエストを許可するように設定
     *
     * @return CORS設定ソース
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /**
     * JwtDecoderをカスタマイズ
     * 有効期限+発行者+クライアントIDの検証を行うように設定
     *
     * @return カスタマイズされたJwtDecoderインスタンス
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // CognitoのJWKセットURIを使用してNimbusJwtDecoderを構築
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // デフォルトの有効期限検証＋発行者検証セットとAudienceクレーム(アプリケーションクライアントID)を検証するカスタムルールを作成し統合する
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(defaultValidators, audienceValidator);

        // JwtDecoderにカスタム検証ルールをセット
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
