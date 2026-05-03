package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.repository.gen.UsersMapper;
import link.s_repo.chii_piyo.model.gen.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport.cognitoUserId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * ユーザー同期処理サービス<br>
 * Cognito認証成功後、DBにユーザーが存在しない場合は自動作成する
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UsersMapper usersMapper;

    /**
     * CognitoのユーザーIDでユーザーを検索し、存在しない場合は作成する
     *
     * @param cognitoUserIdValue CognitoユーザーID
     * @param email メールアドレス
     * @return ユーザー情報
     */
    @Transactional
    public Users findOrCreateByCognitoUserId(String cognitoUserIdValue, String email) {
        return usersMapper.selectOne(
            // cognito_user_idカラムから引数で受け取ったcognitoUserIdValueと一致するユーザーを検索
            c -> c.where(cognitoUserId, isEqualTo(cognitoUserIdValue))
        ).orElseGet(() -> createUser(cognitoUserIdValue, email));
    }

    /**
     * 新しいユーザーを作成してDBに保存しユーザー情報を返却する
     *
     * @param cognitoUserIdValue Cognitoから渡されたユーザーID
     * @param email メールアドレス
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
        user.setRole("VIEWER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // DBに保存
        usersMapper.insert(user);
        return user;
    }
}
