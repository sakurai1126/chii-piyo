package link.s_repo.chii_piyo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

/**
 * AWS S3クライアントとS3Presignerの設定クラス
 * application.yamlからAWSの認証情報とリージョンを読み込み、S3ClientとS3Presignerを構築する
 */
@Configuration
public class S3Config {
    // application.yamlからAWSの認証情報とリージョンを読込
    @Value("${aws.s3.credentials.access-key}")
    private String accessKey;
    @Value("${aws.s3.credentials.secret-key}")
    private String secretKey;
    @Value("${aws.s3.region}")
    private String region;

    /**
     * リージョンと認証情報を設定したS3Clientをビルドする
     *
     * @return 構築したS3Clientオブジェクト
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(region))
            // AWSに対する認証方法を指定
            .credentialsProvider(
                // アクセスキーとシークレットキーを用いて認証情報を作成し、StaticCredentialsProviderで提供
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            // 未完了アップロードの削除はS3削除中も行ロックを保持するため、S3が無応答でもリトライを含めて5分で打ち切る。
            .overrideConfiguration(c -> c
                .apiCallAttemptTimeout(Duration.ofSeconds(60))
                .apiCallTimeout(Duration.ofMinutes(5))
            )
            .build();
    }

    /**
     * 署名付きURLを生成するためのS3Presignerビルドする
     *
     * @return 構築したs3Presignerオブジェクト
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )
            .build();
    }
}
