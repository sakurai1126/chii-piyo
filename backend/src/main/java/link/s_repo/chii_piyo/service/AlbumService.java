package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.model.gen.Media;

import link.s_repo.chii_piyo.repository.gen.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static link.s_repo.chii_piyo.repository.gen.AlbumsDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

/**
 * アルバム管理サービス<br>
 * アルバムの取得・作成およびメディアとのアルバム紐付けを担う
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumsMapper albumsMapper;
    private final MediaMapper mediaMapper;
    private final S3Service s3Service;

    /**
     * アルバムを新規作成する<br>
     *
     * @param title 追加するアルバムのタイトル
     * @return 作成されたアルバムエンティティ
     */
    @Transactional
    public Albums createAlbum(String title) {
        Albums albums = new Albums();

        // アルバムエンティティに値をセット
        albums.setTitle(title);
        albums.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        albums.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        // アルバムをDBに保存
        albumsMapper.insert(albums);
        return albums;
    }

    /**
     * アルバム一覧を取得する<br>
     * 全件をID昇順で返す
     *
     * @return アルバムエンティティの一覧
     */
    @Transactional(readOnly = true)
    public List<Albums> getAlbums() {
        return albumsMapper.select(c -> c.orderBy(id));
    }

    /**
     * アルバムをID指定で1件取得する
     *
     * @param id 対象アルバムのID
     * @return アルバムデータ
     */
    @Transactional(readOnly = true)
    public Albums getAlbumById(Long id) {
        return albumsMapper.selectByPrimaryKey(id)
            .orElseThrow(() -> new ResourceNotFoundException("アルバムが見つかりません id=" + id));
    }

    /**
     * 指定したIDのアルバムに紐づくメディアの件数を取得する<br>
     *
     * @param albumIds 取得するアルバムのIDのリスト
     * @return アルバムに紐づくメディア件数とカバーURLのリストを格納したマップ
     */
    @Transactional(readOnly = true)
    public Map<Long, MediaDataResult> getMediaDataByAlbumIds(List<Long> albumIds) {
        // アルバムIDのリストが空の場合は空のマップを返す
        if (albumIds.isEmpty()) return Collections.emptyMap();

        // メディアの中からalbum_idカラムと受け取ったアルバムIDのリストに含まれるものを全件取得
        List<Media> mediaList = mediaMapper.select(
            c -> c.where(MediaDynamicSqlSupport.albumId, isIn(albumIds))
        );

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
                    ? s3Service.generateDownloadPresignedUrl(media.getThumbnailS3Key(), media.getOriginalFilename())
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

    public record MediaDataResult(int photoCount, int videoCount, List<String> urls) {
    }
}
