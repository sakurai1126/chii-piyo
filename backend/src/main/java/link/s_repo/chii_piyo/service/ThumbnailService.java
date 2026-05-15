package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.repository.MediaUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * サムネイル生成を非同期で実行するサービス<br>
 * MediaService.updateUploadStatus から呼ばれ、リクエストとは別スレッドで動作する
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final S3Service s3Service;
    private final MediaUpdateRepository mediaUpdateRepository;

    @Value("${thumbnail.max-edge-pixels}")
    private int maxEdgePixels;

    @Value("${thumbnail.jpeg-quality}")
    private double jpegQuality;

    @Value("${thumbnail.video-frame-second}")
    private int videoFrameSecond;

    @Value("${thumbnail.ffmpeg-path}")
    private String ffmpegPath;

    @Value("${thumbnail.max-image-bytes}")
    private long maxImageBytes;

    @Value("${thumbnail.max-video-bytes}")
    private long maxVideoBytes;

    private static final DateTimeFormatter S3_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * サムネイルを生成してS3にアップロードし、Mediaレコードのthumbnail_s3_keyを更新する<br>
     * 例外が発生してもリクエストには影響させないため、例外はキャッチしてログ出力する<br>
     * Asyncアノテーションにより、非同期処理にて実行される<br>
     * S3からはInputStreamで取得しヒープへの全展開を避ける
     *
     * @param mediaId          対象のメディアID
     * @param mediaType        対象の種類（PHOTO/VIDEO）
     * @param s3Key            オリジナルのS3キー
     * @param originalFilename ファイル名
     * @param fileSize         ファイルサイズ（バイト単位）
     */
    @Async
    public void generateThumbnailAsync(
        Long mediaId,
        String mediaType,
        String s3Key,
        String originalFilename,
        Long fileSize
    ) {
        if (fileSize != null) {
            if ("PHOTO".equals(mediaType) && fileSize > maxImageBytes) {
                log.warn("画像ファイルサイズが上限を超えているためスキップ mediaId={} size={}", mediaId, fileSize);
                return;
            } else if ("VIDEO".equals(mediaType) && fileSize > maxVideoBytes) {
                log.warn("動画ファイルサイズが上限を超えているためスキップ mediaId={} size={}", mediaId, fileSize);
                return;
            }
        }

        try {
            byte[] thumbnail;

            // S3からInputStreamを取得し、mediaTypeに応じて画像または動画のサムネイルを生成する
            try (InputStream source = s3Service.downloadAsStream(s3Key)) {
                thumbnail = switch (mediaType) {
                    case "PHOTO" -> generateFromImage(source);
                    case "VIDEO" -> generateFromVideo(source);
                    default ->
                        throw new IllegalArgumentException("未対応のmediaTypeです mediaType=" + mediaType);
                };
            }

            // サムネイルをS3にアップロードし、MediaレコードのサムネイルS3キーのカラムを更新する
            String thumbnailS3Key = buildThumbnailS3Key(originalFilename);
            s3Service.uploadThumbnail(thumbnailS3Key, thumbnail);
            mediaUpdateRepository.updateThumbnailKey(mediaId, thumbnailS3Key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("サムネイル生成が中断されました mediaId={}", mediaId, e);
        } catch (Exception e) {
            log.error("サムネイル生成失敗 mediaId={}", mediaId, e);
        }
    }

    /**
     * サムネイル用S3キーを構築する
     */
    private String buildThumbnailS3Key(String originalFilename) {
        // 日付プレフィックスを生成する
        String today = LocalDate.now().format(S3_DATE_FORMAT);
        // UUIDを生成してファイル名の衝突を防止する
        String uniqueId = UUID.randomUUID().toString();
        // ファイル名をサニタイズして拡張子を除去した安全な名前を生成する
        String safeName = sanitizeFilename(stripExtension(originalFilename));
        // サムネイル用のS3キーを "thumbnails/yyyy/MM/dd/uuid_safeName.jpg" の形式で生成する
        return String.format("thumbnails/%s/%s_%s.jpg", today, uniqueId, safeName);
    }

    /**
     * ファイル名から拡張子を除去する<br>
     * 例: "example.jpg" -> "example"
     */
    private String stripExtension(String filename) {
        if (filename == null) return "unknown";
        // 拡張子を除去してファイル名だけを取り出す
        int dot = filename.lastIndexOf('.');
        // 拡張子なしのファイル名を返す※ドットが存在しない場合は元のファイル名を返す
        return dot > 0 ? filename.substring(0, dot) : filename;
    }

    /**
     * ファイル名をS3キー用にサニタイズする
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) return "unknown";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }


    /**
     * 画像InputStreamからJPEGサムネイルを生成する
     *
     * @param source オリジナル画像のInputStream
     * @return JPEG形式のサムネイルバイト列
     */
    private byte[] generateFromImage(InputStream source) throws IOException {
        // サムネイル生成のための出力ストリームを用意する
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // application.yamlで指定した値を元にJPEGのサムネイル画像を生成
        Thumbnails.of(source)
            .size(maxEdgePixels, maxEdgePixels)
            .crop(Positions.CENTER)
            .outputFormat("jpg")
            .outputQuality(jpegQuality)
            .toOutputStream(output);

        return output.toByteArray();
    }

    /**
     * 動画InputStreamから1フレーム抽出しJPEGサムネイルを生成する<br>
     * ffmpegを呼ぶため一時ファイルに書き出してから処理する
     *
     * @param source オリジナル動画のInputStream
     * @return JPEG形式のサムネイルバイト列
     */
    private byte[] generateFromVideo(InputStream source) throws IOException, InterruptedException {
        // 動画の一時ファイルを作成する
        Path tmpInput = Files.createTempFile("thumb-src-", ".tmp");
        // 抽出した画像の一時ファイルを作成する
        Path tmpOutput = Files.createTempFile("thumb-out-", ".jpg");

        try (ByteArrayOutputStream resized = new ByteArrayOutputStream()) {

            // ffmpegはストリームを直接処理できないため動画のInputStreamを一時ファイルに書き出す
            Files.copy(source, tmpInput, StandardCopyOption.REPLACE_EXISTING);

            // FFmpegを呼び出すためのProcessBuilderを構築する
            ProcessBuilder pb = processBuilder(tmpInput, tmpOutput);

            // フリーズ防止のために標準出力とエラー出力を統合し実行開始
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // FFmpegは大量の出力をするためreadAllBytesで読み捨てながら完了を待つ
            try (var processStream = process.getInputStream()) {
                processStream.readAllBytes();
            }

            // 最大60秒間待機しても終了しない場合は強制終了する
            boolean finished = process.waitFor(60, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly(); // 強制終了
                throw new IOException("ffmpegがタイムアウトしました");
            }

            // 何らかの失敗でffmpegが異常終了した場合は例外を投げる
            if (process.exitValue() != 0) {
                throw new IOException("ffmpegが異常終了しました exitCode=" + process.exitValue());
            }

            // ffmpegが出力ファイルを生成しなかった場合も例外を投げる
            if (Files.size(tmpOutput) == 0) {
                throw new IOException("ffmpegが出力ファイルを生成しませんでした");
            }

            // application.yamlで指定した値を元にJPEGのサムネイル画像を生成
            Thumbnails.of(tmpOutput.toFile())
                .size(maxEdgePixels, maxEdgePixels)
                .crop(Positions.CENTER)
                .outputFormat("jpg")
                .outputQuality(jpegQuality)
                .toOutputStream(resized);

            return resized.toByteArray();

        } finally {
            // 一時ファイルの削除
            Files.deleteIfExists(tmpInput);
            Files.deleteIfExists(tmpOutput);
        }
    }

    /**
     * FFmpeg（動画処理）を呼び出す<br>
     * -y: 出力ファイルが既に存在する場合、上書きを確認せずに実行する<br>
     * -ss: application.yamlにて指定した秒数の位置を指定<br>
     * -i: 入力ファイルを指定<br>
     * -vframes: 1フレームだけ抽出することを指定<br>
     * -q:v: JPEGの品質を指定（1が最高品質、31が最低品質）<br>
     *
     * @param input  入力ファイル
     * @param output 出力ファイル
     * @return 設定を構築したProcessBuilderインスタンス
     */
    private ProcessBuilder processBuilder(Path input, Path output) {
        return new ProcessBuilder(
            ffmpegPath,
            "-y",
            "-ss", String.valueOf(videoFrameSecond),
            "-i", input.toString(),
            "-vframes", "1",
            "-q:v", "2",
            output.toString()
        );
    }
}
