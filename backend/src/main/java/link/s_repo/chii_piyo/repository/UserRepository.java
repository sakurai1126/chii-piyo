package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final UsersMapper usersMapper;

    /**
     * ユーザーをID指定で1件取得する
     *
     * @param id 対象のユーザーのID
     * @return ユーザーエンティティ
     */
    public Optional<Users> findById(Long id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    /**
     * ユーザーをIDリスト指定で複数件取得する
     *
     * @param ids 対象のユーザーのIDリスト
     * @return ユーザーエンティティリスト
     */
    public List<Users> findByIds(List<Long> ids) {
        return usersMapper.select(c -> c.where(UsersDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * ユーザーを保存する
     *
     * @param user ユーザーエンティティ
     */
    public void save(Users user) {
        usersMapper.insertSelective(user);
    }

    /**
     * ユーザーを更新する
     *
     * @param user ユーザーエンティティ
     */
    public void update(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * ユーザーを全件取得する
     *
     * @return ユーザーエンティティリスト
     */
    public List<Users> findAll() {
        return usersMapper.select(c -> c.orderBy(id));
    }

    /**
     * ユーザーをCognitoユーザーID指定で1件取得する
     *
     * @param cognitoUserId 対象のCognitoユーザーID
     * @return ユーザーエンティティ
     */
    public Optional<Users> findByCognitoUserId(String cognitoUserId) {
        return usersMapper.selectOne(
            c -> c.where(UsersDynamicSqlSupport.cognitoUserId, isEqualTo(cognitoUserId)));
    }
}
