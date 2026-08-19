package link.s_repo.chii_piyo.component;

import link.s_repo.chii_piyo.repository.UserRepository;
import link.s_repo.chii_piyo.model.gen.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * ユーザー同期処理コンポーネント<br>
 * Cognito認証成功後、DBにユーザーが存在しない場合は自動作成する
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSyncComponent {
    private final UserRepository userRepository;

    @Value("${app.admin-email:}")
    private String adminEmail;

    /**
     * CognitoのユーザーIDでユーザーを検索し、存在しない場合は作成する
     *
     * @param cognitoUserId CognitoユーザーID
     * @param email         メールアドレス
     * @return ユーザー情報
     */
    @Transactional
    public Users findOrCreateByCognitoUserId(String cognitoUserId, String email) {
        return userRepository.findByCognitoUserId(cognitoUserId)
            .orElseGet(() -> createUser(cognitoUserId, email));
    }

    /**
     * 新しいユーザーを作成してDBに保存しユーザー情報を返却する
     *
     * @param cognitoUserIdValue Cognitoから渡されたユーザーID
     * @param email              メールアドレス
     * @return ユーザー情報
     */
    private Users createUser(String cognitoUserIdValue, String email) {
        Users user = new Users();

        // 受け取ったユーザー情報をセット
        user.setCognitoUserId(cognitoUserIdValue);
        user.setEmail(email);
        user.setDisplayName(email);

        // 他の値はDDLのデフォルト値と同じものを明示的にセットする
        user.setIsDarkMode(false);
        user.setIsEasyMode(false);
        if (StringUtils.hasText(adminEmail) && adminEmail.equalsIgnoreCase(email)) {
            user.setRole("ADMIN");
            log.info("管理者ユーザーを初期作成しました: email={}", email);
        } else {
            user.setRole("VIEWER");
        }

        // DBに保存
        userRepository.save(user);
        return user;
    }
}
