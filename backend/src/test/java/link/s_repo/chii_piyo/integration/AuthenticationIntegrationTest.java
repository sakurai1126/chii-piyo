package link.s_repo.chii_piyo.integration;

import link.s_repo.chii_piyo.IntegrationTestBase;
import link.s_repo.chii_piyo.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationIntegrationTest extends IntegrationTestBase {
    @Test
    @DisplayName("IT-01: 有効なJWTでリクエストした場合認証を通過し200が返ること")
    void authenticatedSuccess() throws Exception {
        mockMvc.perform(get("/users").with(jwt()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IT-02: JWTなしで保護されたエンドポイントにアクセスした場合401エラーを返すこと")
    void accessProtectedUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("IT-03: 不正なJWTでリクエストした場合401エラーを返すこと")
    void invalidJwt() throws Exception {
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer invalid-jwt"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getMessage()));
    }

    @Test
    @DisplayName("IT-04: 認証不要エンドポイントにはJWTなしで200が返ること")
    void publicEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("IT-05: 閲覧者ロールで管理者専用操作を実行した場合403が返ること")
    void viewerForbidden() throws Exception {
        mockMvc.perform(delete("/first-records/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIEWER"))))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN.getMessage()));
    }
}
