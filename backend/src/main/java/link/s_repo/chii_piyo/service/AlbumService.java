package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.model.gen.Media;

import link.s_repo.chii_piyo.repository.AlbumRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * アルバム管理サービス<br>
 * アルバムに関する処理のロジックを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final MediaRepository mediaRepository;
    private final S3StorageManager s3StorageManager;
    private final AlbumRepository albumRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * アルバムを新規作成する
     *
     * @param title 追加するアルバムのタイトル
     */
    @Transactional
    public void createAlbum(String title) {
        Albums album = new Albums();

        // アルバムエンティティに値をセット
        album.setTitle(title);

        // アルバムをDBに保存
        albumRepository.save(album);
    }

    /**
     * アルバム一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return アルバムエンティティのリスト
     */
    @Transactional(readOnly = true)
    public List<Albums> getAlbums() {
        return albumRepository.findAll();
    }

    /**
     * アルバムをID指定で1件取得する
     *
     * @param id 対象アルバムのID
     * @return アルバムデータ
     */
    @Transactional(readOnly = true)
    public Albums getAlbumById(Long id) {
        return albumRepository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("アルバムが見つかりません " + "id=" + id));
    }

    /**
     * 指定したIDのアルバムに紐づくメディアの件数を取得する<br>
     *
     * @param albumIds 取得するアルバムのIDのリスト
     * @return アルバムに紐づくメディア件数とカバーURLのリストを格納したマップ
     */
    @Transactional(readOnly = true)
    public Map<Long, MediaDataResult> getMediaDataByAlbumIds(
        List<Long> albumIds) {
        // アルバムIDのリストが空の場合は空のマップを返す
        if (albumIds.isEmpty()) return Collections.emptyMap();

        // メディアの中からalbum_idカラムと受け取ったアルバムIDのリストに含まれるものを全件取得
        List<Media> mediaList = mediaRepository.findByAlbumIds(albumIds, currentUserProvider.getUserId());

        // アルバムIDをキー、画像数と動画数を値とするマップを作成
        Map<Long, MediaDataResult> result = new HashMap<>();

        // media_typeから画像か動画かを判別してそれぞれの件数をマップに格納
        for (Media media : mediaList) {
            Long albumId = media.getAlbumId();
            // すでにマップにアルバムIDが存在するか確認し、存在しない場合は初期値をセット
            MediaDataResult current = result.get(albumId);
            if (current == null) {
                current = new MediaDataResult(0, 0, new ArrayList<>());
            }

            // カウントアップ用の一時変数を用意
            int newPhotoCount = current.photoCount();
            int newVideoCount = current.videoCount();
            List<String> newUrls = current.urls();

            // アルバムIDをキーにして画像数を更新
            if ("PHOTO".equals(media.getMediaType())) {
                newPhotoCount++;
            }

            // アルバムIDをキーにして動画数を更新
            if ("VIDEO".equals(media.getMediaType())) {
                newVideoCount++;
            }

            if (current.urls().size() < 3) {
                // カバーURLのリストを更新
                URI thumbnailPresignedUrl = media.getThumbnailS3Key() != null
                    ? s3StorageManager.generateDownloadPresignedUrl(media.getThumbnailS3Key(), media.getOriginalFilename())
                    : null;

                if (thumbnailPresignedUrl != null) {
                    newUrls.add(thumbnailPresignedUrl.toString());
                }
            }

            result.put(albumId, new MediaDataResult(
                newPhotoCount,
                newVideoCount,
                newUrls
            ));
        }

        return result;
    }

    /**
     * アルバムを削除する
     *
     * @param albumId アルバムID
     */
    @Transactional
    public void deleteAlbum(Long albumId) {
        // 削除前に存在チェック
        getAlbumById(albumId);

        // アルバムに紐づくメディアのalbum_idをnullに更新
        mediaRepository.clearAlbumId(albumId);

        albumRepository.deleteById(albumId);
    }

    /**
     * アルバムのタイトルを更新する
     *
     * @param albumId アルバムID
     * @param title   新しいアルバムタイトル
     */
    @Transactional
    public void updateAlbum(Long albumId, String title) {
        // 更新前に存在チェック
        Albums album = getAlbumById(albumId);

        // タイトルを更新してDBに保存
        album.setTitle(title);
        album.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        albumRepository.update(album);
    }

    /**
     * アルバムに複数メディアを追加する
     *
     * @param albumId  対象のアルバムID
     * @param mediaIds 対象のメディアIDリスト
     */
    @Transactional
    public void addAlbumMedia(Long albumId, List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            throw new IllegalArgumentException("IDが指定されていません");
        }

        // アルバムの存在チェック
        getAlbumById(albumId);

        // mediaIdsのメディアの存在チェック
        List<Media> mediaList = mediaRepository.findByIds(mediaIds, currentUserProvider.getUserId());

        if (mediaList.size() != mediaIds.stream().distinct().toList().size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaIds);
        }

        // 対象メディアを一括更新する
        mediaRepository.updateAlbumIdByMediaIds(mediaIds, albumId);
    }

    /**
     * アルバムから複数メディアを削除する
     *
     * @param albumId  対象のアルバムID
     * @param mediaIds 対象のメディアIDリスト
     */
    @Transactional
    public void deleteAlbumMedia(Long albumId, List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            throw new IllegalArgumentException("IDが指定されていません");
        }

        // アルバムの存在チェック
        getAlbumById(albumId);

        // mediaIdsのメディアの存在チェック
        List<Media> mediaList = mediaRepository.findByIds(mediaIds, currentUserProvider.getUserId());

        if (mediaList.size() != mediaIds.stream().distinct().toList().size()) {
            throw new ResourceNotFoundException("メディアが見つかりません mediaId=" + mediaIds);
        }

        // 全部渡されたアルバムに紐づいたものかを確認
        boolean mediaInAlbum =
            mediaList.stream().allMatch(media -> albumId.equals(media.getAlbumId()));
        if (!mediaInAlbum) {
            throw new IllegalArgumentException("指定されたアルバムに属していないメディアが含まれています");
        }

        // 対象メディアのアルバムIDを一括削除する
        mediaRepository.clearAlbumIdByMediaIds(mediaIds);
    }

    public record MediaDataResult(int photoCount, int videoCount, List<String> urls) {
    }
}
