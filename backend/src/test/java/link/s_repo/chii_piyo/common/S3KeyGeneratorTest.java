package link.s_repo.chii_piyo.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class S3KeyGeneratorTest {

    private final S3KeyGenerator s3KeyGenerator = new S3KeyGenerator();
    // S3キー生成時に使用する日付フォーマット
    private static final DateTimeFormatter S3_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    @Nested
    @DisplayName("buildS3Key - S3キー生成処理")
    class BuildS3Key {
        // 共通リクエストデータの作成
        String requestPrefix = "media";

        @Test
        @DisplayName("Util-01: S3キーの生成ができること")
        void buildS3Key_success() {
            // リクエストデータの作成
            String requestFileName = "image.png";

            // S3キーの生成
            String result = s3KeyGenerator.buildS3Key(requestPrefix, requestFileName);

            // 結果の検証
            String today = LocalDate.now().format(S3_DATE_FORMAT);

            // prefix + 今日の日付から始まりファイル名で終わることを検証
            assertThat(result).startsWith(requestPrefix + "/" + today + "/");
            assertThat(result).endsWith("_" + requestFileName);
        }

        @Test
        @DisplayName("Util-02: ファイルネームを指定しないとunknownに変換されて生成されること")
        void buildS3Key_noFileName() {
            // S3キーの生成
            String emptyResult = s3KeyGenerator.buildS3Key(requestPrefix, "");
            String nullResult = s3KeyGenerator.buildS3Key(requestPrefix, null);

            // 結果の検証
            assertThat(emptyResult).endsWith("_unknown");
            assertThat(nullResult).endsWith("_unknown");
        }

        @Test
        @DisplayName("Util-03: 日本語やパス区切り文字を含むファイル名を渡した場合サニタイズされること")
        void buildS3Key_sanitize() {
            // リクエストデータの作成
            String requestFileName = "日本語/\"#$&.png";

            // S3キーの生成
            String result = s3KeyGenerator.buildS3Key(requestPrefix, requestFileName);

            // 結果の検証
            String today = LocalDate.now().format(S3_DATE_FORMAT);

            // prefix + 今日の日付から始まりファイル名で終わることを検証
            assertThat(result).startsWith(requestPrefix + "/" + today + "/");
            assertThat(result).endsWith("_" + "________.png");
        }
    }
}
