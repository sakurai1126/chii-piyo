package link.s_repo.chii_piyo.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import link.s_repo.chii_piyo.common.ApiResponse;
import link.s_repo.chii_piyo.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * 認証エラー時のエントリーポイント<br>
 * JWT検証失敗時に共通定義のApiResponse形式で401を返す
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JsonMapper jsonMapper;

    /**
     * 認証失敗時の処理
     *
     * @param request       HTTPリクエストオブジェクト
     * @param response      HTTPレスポンスオブジェクト
     * @param authException 認証失敗の例外情報
     * @throws IOException レスポンスの書き込みに失敗した場合にスローされる例外
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        log.warn("認証エラー path={} message={}", request.getRequestURI(), authException.getMessage());

        // 401ステータス、Content-Typeをapplication/json、文字コードをUTF-8に設定
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 共通定義のApiResponse形式エラーレスポンスを作成
        ApiResponse<Void> body = ApiResponse.error(
            ErrorCode.UNAUTHORIZED.getCode(),
            ErrorCode.UNAUTHORIZED.getMessage()
        );

        // レスポンスボディにJSON形式でエラーレスポンスを書き込む
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }
}
