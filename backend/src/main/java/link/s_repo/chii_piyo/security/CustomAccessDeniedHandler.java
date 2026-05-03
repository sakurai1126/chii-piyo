package link.s_repo.chii_piyo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import link.s_repo.chii_piyo.common.ApiResponse;
import link.s_repo.chii_piyo.common.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 認可エラー時のハンドラー<br>
 * 権限不足時に本アプリケーション指定の共通ApiResponse形式で403を返す
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    /**
     * 認可失敗時の処理
     *
     * @param request HTTPリクエストオブジェクト
     * @param response HTTPレスポンスオブジェクト
     * @param accessDeniedException アクセス拒否の例外情報
     * @throws IOException レスポンスの書き込みに失敗した場合にスローされる例外
     */
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        log.warn("認可エラー path={} message={}", request.getRequestURI(), accessDeniedException.getMessage());

        // 403ステータス、Content-Typeをapplication/json、文字コードをUTF-8に設定
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 共通定義のApiResponse形式エラーレスポンスを作成
        ApiResponse<Void> body = ApiResponse.error(
            ErrorCode.FORBIDDEN.getCode(),
            ErrorCode.FORBIDDEN.getMessage()
        );

        // レスポンスボディにJSON形式でエラーレスポンスを書き込む
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
