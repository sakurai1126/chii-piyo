package link.s_repo.chii_piyo.exception;

/**
 * メディアへのアクセス権がない場合の例外<br>
 * GlobalExceptionHandlerを通じて403エラーとして処理される
 */
public class MediaAccessDeniedException extends RuntimeException {
    public MediaAccessDeniedException(String message) {
        super(message);
    }
}
