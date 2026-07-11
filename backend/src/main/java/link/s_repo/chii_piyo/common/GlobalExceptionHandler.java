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

import java.util.stream.Collectors;

/**
 * 共通例外ハンドラー<br>
 * コントローラーで発生した例外をキャッチし、適切なHTTPレスポンスを返すためのクラス
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リソースが見つからない場合のエラー
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
     * リソースへのアクセス権がない場合のエラー
     * ログにエラー内容を出力し、403エラーとして処理する
     *
     * @param e ResourceAccessDeniedException
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaAccessDenied(ResourceAccessDeniedException e) {
        log.warn("リソースへのアクセス拒否: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(
                ErrorCode.FORBIDDEN.getCode(),
                ErrorCode.FORBIDDEN.getMessage()));
    }

    /**
     * 引数エラーなどの不正なリクエスト
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
     * バリデーションエラー
     *
     * @param e バリデーションエラー時の例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // バリデーションエラーのメッセージをカンマ区切りで結合
        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        // 共通エラーコードと結合したメッセージを使用して、バリデーションエラーのレスポンスを返す
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(
                ErrorCode.VALIDATION_ERROR.getCode(),
                message));
    }

    /**
     * 予期しないエラー
     * ログにエラー内容を出力し、内部サーバーエラーとして処理する
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
}
