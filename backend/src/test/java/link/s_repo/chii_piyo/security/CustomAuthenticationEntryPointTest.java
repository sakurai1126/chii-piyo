package link.s_repo.chii_piyo.security;

import jakarta.servlet.http.HttpServletResponse;
import link.s_repo.chii_piyo.common.ApiResponse;
import link.s_repo.chii_piyo.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationEntryPointTest {
    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Nested
    @DisplayName("commence - 認可エラーハンドラー")
    class Commence {
        @Test
        @DisplayName("Auth-11: 認証エラー発生時、共通定義のApiResponse形式で401を返すこと")
        void commence_success() throws IOException {
            // リクエスト・レスポンス・例外オブジェクトの作成
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/media");

            MockHttpServletResponse response = new MockHttpServletResponse();
            AuthenticationException authException = mock(AuthenticationException.class);

            // jsonMapperの変換結果のスタブ化
            String mockJson = "{\"code\":\"UNAUTHORIZED\"}";
            when(jsonMapper.writeValueAsString(any(ApiResponse.class))).thenReturn(mockJson);

            // 対象の実行
            customAuthenticationEntryPoint.commence(request, response, authException);

            // レスポンスの検証（403, application/json, UTF-8, レスポンスボディ）
            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
            assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
            assertThat(response.getContentAsString()).isEqualTo(mockJson);

            // 書き込まれたレスポンスの検証
            verify(jsonMapper).writeValueAsString(argThat((ApiResponse body) ->
                ErrorCode.UNAUTHORIZED.getCode().equals(body.getCode())
                    && ErrorCode.UNAUTHORIZED.getMessage().equals(body.getMessage())));
        }
    }
}

