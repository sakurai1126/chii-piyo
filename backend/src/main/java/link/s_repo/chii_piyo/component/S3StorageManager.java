package link.s_repo.chii_piyo.component;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;


import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class S3StorageManager {
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
    public URI generateUploadPresignedUrl(String s3Key, String contentType) {
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
        return java.net.URI.create(
            s3Presigner.presignPutObject(presignRequest).url().toString()
        );
    }

    /**
     * ダウンロード用Pre-signed URLを発行する
     * 有効期限は60分
     *
     * @param s3Key    S3オブジェクトのキー
     * @param fileName ダウンロード時に保存されるファイル名
     * @return 署名付きURL文字列
     */
    public URI generateDownloadPresignedUrl(String s3Key, @Nullable String fileName) {
        // s3Keyのバリデーション
        if (!StringUtils.hasText(s3Key)) {
            throw new IllegalArgumentException("s3Keyがnullまたは空です");
        }

        // ダウンロード用のGetObjectRequestを作成
        GetObjectRequest.Builder requestBuilder = GetObjectRequest.builder()
            .bucket(s3Bucket)
            .key(s3Key);

        if (fileName != null) {
            // 日本語ファイル名でも文字化けしないようエンコードしてダウンロード用ヘッダーを設定
            String contentDisposition = org.springframework.http.ContentDisposition.attachment()
                .filename(fileName, java.nio.charset.StandardCharsets.UTF_8)
                .build()
                .toString();
            requestBuilder.responseContentDisposition(contentDisposition);
        }

        // GetObjectRequestを元に、署名付きURLを生成するためのGetObjectPresignRequestを作成
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60)) // 有効期限設定
            .getObjectRequest(requestBuilder.build())
            .build();

        // S3Presignerを使用して署名付きURLを生成し、URLをURIに変換して返す
        try {
            return s3Presigner
                .presignGetObject(presignRequest)
                .url().toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("URIの生成に失敗しました", e);
        } catch (SdkException e) {
            throw e; // SdkException はそのまま伝播
        }
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

    /**
     * S3から複数のオブジェクトを一括削除する
     *
     * @param s3Keys S3オブジェクトキーのリスト
     */
    public void deleteObjects(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }

        // S3キーのリストをAWS処理用の型(ObjectIdentifier)に変換
        List<ObjectIdentifier> identifiers = s3Keys.stream()
            .filter(StringUtils::hasText) // 空文字やnullを除外
            .map(key -> ObjectIdentifier.builder().key(key).build())
            .collect(Collectors.toList());

        if (identifiers.isEmpty()) {
            return;
        }

        // 削除対象のリストをDeleteオブジェクトで生成
        Delete delete = Delete.builder()
            .objects(identifiers)
            .build();

        // 上記生成のDeleteオブジェクトを使って削除処理リクエストを作成
        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(s3Bucket)
            .delete(delete)
            .build();

        // 削除処理リクエストを送信
        s3Client.deleteObjects(deleteObjectsRequest);
    }
}

