package link.s_repo.chii_piyo.common;

import lombok.Builder;
import lombok.Getter;

/**
 * APIレスポンスの共通フォーマットを定義するクラス<br>
 * 成功時のレスポンスはOpenAPIの仕様に準じエラーレスポンスを共通化する
 */
@Getter
@Builder
public class ApiResponse {
    private final boolean success;
    private final String code;
    private final String message;

    /**
     * エラー応答生成メソッド<br>
     * 成功フラグはfalseに設定され、アプリケーションエラーコードとメッセージが含まれる
     *
     * @param code    アプリケーションエラーコード
     * @param message エラーメッセージ
     * @return エラー応答を含むApiResponseオブジェクト
     */
    public static ApiResponse error(String code, String message) {
        return ApiResponse.builder()
            .success(false)
            .code(code)
            .message(message)
            .build();
    }
}
