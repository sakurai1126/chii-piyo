package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.repository.gen.SharingGroupsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static link.s_repo.chii_piyo.repository.gen.SharingGroupsDynamicSqlSupport.id;

/**
 * 共有グループ管理サービス<br>
 * 共有グループの取得・作成およびメディアとの共有グループ紐付け、メンバーの管理を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharingGroupService {
    private final SharingGroupsMapper sharingGroupsMapper;

    /**
     * 共有グループ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return 共有グループエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<SharingGroups> findAll() {
        return sharingGroupsMapper.select(c -> c.orderBy(id));
    }
}

