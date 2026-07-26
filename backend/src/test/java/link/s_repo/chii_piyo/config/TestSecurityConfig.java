package link.s_repo.chii_piyo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * テスト環境専用のSpring Security設定クラス
 */
@TestConfiguration // テスト実行時のみ読み込まれる設定クラスであることを定義
public class TestSecurityConfig {
    /**
     * テスト用のセキュリティフィルターチェーンを定義するBean
     *
     * @param http HttpSecurity設定用オブジェクト
     * @return 構築されたSecurityFilterChain
     * @throws Exception 設定時の例外
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // テストリクエストの作成を容易にするためCSRF保護を無効化
            .csrf(AbstractHttpConfigurer::disable)
            // すべてのエンドポイントで認証を必須とする
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }
}
