package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.common.S3KeyGenerator;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.component.ThumbnailGenerator;
import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.MediaSearchCriteria;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaBatchUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.MediaUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.repository.AlbumRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.repository.SharingGroupRepository;
import link.s_repo.chii_piyo.repository.TagRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * メディア管理サービス<br>
 * 写真・動画のメタデータ登録およびS3との連携を担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {
    private final MediaRepository mediaRepository;
    private final S3StorageManager s3StorageManager;
    private final ThumbnailGenerator thumbnailGenerator;
    private final S3KeyGenerator s3KeyGenerator;
    private final SharingGroupRepository sharingGroupRepository;
    private final AlbumRepository albumRepository;
    private final TagRepository tagRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * メディアをID指定で1件取得する
     *
     * @param id 対象のメディアのID
     * @return メディアデータ
     */
    @Transactional(readOnly = true)
    public Media getMedia(Long id) {
        return mediaRepository.findById(id, currentUserProvider.getUserId()).orElseThrow(
            () -> new ResourceNotFoundException("メディアが見つかりません " + "mediaId=" + id));
    }

    /**
     * メディアをID指定で複数件取得する
     *
     * @param ids 対象のメディアのIDリスト
     * @return メディアデータリスト
     */
    @Transactional(readOnly = true)
    public List<Media> getMediabyIds(List<Long> ids) {
        return mediaRepository.findByIds(ids, currentUserProvider.getUserId());
    }

    /**
     * メディア総件数を取得する
     *
     * @param mediaSearchCriteria 検索条件
     * @return 総件数の数値
     */
    @Transactional(readOnly = true)
    public Long countMedia(MediaSearchCriteria mediaSearchCriteria) {

        return mediaRepository.countMedia(mediaSearchCriteria);
    }

    /**
     * メディアの一覧を取得する
     *
     * @param mediaSearchCriteria 検索条件
     * @return メディアのリスト
     */
    @Transactional(readOnly = true)
    public List<Media> getMediaList(MediaSearchCriteria mediaSearchCriteria) {
        // 一覧取得
        return mediaRepository.findBySearchCriteria(mediaSearchCriteria);
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

        // DBに保存
        mediaRepository.save(media);

        // 署名付きアップロードURLを発行
        URI presignedUrl = s3StorageManager.generateUploadPresignedUrl(s3Key, contentType);

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
        Media media = mediaRepository.findUnscopedById(mediaId)
            .orElseThrow(() -> new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaId));

        // アップロード者本人かを確認
        if (!media.getUploadedBy().equals(userId)) {
            throw new ResourceAccessDeniedException("このメディアを更新する権限がありません mediaId=" + mediaId);
        }

        // ステータスのみを更新
        media.setUploadStatus(uploadStatus);
        media.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // updateByPrimaryKeySelectiveでnullカラムをスキップして更新
        mediaRepository.update(media);

        // COMPLETEDに変わったところからサムネイル生成を非同期で起動
        if ("COMPLETED".equals(uploadStatus)) {
            thumbnailGenerator.generateThumbnailAsync(
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
    @Transactional(readOnly = true)
    public List<GetMediaNavigationResult> getMediaNavigation(Long mediaId) {
        Long userId = currentUserProvider.getUserId();

        // 対象の前のメディアをID降順で2件取得
        List<Media> previousMediaList = mediaRepository.findPreviousMedia(mediaId, userId);

        // 表示順を昇順に揃え直す
        Collections.reverse(previousMediaList);

        // 以降のメディアをID昇順で2件取得
        List<Media> nextMediaList = mediaRepository.findNextMedia(mediaId, userId);

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
        List<Media> previousMediaList, List<Media> nextMediaList) {
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
    @Transactional
    public void updateMedia(Long id, MediaUpdateRequestDto updateData) {
        // 対象メディアを取得
        Media media = getMedia(id);

        // isPresentでそのキーが存在したか（undefinedでないか）を判定しnullとundefinedの処理を分岐する
        if (updateData.getSharingGroupId().isPresent()) {
            Long newId = updateData.getSharingGroupId().get();
            // nullではない場合存在するかチェックするため取得処理を挟む（存在しないIDの場合例外になる）
            if (newId != null) {
                sharingGroupRepository.findById(newId)
                    .orElseThrow(() -> new ResourceNotFoundException("共有グループが見つかりません id=" + newId));
            }

            media.setSharingGroupId(newId);
        }

        if (updateData.getAlbumId().isPresent()) {
            Long newId = updateData.getAlbumId().get();
            // nullではない場合存在するかチェックするため取得処理を挟む（存在しないIDの場合例外になる）
            if (newId != null) {
                albumRepository.findById(newId).orElseThrow(() ->
                    new ResourceNotFoundException("アルバムが見つかりません " + "id=" + newId));
            }
            media.setAlbumId(newId);
        }

        mediaRepository.updateAll(media);
    }

    /**
     * メディア情報(タグ/共有範囲)を一括更新する
     *
     * @param mediaBatchUpdateData 更新用のデータ
     */
    @Transactional
    public void updateMediaBatch(MediaBatchUpdateRequestDto mediaBatchUpdateData) {
        // 対象メディアを取得する
        List<Media> mediaList = mediaRepository.findByIds(
            mediaBatchUpdateData.getMediaIds(), currentUserProvider.getUserId());

        if (mediaBatchUpdateData.getMediaIds().size() != mediaList.size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaBatchUpdateData.getMediaIds());
        }

        // 共有グループIDの更新がある場合は共有グループの存在チェックを行う
        if (mediaBatchUpdateData.getSharingGroupId().isPresent()) {
            Long newSharingGroupId = mediaBatchUpdateData.getSharingGroupId().get();
            if (newSharingGroupId != null) {
                sharingGroupRepository.findById(newSharingGroupId)
                    .orElseThrow(() -> new ResourceNotFoundException("共有グループが見つかりません id=" + newSharingGroupId));
            }
        }

        // 明示的にtagIdsプロパティが送信された場合はタグの存在チェックと更新処理を行う
        if (mediaBatchUpdateData.getTagIds().isPresent()) {
            List<Long> tagIds = mediaBatchUpdateData.getTagIds().get();
            if (tagIds != null && !tagIds.isEmpty()) {
                // 該当タグの件数をカウント
                Long count = tagRepository.countByTagIds(tagIds);
                if (count != tagIds.size()) {
                    throw new ResourceNotFoundException("存在しないタグが含まれています");
                }
            }

            // 複数メディアのタグを一括で入替更新する
            if (tagIds != null) {
                List<Long> mediaIds = mediaBatchUpdateData.getMediaIds();
                // 対象メディアに紐づく既存タグを一括削除
                tagRepository.deleteMediaTagsByMediaIds(mediaIds);

                // 新しいタグが指定されている場合のみ登録処理を行う
                if (!tagIds.isEmpty()) {
                    // 登録用のエンティティを作成しメディアの数 × タグの数だけループしてMediaTagsエンティティを作成しリスト化する
                    List<MediaTags> insertList = new ArrayList<>();

                    for (Long mediaId : mediaIds) {
                        for (Long tagId : tagIds) {
                            MediaTags mediaTag = new MediaTags();
                            mediaTag.setMediaId(mediaId);
                            mediaTag.setTagId(tagId);
                            mediaTag.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                            insertList.add(mediaTag);
                        }
                    }

                    // 一括登録する
                    if (!insertList.isEmpty()) {
                        tagRepository.saveMediaTags(insertList);
                    }
                }
            }
        }

        // 共有範囲グループがある場合のみ
        if (mediaBatchUpdateData.getSharingGroupId().isPresent()) {
            // メディアの更新を行う
            mediaRepository.updateSharingGroupBatch(mediaBatchUpdateData);
        }
    }

    /**
     * ゴミ箱データからメディアエンティティを取得しレコードにして返却する
     *
     * @param trashItems ゴミ箱データエンティティ
     * @return ゴミ箱データとメディアデータをまとめたレコードのリスト
     */
    @Transactional(readOnly = true)
    public List<TrashItemAndMediaResult> getTrashItemAndMedia(List<TrashItems> trashItems) {
        // 空の場合空リストで即時リターン
        if (trashItems == null || trashItems.isEmpty()) {
            return Collections.emptyList();
        }

        // ゴミ箱データからメディアIDを抽出
        List<Long> mediaIds = trashItems.stream().map(TrashItems::getMediaId).toList();

        // 抽出したメディアIDに一致するメディアを取得
        List<Media> mediaList = mediaRepository.findUnscopedByIds(mediaIds);

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
