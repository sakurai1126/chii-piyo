package link.s_repo.chii_piyo.common;

import lombok.extern.slf4j.Slf4j;
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
     * バリデーションエラー
     *
     * @param e バリデーションエラー時の例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException e) {

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
