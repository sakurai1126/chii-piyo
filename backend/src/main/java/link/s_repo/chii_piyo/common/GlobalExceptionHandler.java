package link.s_repo.chii_piyo.common;

import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;


import java.util.stream.Collectors;

/**
 * 共通例外ハンドラー<br>
 * コントローラーで発生した例外をキャッチし、適切なHTTPレスポンスを返すためのクラス
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * リソースが見つからない場合のエラー<br>
     * ログにエラー内容を出力し、404エラーとして処理する
     *
     * @param exception ResourceNotFoundException
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        log.warn("リソースが見つかりません: {}", exception.getMessage());
        return ResponseEntity
            // notFound()はbodyを構築できないためステータスを自分で設定し共通エラーコードを使用したレスポンスを返す
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(
                ErrorCode.NOT_FOUND.getCode(),
                ErrorCode.NOT_FOUND.getMessage()));
    }

    /**
     * リソースへのアクセス権がない場合のエラー<br>
     * ログにエラー内容を出力し、403エラーとして処理する
     *
     * @param exception ResourceAccessDeniedException
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleResourceAccessDenied(
        ResourceAccessDeniedException exception) {
        log.warn("リソースへのアクセス拒否: {}", exception.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(
                ErrorCode.FORBIDDEN.getCode(),
                ErrorCode.FORBIDDEN.getMessage()));
    }

    /**
     * 引数エラーなどの不正なリクエスト<br>
     * ログにエラー内容を出力し、400エラーとして処理する
     *
     * @param exception IllegalArgumentException
     * @return エラーレスポンス
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("不正なリクエスト: {}", exception.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_ERROR.getCode(),
                exception.getMessage()));
    }

    /**
     * バリデーションエラー<br>
     * バリデーションエラーのメッセージをカンマ区切りで結合し、400エラーとして処理する
     *
     * @param exception      MethodArgumentNotValidException
     * @param headers HTTPヘッダー
     * @param status  HTTPステータス
     * @param request Webリクエスト
     * @return エラーレスポンス
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_ERROR.getCode(),
                message));
    }

    /**
     * Spring Security の権限エラー<br>
     * ログにエラー内容を出力し、403エラーとして処理する
     *
     * @param exception AccessDeniedException
     * @return エラーレスポンス
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(
        AccessDeniedException exception) {
        log.warn("アクセス権限がありません: {}", exception.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(
                ErrorCode.FORBIDDEN.getCode(),
                ErrorCode.FORBIDDEN.getMessage()));
    }

    /**
     * 予期しないエラー<br>
     * ログにエラー内容を出力し、500内部サーバーエラーとして処理する
     *
     * @param exception 発生した例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception exception) {
        log.error("予期しないエラーが発生しました", exception);
        // 共通エラーコードを使用して、内部サーバーエラーのレスポンスを返す
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    /**
     * その他のSpring MVC標準例外<br>
     * 500エラーに吸収されないよう実装
     *
     * @param exception         発生した例外
     * @param body       レスポンスボディ
     * @param headers    HTTPヘッダー
     * @param statusCode HTTPステータスコード
     * @param request    Webリクエスト
     * @return エラーレスポンス
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception exception,
        @Nullable Object body,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode statusCode,
        @NonNull WebRequest request) {
        log.warn("Spring MVC標準例外が発生しました: {}", exception.getMessage());
        // HTTPステータスコードを文字列化してエラーコードとして利用する
        return ResponseEntity
            .status(statusCode)
            .body(ApiResponse.error(
                String.valueOf(statusCode.value()),
                exception.getMessage()));
    }
}
