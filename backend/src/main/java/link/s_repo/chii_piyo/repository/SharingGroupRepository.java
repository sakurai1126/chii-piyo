package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.SharingGroupMembersMapper;
import link.s_repo.chii_piyo.repository.gen.SharingGroupsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport.sharingGroupId;
import static link.s_repo.chii_piyo.repository.gen.SharingGroupsDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;


@Repository
@RequiredArgsConstructor
public class SharingGroupRepository {
    private final SharingGroupsMapper sharingGroupsMapper;
    private final SharingGroupMembersMapper sharingGroupMembersMapper;

    /**
     * 共有グループをID指定で1件取得する
     *
     * @param id 対象の共有グループのID
     * @return 共有グループデータ
     */
    public Optional<SharingGroups> findById(Long id) {
        return sharingGroupsMapper.selectByPrimaryKey(id);
    }

    /**
     * 共有グループ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return 共有グループエンティティの一覧
     */
    public List<SharingGroups> findAllOrderById() {
        return sharingGroupsMapper.select(c -> c.orderBy(id));
    }

    /**
     * 共有グループを新規作成する
     */
    public void save(SharingGroups sharingGroups) {
        sharingGroupsMapper.insertSelective(sharingGroups);
    }

    /**
     * 共有グループメンバーを複数県新規作成する
     */
    public void membersSave(List<SharingGroupMembers> members) {
        sharingGroupMembersMapper.insertMultiple(members);
    }

    /**
     * 共有グループ所属メンバーリストを取得する
     *
     * @param ids 対象のグループIDリスト
     * @return 共有グループメンバーのリスト
     */
    public List<SharingGroupMembers> findMembersByGroupIds(List<Long> ids) {
        return sharingGroupMembersMapper.select(
            c -> c.where(SharingGroupMembersDynamicSqlSupport.sharingGroupId, isIn(ids))
        );
    }

    /**
     * 対象グループのメンバーをすべて削除
     *
     * @param id 共有グループID
     */
    public void deleteMembersByGroupId(Long id) {
        sharingGroupMembersMapper.delete(c -> c.where(sharingGroupId, isEqualTo(id)));
    }

    /**
     * 共有グループを削除する
     *
     * @param id 共有グループID
     */
    public void delete(Long id) {
        sharingGroupsMapper.deleteByPrimaryKey(id);
    }

    /**
     * 共有グループを更新する
     *
     * @param sharingGroups 共有グループエンティティ
     */
    public void update(SharingGroups sharingGroups) {
        sharingGroupsMapper.updateByPrimaryKeySelective(sharingGroups);
    }

    /**
     * ユーザーIDから対象グループのメンバーリストを取得する
     *
     * @param id ユーザーID
     */
    public List<SharingGroupMembers> findMembersByUserId(Long id) {
        return sharingGroupMembersMapper.select(
            c -> c.where(SharingGroupMembersDynamicSqlSupport.userId, isEqualTo(id)));
    }

    /**
     * ユーザーIDリストから対象グループのメンバーリストを取得する
     *
     * @param ids ユーザーIDリスト
     */
    public List<SharingGroupMembers> findMembersByUserIds(List<Long> ids) {
        return sharingGroupMembersMapper.select(
            c -> c.where(SharingGroupMembersDynamicSqlSupport.userId, isIn(ids)));
    }
}
