package link.s_repo.chii_piyo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;


import java.io.InputStream;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    // 署名付きURL生成用オブジェクト
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    // application.yamlからS3バケット名を読込
    @Value("${aws.s3.bucket}")
    private String s3Bucket;

    /**
     * アップロード用Pre-signed URLを発行する
     * 有効期限は15分
     *
     * @param s3Key       S3オブジェクトのキー
     * @param contentType アップロードするオブジェクトのContent-Type
     * @return 署名付きURL文字列
     */
    public String generateUploadPresignedUrl(String s3Key, String contentType) {
        // s3KeyとcontentTypeのバリデーション
        if (!StringUtils.hasText(s3Key)) {
            throw new IllegalArgumentException("s3Keyがnullまたは空です");
        }
        if (!StringUtils.hasText(contentType)) {
            throw new IllegalArgumentException("contentTypeがnullまたは空です");
        }

        // アップロード用のPutObjectRequestを作成
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(s3Bucket)
            .key(s3Key)
            .contentType(contentType)
            .build();

        // PutObjectRequestを元に、署名付きURLを生成するためのPutObjectPresignRequestを作成
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15)) // 有効期限設定
            .putObjectRequest(objectRequest)
            .build();

        // S3Presignerを使用して署名付きURLを生成し、URL文字列を返す
        return s3Presigner
            .presignPutObject(presignRequest)
            .url()
            .toString();
    }

    /**
     * ダウンロード用Pre-signed URLを発行する
     * 有効期限は60分
     *
     * @param s3Key S3オブジェクトのキー
     * @return 署名付きURL文字列
     */
    public String generateDownloadPresignedUrl(String s3Key) {
        // s3Keyのバリデーション
        if (!StringUtils.hasText(s3Key)) {
            throw new IllegalArgumentException("s3Keyがnullまたは空です");
        }

        // ダウンロード用のGetObjectRequestを作成
        GetObjectRequest objectRequest = GetObjectRequest.builder()
            .bucket(s3Bucket)
            .key(s3Key)
            .build();

        // GetObjectRequestを元に、署名付きURLを生成するためのGetObjectPresignRequestを作成
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60)) // 有効期限設定
            .getObjectRequest(objectRequest)
            .build();

        // S3Presignerを使用して署名付きURLを生成し、URL文字列を返す
        return s3Presigner
            .presignGetObject(presignRequest)
            .url()
            .toString();
    }

    /**
     * ストリームとしてS3オブジェクトをダウンロードする<br>
     * サムネイル生成のためバックエンド内部から呼び出される
     *
     * @param s3Key S3オブジェクトキー
     * @return オブジェクトのInputStream
     */
    public InputStream downloadAsStream(String s3Key) {
        if (!StringUtils.hasText(s3Key)) {
            throw new IllegalArgumentException("s3Keyがnullまたは空です");
        }
        GetObjectRequest req = GetObjectRequest.builder().bucket(s3Bucket).key(s3Key).build();
        return s3Client.getObject(req);
    }

    /**
     * サムネイルバイト列をS3バケットにアップロードする
     *
     * @param thumbnailS3Key サムネイルのS3キー
     * @param data           サムネイルのバイト列
     */
    public void uploadThumbnail(String thumbnailS3Key, byte[] data) {
        if (!StringUtils.hasText(thumbnailS3Key)) {
            throw new IllegalArgumentException("thumbnailS3Keyがnullまたは空です");
        }
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(s3Bucket)
            .key(thumbnailS3Key)
            .contentType("image/jpeg")
            .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(data));
    }

}
