package link.s_repo.chii_piyo.exception;

/**
 * リソースへのアクセス権がない場合の例外<br>
 * GlobalExceptionHandlerを通じて403エラーとして処理される
 */
public class ResourceAccessDeniedException extends RuntimeException {
    public ResourceAccessDeniedException(String message) {
        super(message);
    }
}
