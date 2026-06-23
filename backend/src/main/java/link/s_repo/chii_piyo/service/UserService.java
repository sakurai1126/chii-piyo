package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.common.S3KeyGenerator;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.UserUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.List;


/**
 * ユーザー管理サービス<br>
 * ユーザーの取得・作成およびメディアとのユーザー紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3KeyGenerator s3KeyGenerator;
    private final S3StorageManager s3StorageManager;

    /**
     * ユーザーをIDで１件絞り込み
     *
     * @param id 対象のユーザーID
     * @return ユーザー情報
     */
    public Users getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));
    }

    /**
     * ユーザーをIDリストで複数件絞り込み
     *
     * @param ids 対象となるユーザーIDのリスト
     * @return ユーザー情報のリスト
     */
    public List<Users> getUsersById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return userRepository.findByIds(ids);
    }

    /**
     * プロフィールのメタデータを更新する
     *
     * @param userId     ユーザーデータ
     * @param updateData アップデート情報
     * @return 更新されたユーザーデータ
     */
    public Users updateMe(Long userId, UserUpdateRequestDto updateData) {
        // 現在のユーザー情報を取得
        Users user = getUserById(userId);
        boolean isUpdated = false;
        // リクエストに含まれている項目のみエンティティを書き換える
        if (updateData.getDisplayName() != null) {
            user.setDisplayName(updateData.getDisplayName());
            isUpdated = true;
        }

        if (updateData.getS3key() != null && updateData.getS3key().startsWith("profile/")) {
            user.setUserIconKey(updateData.getS3key());
            isUpdated = true;
        }

        if (updateData.getIsDarkMode() != null) {
            user.setIsDarkMode(updateData.getIsDarkMode());
            isUpdated = true;
        }

        if (updateData.getIsEasyMode() != null) {
            user.setIsEasyMode(updateData.getIsEasyMode());
            isUpdated = true;
        }

        // 何らかの変更があった場合のみDBを更新
        if (isUpdated) {
            // 更新日時をUTCでセット
            user.setUpdatedAt(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
            // Selectiveメソッドを使って、null以外の項目のみUPDATE実行
            userRepository.update(user);
        }
        return user;
    }


    /**
     * プロフィールアイコンダウンロード用署名付きURLの生成
     *
     * @param user ユーザー情報
     * @return 生成した署名付きダウンロード用URL
     */
    public URI generateIconDownloadPresignedUrl(Users user) {
        String s3Key = user.getUserIconKey();
        if (s3Key == null || s3Key.isEmpty()) return null;
        return s3StorageManager.generateDownloadPresignedUrl(s3Key, null);
    }

    /**
     * プロフィールアイコン生成用署名付きURLの生成
     *
     * @param filename    ファイル名
     * @param contentType コンテンツタイプ
     * @return 生成した署名付きアップロード用URL
     */
    public CreateIconS3KeyResult generateIconPresignedUrl(String filename, String contentType) {
        // S3キーを生成
        String s3Key = s3KeyGenerator.buildS3Key("profile", filename);

        // 署名付きアップロードURLを発行
        URI presignedUrl = s3StorageManager.generateUploadPresignedUrl(s3Key, contentType);

        return new CreateIconS3KeyResult(s3Key, presignedUrl);
    }

    /**
     * ユーザー情報の一覧の取得と取得したユーザーからダウンロード用署名付きURLを生成し返却する
     *
     * @return ユーザー情報とダウンロード用署名付きURLをまとめたレコード型の一覧
     */
    public List<UsersAndIconResult> getUsersAndIcon() {
        // ユーザー情報を一覧取得
        List<Users> users = userRepository.findAll();

        // 取得下ユーザー情報から署名付きURLを取得して返却
        return users.stream().map(user -> {
                // ダウンロード用URLを生成
                URI presignedUrl = generateIconDownloadPresignedUrl(user);
                // レコードにまとめる
                return new UsersAndIconResult(user, presignedUrl);
            }).toList();
    }

    /**
     * ユーザー情報とダウンロード用署名付きURLをまとめて返すための内部クラス
     */
    public record UsersAndIconResult(Users user, URI presignedUrl) {
    }

    /**
     * S3Keyと署名付きURLをまとめて返すための内部クラス
     */
    public record CreateIconS3KeyResult(String s3Key, URI presignedUrl) {
    }

}
