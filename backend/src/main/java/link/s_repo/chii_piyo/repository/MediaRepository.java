package link.s_repo.chii_piyo.repository;

import link.s_repo.chii_piyo.model.MediaSearchCriteria;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaBatchUpdateRequestDto;
import link.s_repo.chii_piyo.repository.gen.FavoritesDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.MediaMapper;
import link.s_repo.chii_piyo.repository.gen.MediaTagsDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.SharingGroupMembersDynamicSqlSupport;
import link.s_repo.chii_piyo.repository.gen.TrashItemsDynamicSqlSupport;
import lombok.RequiredArgsConstructor;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.thumbnailS3Key;
import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.uploadStatus;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Repository
@RequiredArgsConstructor
public class MediaRepository {
    private final MediaMapper mediaMapper;

    /**
     * メディアをID指定で1件取得する
     * (ゴミ箱にあるデータは除外)
     *
     * @param id 対象のメディアのID
     * @return メディアデータ
     */
    public Optional<Media> findById(Long id, Long userId) {
        return mediaMapper.selectOne(c -> c
            .where(MediaDynamicSqlSupport.id, isEqualTo(id), andNotInTrash(), andSharingGroupFilter(userId))
        );
    }

    /**
     * メディアをID指定で複数件取得する
     * (ゴミ箱にあるデータは除外)
     *
     * @param ids 対象のメディアのIDリスト
     * @return メディアデータリスト
     */
    public List<Media> findByIds(List<Long> ids, Long userId) {
        return mediaMapper.select(c -> c
            .where(MediaDynamicSqlSupport.id, isIn(ids), andNotInTrash(), andSharingGroupFilter(userId))
        );
    }

    /**
     * 検索条件をもとに該当するメディアの総件数を取得する
     * (ゴミ箱にあるデータは除外)
     *
     * @param mediaSearchCriteria 検索条件
     * @return 総件数の数値
     */
    public Long countMedia(MediaSearchCriteria mediaSearchCriteria) {
        return mediaMapper.count(c -> {
            // 絞り込み条件設定を構築
            c.where(buildMediaFilterConditions(mediaSearchCriteria));
            return c;
        });
    }

    /**
     * メディアのページ用一覧を取得する
     * (ゴミ箱にあるデータは除外)
     *
     * @param mediaSearchCriteria 検索条件
     * @return メディアのリスト
     */
    public List<Media> findBySearchCriteria(MediaSearchCriteria mediaSearchCriteria) {
        return mediaMapper.select(c -> {
                // 絞り込み条件設定を構築しページ分割して取得
                c.where(buildMediaFilterConditions(mediaSearchCriteria));
                c.orderBy(MediaDynamicSqlSupport.createdAt.descending());
                return c.limit(mediaSearchCriteria.limit()).offset(mediaSearchCriteria.offset());
            }
        );
    }

    /**
     * メディアデータを保存する
     *
     * @param media メディアエンティティ
     */
    public void save(Media media) {
        mediaMapper.insertSelective(media);
    }

    /**
     * 対象メディアをID指定で取得する
     * ゴミ箱にあるデータも含む
     *
     * @param id 対象のメディアID
     */
    public Optional<Media> findUnscopedById(Long id) {
        return mediaMapper.selectByPrimaryKey(id);
    }

    /**
     * 対象メディアをIDリスト指定で取得する
     * ゴミ箱にあるデータも含む
     *
     * @param ids 対象のメディアIDリスト
     */
    public List<Media> findUnscopedByIds(List<Long> ids) {
        return mediaMapper.select(c -> c.where(MediaDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * メディアデータを更新する
     *
     * @param media メディアエンティティ
     */
    public void update(Media media) {
        mediaMapper.updateByPrimaryKeySelective(media);
    }

    /**
     * メディアデータを全件更新する
     *
     * @param media メディアエンティティ
     */
    public void updateAll(Media media) {
        mediaMapper.updateByPrimaryKey(media);
    }

    /**
     * メディアデータをID指定で1件削除する
     *
     * @param id 対象のメディアID
     */
    public void deleteById(Long id) {
        mediaMapper.deleteByPrimaryKey(id);
    }

    /**
     * メディアデータをIDリスト指定で複数件削除する
     *
     * @param ids 対象のメディアID
     */
    public void deleteByIds(List<Long> ids) {
        mediaMapper.delete(c -> c.where(MediaDynamicSqlSupport.id, isIn(ids)));
    }

    /**
     * 共有データの更新を行う
     *
     * @param mediaBatchUpdateData アップデータデータ
     */
    public void updateSharingGroupBatch(MediaBatchUpdateRequestDto mediaBatchUpdateData) {
        // メディアの更新を行う
        mediaMapper.update(updateBuilder -> {
            // 共有グループの送信があれば SET 句に追加
            if (mediaBatchUpdateData.getSharingGroupId().isPresent()) {
                Long newSharingGroupId = mediaBatchUpdateData.getSharingGroupId().get();
                if (newSharingGroupId == null) {
                    updateBuilder = updateBuilder.set(MediaDynamicSqlSupport.sharingGroupId).equalToNull();
                } else {
                    // sharing_group_idカラムをnewSharingGroupIdの値に更新する
                    updateBuilder = updateBuilder
                        .set(MediaDynamicSqlSupport.sharingGroupId)
                        .equalTo(newSharingGroupId);
                }
            }

            // 指定した複数のメディアを対象に更新
            return updateBuilder.where(MediaDynamicSqlSupport.id,
                isIn(mediaBatchUpdateData.getMediaIds()), andNotInTrash());
        });
    }

    /**
     * 対象の前のメディアをID降順で2件取得
     * (ゴミ箱にあるデータは除外)
     */
    public List<Media> findPreviousMedia(Long id, Long userId) {
        return mediaMapper.select(c -> c
            // IDが小さいものが前のメディアになるため、ID < 対象IDで絞り込む
            .where(MediaDynamicSqlSupport.id, isLessThan(id), andNotInTrash(), andSharingGroupFilter(userId))
            // ID降順で対象に近いものから順番に2件取得する
            .orderBy(MediaDynamicSqlSupport.id.descending())
            .limit(2)
        );
    }

    /**
     * 対象以降のメディアをID昇順で2件取得
     * (ゴミ箱にあるデータは除外)
     */
    public List<Media> findNextMedia(Long id, Long userId) {
        return mediaMapper.select(c -> c
            // IDが大きいものが後のメディアになるため、ID > 対象IDで絞り込む
            .where(MediaDynamicSqlSupport.id, isGreaterThan(id), andNotInTrash(), andSharingGroupFilter(userId))
            // ID昇順で対象から順番に取得する
            .orderBy(MediaDynamicSqlSupport.id)
            .limit(2)
        );
    }

    /**
     * メディアをアルバムIDリスト指定で複数件取得する
     * (ゴミ箱にあるデータは除外)
     *
     * @param albumIds 対象のアルバムのIDリスト
     * @return メディアデータリスト
     */
    public List<Media> findByAlbumIds(List<Long> albumIds, Long userId) {
        return mediaMapper.select(
            c -> c.where(MediaDynamicSqlSupport.albumId, isIn(albumIds), andNotInTrash(),
                andSharingGroupFilter(userId))
        );
    }

    /**
     * アルバムに紐づくメディアのalbum_idをnullに更新
     *
     * @param albumId 対象のアルバムID
     */
    public void clearAlbumId(Long albumId) {
        mediaMapper.update(c -> c.set(MediaDynamicSqlSupport.albumId).equalToNull()
            .where(MediaDynamicSqlSupport.albumId, isEqualTo(albumId)));
    }

    /**
     * IDリストに紐づくメディアのalbum_idをnullに更新
     * (ゴミ箱にあるデータは除外)
     *
     * @param mediaIds 対象のメディアIDリスト
     */
    public void clearAlbumIdByMediaIds(List<Long> mediaIds) {
        mediaMapper.update(c -> c.set(MediaDynamicSqlSupport.albumId).equalToNull()
            .where(MediaDynamicSqlSupport.id, isIn(mediaIds))
        );
    }

    /**
     * IDリスト指定の対象メディアのアルバムIDデータを一括更新する
     * (ゴミ箱にあるデータは除外)
     */
    public void updateAlbumIdByMediaIds(List<Long> mediaIds, Long albumId) {
        mediaMapper.update(
            c -> c.set(MediaDynamicSqlSupport.albumId).equalTo(albumId)
                .where(MediaDynamicSqlSupport.id, isIn(mediaIds), andNotInTrash())
        );
    }

    /**
     * 共有グループに紐づくメディアのsharing_group_idをnullに更新
     *
     * @param sharingGroupId 対象の共有グループID
     */
    public void clearSharingGroupId(Long sharingGroupId) {
        mediaMapper.update(c -> c.set(MediaDynamicSqlSupport.sharingGroupId).equalToNull()
            .where(MediaDynamicSqlSupport.sharingGroupId, isEqualTo(sharingGroupId)));
    }

    /**
     * 各パラメータを受け取りフィルタリング用の条件を構築する
     *
     * @param mediaSearchCriteria 検索条件
     * @return 構築された絞り込み条件
     */
    private List<AndOrCriteriaGroup> buildMediaFilterConditions(
        MediaSearchCriteria mediaSearchCriteria) {

        // 絞り込み条件構築用のリストを用意し各条件が存在する時は追加していく
        List<AndOrCriteriaGroup> conditions = new ArrayList<>();
        if (mediaSearchCriteria.albumId() != null) {
            conditions.add(and(MediaDynamicSqlSupport.albumId,
                isEqualTo(mediaSearchCriteria.albumId())));
        }

        if (mediaSearchCriteria.excludeAlbumId() != null) {
            // 指定したアルバムIDと一致しないまたはアルバムIDがNULLのメディア
            conditions.add(and(MediaDynamicSqlSupport.albumId,
                isNotEqualTo(mediaSearchCriteria.excludeAlbumId()),
                or(MediaDynamicSqlSupport.albumId, isNull())));
        }

        if ("PHOTO".equals(mediaSearchCriteria.mediaType()) || "VIDEO".equals(mediaSearchCriteria.mediaType())) {
            conditions.add(and(MediaDynamicSqlSupport.mediaType,
                isEqualTo(mediaSearchCriteria.mediaType())));
        }

        if (mediaSearchCriteria.sharingGroupId() != null) {
            // 0指定の場合は指定されていない(全員公開)のみを取得する
            if (mediaSearchCriteria.sharingGroupId() == 0) {
                conditions.add(and(MediaDynamicSqlSupport.sharingGroupId, isNull()));
            } else {
                conditions.add(and(MediaDynamicSqlSupport.sharingGroupId,
                    isEqualTo(mediaSearchCriteria.sharingGroupId())));
            }
        }

        if (mediaSearchCriteria.tagId() != null && !mediaSearchCriteria.tagId().isEmpty()) {
            // タグIDで絞り込むため、MediaTagsテーブルとサブクエリで結合して条件を追加
            // タグIDの一致するMediaTagsレコードをサブクエリで取得しMediaIdを抽出
            // 上記のMediaIdとMediaテーブルのidで絞り込む
            conditions.add(and(MediaDynamicSqlSupport.id,
                isIn(select(MediaTagsDynamicSqlSupport.mediaId)
                    .from(MediaTagsDynamicSqlSupport.mediaTags)
                    .where(MediaTagsDynamicSqlSupport.tagId, isIn(mediaSearchCriteria.tagId()))
                ))
            );
        }

        if (mediaSearchCriteria.startDate() != null) {
            // createdAt >= startDate で検索
            // atStartOfDayでLocalDateTimeに変換、toOffsetDateTimeでOffsetDateTimeに変換して検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isGreaterThanOrEqualTo(mediaSearchCriteria.startDate().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        if (mediaSearchCriteria.endDate() != null) {
            // createdAt < endDate で検索
            // plusDays(1)で翌日にして対象日末までを検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isLessThan(mediaSearchCriteria.endDate().plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        // isFavoriteがtrueの場合、サブクエリで現在のユーザーがお気に入りに入れているメディアをカウントする
        if (Boolean.TRUE.equals(mediaSearchCriteria.isFavorite()) && mediaSearchCriteria.currentUserId() != null) {
            conditions.add(
                // 自分（=currentUserId）が登録したfavoritesレコードのmedia_idのリストを取得し
                // メイン検索対象の media.id がそのリストの中に含まれているかを判定
                and(MediaDynamicSqlSupport.id, isIn(
                    select(FavoritesDynamicSqlSupport.mediaId)
                        .from(FavoritesDynamicSqlSupport.favorites)
                        .where(FavoritesDynamicSqlSupport.userId,
                            isEqualTo(mediaSearchCriteria.currentUserId()))
                ))
            );
        }


        // 共有範囲でのフィルタリング
        if (mediaSearchCriteria.currentUserId() != null) {
            conditions.add(andSharingGroupFilter(mediaSearchCriteria.currentUserId()));
        }

        // ゴミ箱のテーブル内に存在しないもののみで限定する
        conditions.add(
            and(MediaDynamicSqlSupport.id, isNotIn(
                select(TrashItemsDynamicSqlSupport.mediaId)
                    .from(TrashItemsDynamicSqlSupport.trashItems)
            ))
        );


        return conditions;
    }

    /**
     * 共有範囲に合わせてフィルタリングする
     */
    private AndOrCriteriaGroup andSharingGroupFilter(Long userId) {
        // 共有グループ未設定(全員に公開)を許可
        return and(MediaDynamicSqlSupport.sharingGroupId, isNull(),
            // 加えて共有グループメンバーテーブルの中にユーザーIDがある共有グループで絞り込みをかける
            or(MediaDynamicSqlSupport.sharingGroupId, isIn(
                select(SharingGroupMembersDynamicSqlSupport.sharingGroupId)
                    .from(SharingGroupMembersDynamicSqlSupport.sharingGroupMembers)
                    .where(SharingGroupMembersDynamicSqlSupport.userId, isEqualTo(userId))
            ))
        );
    }


    /**
     * アップロードが完了していてサムネイルのキーがデータ登録されていないものを取得
     *
     * @return 対象のメディアエンティティリスト
     */
    public List<Media> findMissingThumbnails(Long limit) {
        return mediaMapper.select(c -> c
            .where(uploadStatus, isEqualTo("COMPLETED"))
            .and(thumbnailS3Key, isNull())
            .limit(limit)
        );
    }

    /**
     * thumbnail_s3_key を更新する
     */
    @Transactional
    public void updateThumbnailKey(Media media, String thumbnailS3Key) {
        media.setThumbnailS3Key(thumbnailS3Key);
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        mediaMapper.updateByPrimaryKeySelective(media);
    }

    /**
     * 「AND id NOT IN (ゴミ箱)」を丸ごと返す共通メソッド
     */
    private AndOrCriteriaGroup andNotInTrash() {
        return and(MediaDynamicSqlSupport.id, isNotIn(
            select(TrashItemsDynamicSqlSupport.mediaId)
                .from(TrashItemsDynamicSqlSupport.trashItems)
        ));
    }
}
