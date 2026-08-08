package link.s_repo.chii_piyo.common;

import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Ex-01: ResourceNotFoundExceptionが発生した場合、404エラーレスポンスが返ること")
    void handleResourceNotFound() {
        // 例外インスタンスの作成
        ResourceNotFoundException exception = new ResourceNotFoundException("該当データがありません");

        // 共通例外のハンドラーメソッドを呼び出し
        ResponseEntity<ApiResponse> response = handler.handleResourceNotFound(exception);

        // レスポンスの検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.NOT_FOUND.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("Ex-02: ResourceAccessDeniedExceptionが発生した場合、403エラーレスポンスが返ること")
    void handleResourceAccessDenied() {
        // 例外インスタンスの作成
        ResourceAccessDeniedException exception = new ResourceAccessDeniedException("リソースへのアクセス権限がありません");

        // 共通例外のハンドラーメソッドを呼び出し
        ResponseEntity<ApiResponse> response = handler.handleResourceAccessDenied(exception);

        // レスポンスの検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.FORBIDDEN.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("Ex-03: IllegalArgumentExceptionが発生した場合、400エラーレスポンスが返ること")
    void handleIllegalArgument() {
        // 例外インスタンスの作成
        String message = "不正なパラメータです";
        IllegalArgumentException exception = new IllegalArgumentException(message);

        // 共通例外のハンドラーメソッドを呼び出し
        ResponseEntity<ApiResponse> response = handler.handleIllegalArgument(exception);

        // レスポンスの検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Ex-04: バリデーションエラーが発生した場合、400エラーレスポンスと独自メッセージが返ること")
    void handleMethodArgumentNotValid() {
        // バリデーションエラー情報のモック作成
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        String message1 = "タイトルは必須です";
        String message2 = "件数は1以上を指定してください";

        FieldError fieldError1 = new FieldError("requestDto", "title", message1);
        FieldError fieldError2 = new FieldError("requestDto", "count", message2);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // メソッドの引数用データの作成
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        // 対象の実行
        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(exception, headers, status, request);

        // 結果の検証
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        ApiResponse body = (ApiResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo(ErrorCode.VALIDATION_ERROR.getCode());

        // メッセージがカンマ区切りで結合されていることの確認
        assertThat(body.getMessage()).isEqualTo(message1 + ", " + message2);
    }

    @Test
    @DisplayName("Ex-05: Spring Securityの権限エラーが発生した場合、403エラーレスポンスが返ること")
    void handleAccessDeniedException() {
        // 例外インスタンスの作成
        AccessDeniedException exception = new AccessDeniedException("未認証です");

        // 共通例外のハンドラーメソッドを呼び出し
        ResponseEntity<ApiResponse> response = handler.handleAccessDeniedException(exception);

        // レスポンスの検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.FORBIDDEN.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("Ex-06: 予期しないエラーが発生した場合、500エラーレスポンスが返ること")
    void handleException() {
        // 例外インスタンスの作成
        Exception exception = new Exception("予期しないエラーが発生しました");

        // 共通例外のハンドラーメソッドを呼び出し
        ResponseEntity<ApiResponse> response = handler.handleException(exception);

        // レスポンスの検証
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    @Test
    @DisplayName("Ex-07: その他のSpring MVC標準例外が発生した場合、500にならずにエラーレスポンスが返ること")
    void handleExceptionInternal() {
        // 例外および引数データの作成
        String errorMessage = "許可されていないHTTPメソッドです";
        Exception exception = new Exception(errorMessage);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.METHOD_NOT_ALLOWED;
        WebRequest request = mock(WebRequest.class);

        // 対象の実行
        ResponseEntity<Object> response = handler.handleExceptionInternal(
            exception, null, headers, status, request);

        // 結果の検証
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        ApiResponse body = (ApiResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()));
        assertThat(body.getMessage()).isEqualTo(errorMessage);
    }
}
