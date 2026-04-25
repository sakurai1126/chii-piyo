package link.s_repo.chii_piyo.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * エラーコードの列挙型
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR("VALIDATION_ERROR", "入力値が不正です"),
    UNAUTHORIZED("UNAUTHORIZED", "認証が必要です"),
    FORBIDDEN("FORBIDDEN", "権限がありません"),
    NOT_FOUND("NOT_FOUND", "リソースが見つかりません"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "サーバーエラーが発生しました");

    private final String code;
    private final String message;
}
