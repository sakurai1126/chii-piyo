package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.gen.UsersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ユーザー管理サービス<br>
 * ユーザーの取得・作成およびメディアとのユーザー紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersMapper usersMapper;

    public Users getUserById(long id) {
        return usersMapper.selectByPrimaryKey(id)
            .orElseThrow(() -> new IllegalStateException("ユーザーが見つかりません"));
    }
}
