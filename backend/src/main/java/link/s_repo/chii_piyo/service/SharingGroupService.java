package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.SharingGroupMembers;
import link.s_repo.chii_piyo.model.gen.SharingGroups;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.SharingGroupRepository;
import link.s_repo.chii_piyo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * 共有グループ管理サービス<br>
 * 共有グループの取得・作成およびメディアとの共有グループ紐付け、メンバーの管理を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SharingGroupService {
    private final MediaRepository mediaRepository;
    private final SharingGroupRepository sharingGroupRepository;
    private final UserRepository userRepository;
    private final S3StorageManager s3StorageManager;

    /**
     * ログインユーザーの所属する共有グループ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @param userId ログイン中のユーザーID
     * @return 共有グループエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<SharingGroups> getSharingGroups(Long userId) {

        // ユーザーの所属する共有グループのIDリストを取得
        List<Long> userSharingScopeIds = getUserSharingScopes(userId);

        // リストが空の場合は空のリストを返す
        if (userSharingScopeIds.isEmpty()) {
            return Collections.emptyList();
        }

        // IDを元に共有グループを取得して返却
        return sharingGroupRepository.findByIdsOrderById(userSharingScopeIds);
    }

    /**
     * 共有グループ一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return 共有グループエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<SharingGroups> getAllSharingGroups() {
        return sharingGroupRepository.findAllOrderById();
    }

    /**
     * 共有グループをID指定で1件取得する
     *
     * @param id 対象共有グループのID
     * @return 共有グループデータ
     */
    @Transactional(readOnly = true)
    public SharingGroups getSharingGroupById(Long id) {
        return sharingGroupRepository.findById(id)
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
        sharingGroupRepository.save(sharingGroups);

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
            sharingGroupRepository.membersSave(members);
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
        return sharingGroupRepository.findMembersByGroupIds(groupIds);
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
        sharingGroupRepository.deleteMembersByGroupId(id);

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
            sharingGroupRepository.membersSave(newMembers);
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
        List<Users> users = userIds.isEmpty() ? Collections.emptyList() : userRepository.findByIds(userIds);


        // ユーザーIDをキーにしたMapに変換してIDで取得できるようにする
        Map<Long, Users> usersMap = users.stream()
            .collect(Collectors.toMap(Users::getId, user -> user));

        // ユーザーごとのアイコンダウンロードURLを取得し同様にユーザーIDをキーにしたMapに変換
        Map<Long, URI> iconUrlsMap = new HashMap<>();
        users.forEach(user -> {
            String s3Key = user.getUserIconKey();
            URI uri = (s3Key == null || s3Key.isEmpty()) ? null :
                s3StorageManager.generateDownloadPresignedUrl(s3Key, null);
            iconUrlsMap.put(user.getId(), uri);
        });

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
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public void deleteSharingGroup(Long id) {
        // 存在チェック
        getSharingGroupById(id);

        // メディアの共有グループ選択を削除する
        mediaRepository.clearSharingGroupId(id);

        // 所属メンバーの削除
        sharingGroupRepository.deleteMembersByGroupId(id);

        // グループ本体の削除
        sharingGroupRepository.delete(id);
    }

    /**
     * 共有グループの名前を更新する
     *
     * @param sharingGroups 対象の共有グループID
     * @param name          新しい名前
     * @return 共有グループエンティティ
     */
    @PreAuthorize("hasRole('ADMIN')")
    public SharingGroups updateSharingGroup(SharingGroups sharingGroups, String name) {
        sharingGroups.setName(name);
        sharingGroupRepository.update(sharingGroups);

        return sharingGroups;
    }

    /**
     * 指定のユーザーの所属共有グループを取得する
     *
     * @param userId 対象ユーザーID
     * @return 共有グループのIDリスト
     */
    public List<Long> getUserSharingScopes(Long userId) {
        // ユーザーIDが一致するメンバー情報をDBから取得
        List<SharingGroupMembers> members = sharingGroupRepository.findMembersByUserId(userId);

        // 取得したエンティティのリストから 共有グループID だけを抽出して返す
        return members.stream()
            .map(SharingGroupMembers::getSharingGroupId)
            .toList();
    }

    /**
     * ユーザーIDのリストを受け取り、各ユーザーの共有グループIDリストをMapで返す
     *
     * @param userIds 対象ユーザーIDリスト
     * @return Map<userId, 共有グループIDリスト>
     */
    @Transactional(readOnly = true)
    public Map<Long, List<Long>> getUserSharingScopesBulk(List<Long> userIds) {
        // ユーザーIDリストが空の場合空マップを返却
        if (userIds.isEmpty()) {
            return Map.of();
        }

        // 受け取ったユーザーIDリストに該当する共有メンバーを一括取得
        List<SharingGroupMembers> members = sharingGroupRepository.findMembersByUserIds(userIds);


        // ユーザーIDと共有メンバー情報をMap型にグルーピングして返却
        return members.stream()
            .collect(Collectors.groupingBy(
                SharingGroupMembers::getUserId,
                Collectors.mapping(SharingGroupMembers::getSharingGroupId, Collectors.toList())
            ));
    }

    /**
     * memberAndIconMappingの結果を返すためのレコードクラス
     */
    public record MemberAndIconMapResult(
        Map<Long, Users> usersMap, Map<Long, URI> iconUrlsMap,
        Map<Long, List<SharingGroupMembers>> membersByGroupIdMap) {
    }
}
