package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.model.gen.Media;

import link.s_repo.chii_piyo.repository.gen.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Albums> findAll() {
        return albumsMapper.select(c -> c.orderBy(id));
    }

    /**
     * 指定したIDのアルバムに紐づくメディアの件数を取得する<br>
     *
     * @param albumIds 取得するアルバムのIDのリスト
     * @return アルバムに紐づくメディアの件数レコードのリスト
     */
    @Transactional(readOnly = true)
    public Map<Long, MediaCountResult> getMediaCountsByAlbumIds(List<Long> albumIds) {
        // アルバムIDのリストが空の場合は空のマップを返す
        if (albumIds.isEmpty()) return Collections.emptyMap();

        // メディアの中からalbum_idカラムと受け取ったアルバムIDのリストに含まれるものを全件取得
        List<Media> mediaList = mediaMapper.select(
            c -> c.where(MediaDynamicSqlSupport.albumId, isIn(albumIds))
        );

        // アルバムIDをキー、画像数と動画数を値とするマップを作成
        Map<Long, MediaCountResult> result = new HashMap<>();

        // media_typeから画像か動画化を判別してそれぞれの件数をマップに格納
        for (Media media : mediaList) {
            Long albumId = media.getAlbumId();
            // すでにマップにアルバムIDが存在するか確認し、存在しない場合は初期値をセット
            MediaCountResult current = result.getOrDefault(albumId, new MediaCountResult(0, 0));

            // アルバムIDをキーにしてマップに画像数と動画数を格納、存在する場合は上書き
            if (media.getMediaType().equals("PHOTO")) {
                result.put(albumId, new MediaCountResult(current.photoCount() + 1, current.videoCount()));
            }
            if (media.getMediaType().equals("VIDEO")) {
                result.put(albumId, new MediaCountResult(current.photoCount(), current.videoCount() + 1));
            }
        }

        return result;
    }

    public record MediaCountResult(int photoCount, int videoCount) {
    }
}
