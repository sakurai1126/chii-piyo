package link.s_repo.chii_piyo.exception;

/**
 * メディアが見つからない場合の例外<br>
 * GlobalExceptionHandlerを通じて404エラーとして処理される
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
