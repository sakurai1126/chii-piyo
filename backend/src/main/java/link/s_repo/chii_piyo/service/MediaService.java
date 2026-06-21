package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.common.S3KeyGenerator;
import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaBatchUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.MediaUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.gen.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static link.s_repo.chii_piyo.repository.gen.MediaDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * メディア管理サービス<br>
 * 写真・動画のメタデータ登録およびS3との連携を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaMapper mediaMapper;
    private final S3Service s3Service;
    private final ThumbnailService thumbnailService;
    private final S3KeyGenerator s3KeyGenerator;
    private final SharingGroupService sharingGroupService;
    private final AlbumService albumService;
    private final TagService tagService;

    /**
     * メディアをID指定で1件取得する
     *
     * @param id 対象のメディアのID
     * @return メディアデータ
     */
    @Transactional(readOnly = true)
    public Media getMedia(Long id) {
        return mediaMapper.selectOne(c -> c
            .where(MediaDynamicSqlSupport.id, isEqualTo(id))
            .and(MediaDynamicSqlSupport.id, isNotIn(
                select(TrashItemsDynamicSqlSupport.mediaId)
                    .from(TrashItemsDynamicSqlSupport.trashItems)
            ))
        ).orElseThrow(() -> new ResourceNotFoundException("メディアが見つかりません mediaId=" + id));
    }

    /**
     * メディアをID指定で複数件取得する
     *
     * @param ids 対象のメディアのIDリスト
     * @return メディアデータリスト
     */
    @Transactional(readOnly = true)
    public List<Media> getMediabyIds(List<Long> ids) {
        return mediaMapper.select(c -> c
            .where(MediaDynamicSqlSupport.id, isIn(ids))
            .and(MediaDynamicSqlSupport.id, isNotIn(
                select(TrashItemsDynamicSqlSupport.mediaId)
                    .from(TrashItemsDynamicSqlSupport.trashItems)
            ))
        );
    }

    /**
     * メディア総件数を取得する
     *
     * @param mediaType      メディア種別 (PHOTO / VIDEO)
     * @param albumId        アルバムID
     * @param excludeAlbumId 除外するアルバムID
     * @param tagId          タグIDのリスト
     * @param sharingGroupId 共有グループID
     * @param startDate      日時指定開始日
     * @param endDate        日時指定終了日
     * @param isFavorite     お気に入りのみかどうかのフラグ
     * @param currentUserId  現在のユーザーID (isFavoriteがtrueの場合に使用)
     * @return 総件数の数値
     */
    @Transactional(readOnly = true)
    public Long countMedia(
        String mediaType,
        Long albumId,
        Long excludeAlbumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFavorite,
        Long currentUserId) {

        return mediaMapper.count(c -> {
            // 絞り込み条件設定を構築
            c.where(buildMediaFilterConditions(mediaType, albumId, excludeAlbumId, tagId,
                sharingGroupId, isFavorite, currentUserId, startDate, endDate));

            return c;
        });
    }

    /**
     * メディアの一覧を取得する
     *
     * @param offset         取得位置
     * @param limit          最大件数
     * @param mediaType      メディア種別 (PHOTO / VIDEO)
     * @param albumId        アルバムID
     * @param excludeAlbumId 除外するアルバムID
     * @param tagId          タグIDのリスト
     * @param sharingGroupId 共有グループID
     * @param startDate      日時指定開始日
     * @param endDate        日時指定終了日
     * @param isFavorite     お気に入りのみかどうかのフラグ
     * @param currentUserId  現在のユーザーID (isFavoriteがtrueの場合に使用)
     * @return メディアのリスト
     */
    public List<Media> getMediaList(
        Integer offset,
        Integer limit,
        String mediaType,
        Long albumId,
        Long excludeAlbumId,
        List<Long> tagId,
        Long sharingGroupId,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFavorite,
        Long currentUserId) {
        // ページネーション込みで一覧取得
        return mediaMapper.select(c -> {
                // 絞り込み条件設定を構築
                c.where(buildMediaFilterConditions(mediaType, albumId, excludeAlbumId, tagId,
                    sharingGroupId, isFavorite, currentUserId, startDate, endDate));

                c.orderBy(MediaDynamicSqlSupport.createdAt.descending());
                return c.limit(limit).offset(offset);
            }
        );
    }

    private List<AndOrCriteriaGroup> buildMediaFilterConditions(
        String mediaType,
        Long albumId,
        Long excludeAlbumId,
        List<Long> tagId,
        Long sharingGroupId,
        Boolean isFavorite,
        Long currentUserId,
        LocalDate startDate,
        LocalDate endDate) {

        // 絞り込み条件構築用のリストを用意し各条件が存在する時は追加していく
        List<AndOrCriteriaGroup> conditions = new ArrayList<>();
        if (albumId != null) {
            conditions.add(and(MediaDynamicSqlSupport.albumId, isEqualTo(albumId)));
        }

        if (excludeAlbumId != null) {
            // 指定したアルバムIDと一致しないまたはアルバムIDがNULLのメディア
            conditions.add(and(MediaDynamicSqlSupport.albumId, isNotEqualTo(excludeAlbumId),
                or(MediaDynamicSqlSupport.albumId, isNull())));
        }

        if ("PHOTO".equals(mediaType) || "VIDEO".equals(mediaType)) {
            conditions.add(and(MediaDynamicSqlSupport.mediaType, isEqualTo(mediaType)));
        }

        if (sharingGroupId != null) {
            // 0指定の場合は指定されていない(全員公開)のみを取得する
            if (sharingGroupId == 0) {
                conditions.add(and(MediaDynamicSqlSupport.sharingGroupId, isNull()));
            } else {
                conditions.add(and(MediaDynamicSqlSupport.sharingGroupId, isEqualTo(sharingGroupId)));
            }
        }

        if (tagId != null && !tagId.isEmpty()) {
            // タグIDで絞り込むため、MediaTagsテーブルとサブクエリで結合して条件を追加
            // タグIDの一致するMediaTagsレコードをサブクエリで取得しMediaIdを抽出
            // 上記のMediaIdとMediaテーブルのidで絞り込む
            conditions.add(and(MediaDynamicSqlSupport.id,
                isIn(select(MediaTagsDynamicSqlSupport.mediaId)
                    .from(MediaTagsDynamicSqlSupport.mediaTags)
                    .where(MediaTagsDynamicSqlSupport.tagId, isIn(tagId))
                ))
            );
        }

        if (startDate != null) {
            // createdAt >= startDate で検索
            // atStartOfDayでLocalDateTimeに変換、toOffsetDateTimeでOffsetDateTimeに変換して検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isGreaterThanOrEqualTo(startDate.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        if (endDate != null) {
            // createdAt < endDate で検索
            // plusDays(1)で翌日にして対象日末までを検索
            conditions.add(and(MediaDynamicSqlSupport.createdAt,
                isLessThan(endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime())));
        }

        // isFavoriteがtrueの場合、サブクエリで現在のユーザーがお気に入りに入れているメディアをカウントする
        if (Boolean.TRUE.equals(isFavorite) && currentUserId != null) {
            conditions.add(
                // 自分（=currentUserId）が登録したfavoritesレコードのmedia_idのリストを取得し
                // メイン検索対象の media.id がそのリストの中に含まれているかを判定
                and(MediaDynamicSqlSupport.id, isIn(
                    select(FavoritesDynamicSqlSupport.mediaId)
                        .from(FavoritesDynamicSqlSupport.favorites)
                        .where(FavoritesDynamicSqlSupport.userId, isEqualTo(currentUserId))
                ))
            );
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
     * メディアレコードを作成し、署名付きアップロードURLを返却する<br>
     * upload_status は PROCESSING で初期化される
     *
     * @param userId           アップロードを実行するユーザーID
     * @param mediaType        メディア種別 (PHOTO / VIDEO)
     * @param originalFilename 元のファイル名
     * @param contentType      MIMEタイプ
     * @param fileSize         ファイルサイズ (バイト)
     * @param width            幅
     * @param height           高さ
     * @param takenAt          撮影日
     * @param albumId          所属アルバムID
     * @param sharingGroupId   共有範囲グループID
     * @return 作成したメディアレコードと署名付きURL
     */
    @Transactional
    public CreateMediaResult createMedia(
        Long userId,
        String mediaType,
        String originalFilename,
        String contentType,
        Long fileSize,
        Integer width,
        Integer height,
        LocalDate takenAt,
        Long albumId,
        Long sharingGroupId
    ) {
        // S3キーを生成
        String s3Key = s3KeyGenerator.buildS3Key("media", originalFilename);

        // Mediaエンティティを構築
        Media media = new Media();
        media.setUploadedBy(userId);
        media.setMediaType(mediaType);
        media.setOriginalFilename(originalFilename);
        media.setContentType(contentType);
        media.setFileSize(fileSize);
        media.setWidth(width);
        media.setHeight(height);
        media.setS3Key(s3Key);
        media.setTakenAt(takenAt);
        media.setAlbumId(albumId);
        media.setSharingGroupId(sharingGroupId);
        // アップロード進行中の状態でステータスを登録
        media.setUploadStatus("PROCESSING");
        media.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // DBに保存 (insertSelectiveでnullカラムをスキップ)
        mediaMapper.insertSelective(media);

        // 署名付きアップロードURLを発行
        URI presignedUrl = s3Service.generateUploadPresignedUrl(s3Key, contentType);

        return new CreateMediaResult(media, presignedUrl);
    }

    /**
     * メディアのアップロードステータスを更新する<br>
     * 自分自身がアップロードしたメディアのみ更新可能
     *
     * @param mediaId      対象のメディアID
     * @param userId       実行ユーザーID
     * @param uploadStatus 更新後のステータス (COMPLETED / FAILED / PROCESSING)
     * @return 更新後のメディア情報
     * @throws ResourceNotFoundException     対象メディアが存在しない場合
     * @throws ResourceAccessDeniedException アップロード者以外が更新しようとした場合
     */
    @Transactional
    public Media updateUploadStatus(Long mediaId, Long userId, String uploadStatus) {
        // 対象メディアを取得
        // 生成に必須の処理のためゴミ箱フィルターはなし
        Media media = mediaMapper.selectOne(
                c -> c.where(id, isEqualTo(mediaId)))
            .orElseThrow(() -> new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaId));

        // アップロード者本人かを確認
        if (!media.getUploadedBy().equals(userId)) {
            throw new ResourceAccessDeniedException("このメディアを更新する権限がありません mediaId=" + mediaId);
        }

        // ステータスのみを更新
        media.setUploadStatus(uploadStatus);
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // updateByPrimaryKeySelectiveでnullカラムをスキップして更新
        mediaMapper.updateByPrimaryKeySelective(media);

        // COMPLETEDに変わったところからサムネイル生成を非同期で起動
        if ("COMPLETED".equals(uploadStatus)) {
            thumbnailService.generateThumbnailAsync(
                mediaId,
                media.getMediaType(),
                media.getS3Key(),
                media.getOriginalFilename(),
                media.getFileSize()
            );
        }

        return media;
    }

    /**
     * 対象メディアの前後のメディア情報を取得し返却する<br>
     * 前後のメディア情報と位置情報を返却する
     *
     * @param mediaId 対象メディアのID
     * @return Mediaエンティティとナビゲーション位置をまとめたリスト
     */
    public List<GetMediaNavigationResult> getMediaNavigation(Long mediaId) {
        // 対象の前のメディアをID降順で2件取得
        List<Media> previousMediaList = mediaMapper.select(c -> c
            // IDが小さいものが前のメディアになるため、ID < 対象IDで絞り込む
            .where(MediaDynamicSqlSupport.id, isLessThan(mediaId))
            // ゴミ箱フィルター
            .and(MediaDynamicSqlSupport.id, isNotIn(
                select(TrashItemsDynamicSqlSupport.mediaId)
                    .from(TrashItemsDynamicSqlSupport.trashItems)
            ))
            // ID降順で対象に近いものから順番に2件取得する
            .orderBy(MediaDynamicSqlSupport.id.descending())
            .limit(2)
        );
        // 表示順を昇順に揃え直す
        Collections.reverse(previousMediaList);

        // 以降のメディアをID昇順で2件取得
        List<Media> nextMediaList = mediaMapper.select(c -> c
            // IDが大きいものが後のメディアになるため、ID > 対象IDで絞り込む
            .where(MediaDynamicSqlSupport.id, isGreaterThan(mediaId))
            // ゴミ箱フィルター
            .and(MediaDynamicSqlSupport.id, isNotIn(
                select(TrashItemsDynamicSqlSupport.mediaId)
                    .from(TrashItemsDynamicSqlSupport.trashItems)
            ))
            // ID昇順で対象から順番に取得する
            .orderBy(MediaDynamicSqlSupport.id)
            .limit(2)
        );

        // buildNavigationResultsで前方メディアと現在以降のメディアを結合し、ナビゲーション位置を付与して返却する
        return buildNavigationResults(previousMediaList, nextMediaList);
    }

    /**
     * 前方メディアと現在以降のメディアを結合しナビゲーション位置を付与する
     *
     * @param previousMediaList 対象の前のメディアリスト 昇順
     * @param nextMediaList     以降のメディアリスト 昇順
     * @return ナビゲーション位置を付与したメディアリスト
     */
    private List<GetMediaNavigationResult> buildNavigationResults(
        List<Media> previousMediaList,
        List<Media> nextMediaList
    ) {
        List<GetMediaNavigationResult> results = new ArrayList<>();
        // 前のメディアリストは昇順で2件まで入っているため、サイズとインデックスに応じてナビゲーション位置を付与する
        for (int i = 0; i < previousMediaList.size(); i++) {
            Media media = previousMediaList.get(i);
            NavigationPosition position = (previousMediaList.size() == 2 && i == 0) ?
                NavigationPosition.PREVIOUS_2 : NavigationPosition.PREVIOUS_1;
            results.add(new GetMediaNavigationResult(media, position));
        }
        // 現在以降のメディアリストは昇順で2件まで入っているため、サイズとインデックスに応じてナビゲーション位置を付与する
        for (int i = 0; i < nextMediaList.size(); i++) {
            Media media = nextMediaList.get(i);

            NavigationPosition position = (i == 0) ? NavigationPosition.NEXT_1 :
                NavigationPosition.NEXT_2;
            results.add(new GetMediaNavigationResult(media, position));
        }
        return results;
    }

    /**
     * メディア情報を更新する
     *
     * @param id         対象のメディアID
     * @param updateData 更新用データ（アルバムID と 共有グループIDを想定）
     */
    public void updateMedia(Long id, MediaUpdateRequestDto updateData) {
        // 対象メディアを取得
        Media media = getMedia(id);

        // isPresentでそのキーが存在したか（undefinedでないか）を判定しnullとundefinedの処理を分岐する
        if (updateData.getSharingGroupId().isPresent()) {
            Long newId = updateData.getSharingGroupId().get();
            // nullではない場合存在するかチェックするため取得処理を挟む（存在しないIDの場合例外になる）
            if (newId != null) {
                sharingGroupService.getSharingGroupById(newId);
            }

            media.setSharingGroupId(newId);
        }

        if (updateData.getAlbumId().isPresent()) {
            Long newId = updateData.getAlbumId().get();
            // nullではない場合存在するかチェックするため取得処理を挟む（存在しないIDの場合例外になる）
            if (newId != null) {
                albumService.getAlbumById(newId);
            }
            media.setAlbumId(newId);
        }

        mediaMapper.updateByPrimaryKey(media);
    }

    /**
     * メディア情報(タグ/共有範囲/アルバム)を一括更新する
     *
     * @param mediaBatchUpdateData 更新用のデータ
     */
    @Transactional
    public void updateMediaBatch(MediaBatchUpdateRequestDto mediaBatchUpdateData) {
        // 対象メディアを取得する
        List<Media> mediaList = mediaMapper.select(
            c -> c.where(id, isIn(mediaBatchUpdateData.getMediaIds()))
                // ゴミ箱フィルター
                .and(MediaDynamicSqlSupport.id, isNotIn(
                    select(TrashItemsDynamicSqlSupport.mediaId)
                        .from(TrashItemsDynamicSqlSupport.trashItems)
                ))
        );
        if (mediaBatchUpdateData.getMediaIds().size() != mediaList.size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaBatchUpdateData.getMediaIds());
        }

        // 共有グループIDの更新がある場合は共有グループの存在チェックを行う
        if (mediaBatchUpdateData.getSharingGroupId().isPresent()) {
            Long newSharingGroupId = mediaBatchUpdateData.getSharingGroupId().get();
            if (newSharingGroupId != null) {
                sharingGroupService.getSharingGroupById(newSharingGroupId);
            }
        }

        // 明示的にtagIdsプロパティが送信された場合はタグの存在チェックと更新処理を行う
        if (mediaBatchUpdateData.getTagIds().isPresent()) {
            List<Long> tagIds = mediaBatchUpdateData.getTagIds().get();
            if (tagIds != null && !tagIds.isEmpty()) {
                // 該当タグの件数をカウント
                Long count = tagService.count(tagIds);
                if (count != tagIds.size()) {
                    throw new ResourceNotFoundException("存在しないタグが含まれています");
                }
            }

            if (tagIds != null) {
                tagService.syncMediaBatchTags(mediaBatchUpdateData.getMediaIds(), tagIds);
            }
        }

        // 共有範囲グループがある場合のみ
        if (mediaBatchUpdateData.getSharingGroupId().isPresent()) {
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
                    isIn(mediaBatchUpdateData.getMediaIds()));
            });
        }
    }

    /**
     * ゴミ箱データからメディアエンティティを取得しレコードにして返却する
     *
     * @param trashItems ゴミ箱データエンティティ
     * @return ゴミ箱データとメディアデータをまとめたレコードのリスト
     */
    public List<TrashItemAndMediaResult> getTrashItemAndMedia(List<TrashItems> trashItems) {
        // 空の場合空リストで即時リターン
        if (trashItems == null || trashItems.isEmpty()) {
            return Collections.emptyList();
        }

        // ゴミ箱データからメディアIDを抽出
        List<Long> mediaIds = trashItems.stream().map(TrashItems::getMediaId).toList();

        // 抽出したメディアIDに一致するメディアを取得
        List<Media> mediaList = mediaMapper.select(c -> c.where(id, isIn(mediaIds)));

        // 取り出せるようにIDをキーにマップ化
        Map<Long, Media> mediaMapList = mediaList.stream()
            .collect(Collectors.toMap(Media::getId, Function.identity()));

        // レコードにまとめつつリスト化して返却
        return trashItems.stream().map(
            trashItem -> new TrashItemAndMediaResult(
                trashItem,
                mediaMapList.get(trashItem.getMediaId())
            )
        ).toList();
    }


    /**
     * ゴミ箱のデータとそれに連動したメディアのレコード
     */
    public record TrashItemAndMediaResult(TrashItems trashItem, Media media) {
    }


    /**
     * Mediaエンティティと署名付きURLをまとめて返すための内部クラス
     */
    public record CreateMediaResult(Media media, URI presignedUrl) {
    }


    /**
     * どの位置のナビゲーションを返すかのENUM型
     */
    public enum NavigationPosition {
        NEXT_1, NEXT_2, PREVIOUS_1, PREVIOUS_2
    }

    /**
     * Mediaエンティティとナビゲーション位置をまとめて返すための内部クラス
     */
    public record GetMediaNavigationResult(Media media, NavigationPosition position) {
    }


}
