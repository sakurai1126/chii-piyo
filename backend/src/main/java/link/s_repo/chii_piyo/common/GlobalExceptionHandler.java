package link.s_repo.chii_piyo.common;

import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param e ResourceNotFoundException
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("リソースが見つかりません: {}", e.getMessage());
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
     * @param e ResourceAccessDeniedException
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaAccessDenied(
        ResourceAccessDeniedException e) {
        log.warn("リソースへのアクセス拒否: {}", e.getMessage());
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
     * @param e IllegalArgumentException
     * @return エラーレスポンス
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("不正なリクエスト: {}", e.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_ERROR.getCode(),
                e.getMessage()));
    }

    /**
     * バリデーションエラー<br>
     * バリデーションエラーのメッセージをカンマ区切りで結合し、400エラーとして処理する
     *
     * @param ex      MethodArgumentNotValidException
     * @param headers HTTPヘッダー
     * @param status  HTTPステータス
     * @param request Webリクエスト
     * @return エラーレスポンス
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        String message = ex.getBindingResult()
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
     * @param e AccessDeniedException
     * @return エラーレスポンス
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
        org.springframework.security.access.AccessDeniedException e) {
        log.warn("アクセス権限がありません: {}", e.getMessage());
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
     * @param e 発生した例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("予期しないエラーが発生しました", e);
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
     * @param ex         発生した例外
     * @param body       レスポンスボディ
     * @param headers    HTTPヘッダー
     * @param statusCode HTTPステータスコード
     * @param request    Webリクエスト
     * @return エラーレスポンス
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception ex,
        @Nullable Object body,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode statusCode,
        @NonNull WebRequest request) {
        log.warn("Spring MVC標準例外が発生しました: {}", ex.getMessage());
        // HTTPステータスコードを文字列化してエラーコードとして利用する
        return ResponseEntity
            .status(statusCode)
            .body(ApiResponse.error(
                String.valueOf(statusCode.value()),
                ex.getMessage()));
    }
}
