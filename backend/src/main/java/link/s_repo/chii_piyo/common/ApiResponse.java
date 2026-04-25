package link.s_repo.chii_piyo.common;

import lombok.Builder;
import lombok.Getter;

/**
 * APIレスポンスの共通フォーマットを定義するクラス
 *
 * @param <T> レスポンスのデータ型
 */
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String code;
    private final String message;

    /**
     * 成功応答生成メソッド<br>
     * 成功フラグはtrueに設定され、受け取ったデータが含まれる
     *
     * @param data レスポンスデータ
     * @param <T>  レスポンスのデータ型
     * @return 成功応答を含むApiResponseオブジェクト
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .build();
    }

    /**
     * エラー応答生成メソッド<br>
     * 成功フラグはfalseに設定され、アプリケーションエラーコードとメッセージが含まれる
     *
     * @param code    アプリケーションエラーコード
     * @param message エラーメッセージ
     * @return エラー応答を含むApiResponseオブジェクト
     */
    public static ApiResponse<Void> error(String code, String message) {
        return ApiResponse.<Void>builder()
            .success(false)
            .code(code)
            .message(message)
            .build();
    }
}
