package link.s_repo.chii_piyo.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.SharingGroupMembersMapper;
import link.s_repo.chii_piyo.repository.gen.SharingGroupsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static link.s_repo.chii_piyo.repository.gen.SharingGroupsDynamicSqlSupport.id;
import static link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport.sharingGroupId;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * 共有グループ管理サービス<br>
 * 共有グループの取得・作成およびメディアとの共有グループ紐付け、メンバーの管理を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharingGroupService {
    private final SharingGroupsMapper sharingGroupsMapper;
    private final SharingGroupMembersMapper sharingGroupMembersMapper;
    private final UserService userService;

    /**
     * 共有グループ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return 共有グループエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<SharingGroups> getSharingGroups() {
        return sharingGroupsMapper.select(c -> c.orderBy(id));
    }

    /**
     * 共有グループをID指定で1件取得する
     *
     * @param id 対象共有グループのID
     * @return 共有グループデータ
     */
    @Transactional(readOnly = true)
    public SharingGroups getSharingGroupById(Long id) {
        return sharingGroupsMapper.selectByPrimaryKey(id)
            .orElseThrow(() -> new ResourceNotFoundException("共有グループが見つかりません id=" + id));
    }

    /**
     * 共有グループを新規作成する
     *
     * @param name    新規共有グループの名前
     * @param userIds 新規共有グループ所属メンバーのID
     */
    @Transactional
    public void createGroup(String name, List<Long> userIds) {
        SharingGroups sharingGroups = new SharingGroups();

        // 受け取った値をセットしてデータを登録
        sharingGroups.setName(name);
        sharingGroupsMapper.insertSelective(sharingGroups);

        // 採番されたIDを取得
        Long id = sharingGroups.getId();

        // ユーザーIDの指定がある場合は合わせて登録
        if (userIds != null && !userIds.isEmpty()) {
            List<SharingGroupMembers> members = userIds.stream()
                .map(userId -> {
                    SharingGroupMembers member = new SharingGroupMembers();
                    member.setSharingGroupId(id);
                    member.setUserId(userId);
                    member.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                    return member;
                })
                .toList();

            // DBに一括登録
            sharingGroupMembersMapper.insertMultiple(members);
        }
    }


    /**
     * 共有グループ所属メンバー一覧を取得する
     *
     * @param groupIds 対象のグループIDリスト
     * @return 共有グループのメンバーのリスト
     */
    @Transactional(readOnly = true)
    public List<SharingGroupMembers> getMembersByGroupIds(List<Long> groupIds) {
        // 受け取ったIDリストが空であれば空リストを返す
        if (groupIds == null || groupIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 受け取ったgroupIdsが含まれる共有グループ所属メンバーを取得して返却
        return sharingGroupMembersMapper.select(
            c -> c.where(SharingGroupMembersDynamicSqlSupport.sharingGroupId, isIn(groupIds))
        );
    }

    /**
     * 共有グループを更新する
     *
     * @param id         対象共有グループID
     * @param newUserIds 新しい共有グループメンバーのIDリスト
     * @return 更新後のメンバーリスト
     */
    @Transactional
    public List<SharingGroupMembers> editMembers(
        Long id, List<Long> newUserIds) {

        // 対象グループの既存メンバーを一度すべて削除
        sharingGroupMembersMapper.delete(c -> c.where(sharingGroupId, isEqualTo(id)));

        // 保存用のエンティティを作成
        if (newUserIds != null && !newUserIds.isEmpty()) {
            List<SharingGroupMembers> newMembers = newUserIds.stream()
                .map(userId -> {
                    SharingGroupMembers member = new SharingGroupMembers();
                    member.setSharingGroupId(id);
                    member.setUserId(userId);
                    member.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                    return member;
                })
                .toList();

            // DBに一括登録
            sharingGroupMembersMapper.insertMultiple(newMembers);
        }

        // 更新後のメンバーリストを再取得して返却
        return getMembersByGroupIds(List.of(id));
    }

    /**
     * 共有グループ所属メンバーの処理をするようにMap化する
     *
     * @param members 共有グループ所属メンバーエンティティリスト
     * @return MemberAndIconMapResultレコードクラス<br>
     * - usersMap: キーがユーザーID、値がユーザーのMap<br>
     * - iconUrlsMap: キーがユーザーID、値がアイコンURLのMap<br>
     * - membersByGroupIdMap: キーが共有グループID、値がユーザーのMap
     */
    public MemberAndIconMapResult memberAndIconMapping(List<SharingGroupMembers> members) {
        // 所属メンバーから重複削除したユーザーIDを抽出
        List<Long> userIds = members.stream()
            .map(SharingGroupMembers::getUserId)
            .distinct()
            .toList();

        // 所属メンバーに紐づいたユーザーを取得
        List<Users> users = userService.getUsersById(userIds);

        // ユーザーIDをキーにしたMapに変換してIDで取得できるようにする
        Map<Long, Users> usersMap = users.stream()
            .collect(Collectors.toMap(Users::getId, user -> user));

        // ユーザーごとのアイコンダウンロードURLを取得し同様にユーザーIDをキーにしたMapに変換
        Map<Long, URI> iconUrlsMap = new HashMap<>();
        users.forEach(user ->
            iconUrlsMap.put(user.getId(), userService.generateIconDownloadPresignedUrl(user))
        );

        // グループIDごとにメンバーをまとめる
        Map<Long, List<SharingGroupMembers>> membersByGroupIdMap = members.stream()
            .collect(Collectors.groupingBy(SharingGroupMembers::getSharingGroupId));

        return new MemberAndIconMapResult(usersMap, iconUrlsMap, membersByGroupIdMap);
    }


    /**
     * 共有グループと所属するメンバーを削除する
     *
     * @param id 共有グループID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSharingGroup(Long id) {
        // 存在チェック
        getSharingGroupById(id);

        // 所属メンバーの削除
        sharingGroupMembersMapper.delete(c -> c.where(sharingGroupId, isEqualTo(id)));

        // グループ本体の削除
        sharingGroupsMapper.deleteByPrimaryKey(id);
    }

    /**
     * 共有グループの名前を更新する
     *
     * @param sharingGroups 対象の共有グループID
     * @param name          新しい名前
     * @return 共有グループエンティティ
     */
    public SharingGroups updateSharingGroup(SharingGroups sharingGroups, String name) {
        sharingGroups.setName(name);
        sharingGroupsMapper.updateByPrimaryKeySelective(sharingGroups);

        return sharingGroups;
    }

    /**
     * memberAndIconMappingの結果を返すためのレコードクラス
     */
    public record MemberAndIconMapResult(
        Map<Long, Users> usersMap, Map<Long, URI> iconUrlsMap,
        Map<Long, List<SharingGroupMembers>> membersByGroupIdMap) {
    }
}

