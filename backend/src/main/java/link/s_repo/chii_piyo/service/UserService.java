package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.gen.UsersDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.UsersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

/**
 * ユーザー管理サービス<br>
 * ユーザーの取得・作成およびメディアとのユーザー紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersMapper usersMapper;

    /**
     * ユーザーをIDで１件絞り込み
     *
     * @param id 対象のユーザーID
     * @return ユーザー情報
     */
    public Users getUserById(long id) {
        return usersMapper.selectByPrimaryKey(id)
            .orElseThrow(() -> new IllegalStateException("ユーザーが見つかりません"));
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

        return usersMapper.select(c -> c.where(UsersDynamicSqlSupport.id, isIn(ids)));
    }

}
