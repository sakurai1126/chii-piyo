package link.s_repo.chii_piyo.common;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * S3キー生成処理用コンポーネント
 */
@Component
public class S3KeyGenerator {
    // S3キー生成時に使用する日付フォーマット
    private static final DateTimeFormatter S3_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * S3キーを構築する<br>
     * 形式: {prefix}/yyyy/MM/dd/{UUID}_{元のファイル名}
     *
     * @param originalFilename 元のファイル名
     * @return S3キー
     */
    public String buildS3Key(String prefix, String originalFilename) {
        String today = LocalDate.now().format(S3_DATE_FORMAT);
        // 同一名でファイルが衝突しないようUUIDを付与
        String uniqueId = UUID.randomUUID().toString();
        // 元のファイル名は安全のためサニタイズ
        String safeName = sanitizeFilename(originalFilename);
        return String.format("%s/%s/%s_%s", prefix, today, uniqueId, safeName);
    }

    /**
     * ファイル名をS3キー用にサニタイズする<br>
     * パス区切り文字や制御文字を除去する
     *
     * @param filename ファイル名
     * @return サニタイズ後のファイル名
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unknown";
        }
        // パス区切り文字をアンダースコアに置換し、安全な文字のみを許可
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
