package link.s_repo.chii_piyo.integration;

import link.s_repo.chii_piyo.IntegrationTestBase;
import link.s_repo.chii_piyo.common.ErrorCode;
import link.s_repo.chii_piyo.model.gen.MediaUploadRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExceptionHandlingIntegrationTest extends IntegrationTestBase {
    @Autowired
    private ObjectMapper objectMapper;

    private static final String cognitoUserId = "cognito-sub-test-123";

    @Test
    @DisplayName("IT-06: 存在しないメディアIDでリクエストした場合404とNOT_FOUNDのレスポンスが返ること")
    void notFoundError() throws Exception {
        mockMvc.perform(get("/media/{id}", 99L)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    .jwt(jwt -> jwt.subject(cognitoUserId))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("IT-07: バリデーション違反のリクエストをした場合400とVALIDATION_ERRORのレスポンスが返ること")
    void varidationError() throws Exception {
        // 空のリクエストでリクエストを送信し結果を検証
        mockMvc.perform(post("/media")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    .jwt(jwt -> jwt.subject(cognitoUserId)))
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(new MediaUploadRequestDto())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.getCode()));
    }

    @Test
    @DisplayName("IT-08: 一般ユーザーでメディアを更新しようとした場合403とFORBIDDENのレスポンスが返ること")
    void forbiddenError() throws Exception {
        mockMvc.perform(patch("/media/{id}", 1L)
                .with(jwt().jwt(jwt -> jwt.subject(cognitoUserId)))
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(new MediaUploadRequestDto())))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN.getCode()));
    }
}
