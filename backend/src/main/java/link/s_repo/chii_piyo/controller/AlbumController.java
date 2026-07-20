package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.AlbumConverter;
import link.s_repo.chii_piyo.controller.gen.AlbumManagementApi;
import link.s_repo.chii_piyo.model.gen.AlbumMediaAddRequestDto;
import link.s_repo.chii_piyo.model.gen.AlbumRequestDto;
import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * アルバム管理コントローラー<br>
 * アルバムの取得・作成およびメディアとのアルバム紐付けに関するAPIエンドポイントを提供
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AlbumController implements AlbumManagementApi {
    private final AlbumService albumService;
    private final AlbumConverter albumConverter;

    /**
     * POST /albums/{id}/media<br>
     * アルバムにメディアを追加する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             対象のアルバムID
     * @param albumMediaData アルバムに追加するメディアの情報(メディアIDリスト)
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> addAlbumMedia(
        String xRequestedWith, Long id, AlbumMediaAddRequestDto albumMediaData) {

        // サービス層でアルバムにメディアを追加する
        albumService.addAlbumMedia(id, albumMediaData.getMediaIds());
        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /albums/{id}/media<br>
     * アルバムから複数メディアを削除する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             対象のアルバムID
     * @param mediaIds       アルバムから削除するメディアのIDリスト
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteAlbumMedia(
        String xRequestedWith, Long id, List<Long> mediaIds) {

        // サービス層でアルバムにメディアを追加する
        albumService.deleteAlbumMedia(id, mediaIds);

        // 204 No Contentを返す
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /albums<br>
     * 新しいアルバムを作成する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param albumData      アップロードリクエストDTO
     * @return 201ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createAlbum(
        String xRequestedWith, AlbumRequestDto albumData) {
        // サービス層でアルバムを作成する
        albumService.createAlbum(albumData.getTitle());

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /albums/{albumId}<br>
     * 指定したIDのアルバムを取得する
     */
    @Override
    public ResponseEntity<AlbumResponseDto> getAlbum(String xRequestedWith, Long albumId) {
        // サービス層でアルバムを取得する
        Albums album = albumService.getAlbumById(albumId);

        List<Long> albumIds = List.of(albumId);

        // アルバムIDをキーにして画像数と動画数、URLリストのレコードを取得
        AlbumService.MediaDataResult mediaData =
            albumService.getMediaDataByAlbumIds(albumIds).getOrDefault(
                albumId, new AlbumService.MediaDataResult(0, 0, Collections.emptyList()));

        // コンバータでDTOに変換
        AlbumResponseDto response = albumConverter.toAlbumResponseDto(album, mediaData);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /albums<br>
     * アルバムのリストを取得する
     */
    @Override
    public ResponseEntity<List<AlbumResponseDto>> getAlbums(String xRequestedWith) {
        // サービス層でエンティティを取得し、コンバータでDTOに変換する
        List<Albums> albums = albumService.getAlbums();

        // アルバムIDを抽出してリストを作成
        List<Long> albumIds = albums.stream().map(Albums::getId).toList();

        // アルバムIDをキー、画像数と動画数のレコードを値とするマップを一括取得
        Map<Long, AlbumService.MediaDataResult> mediaData =
            albumService.getMediaDataByAlbumIds(albumIds);

        // アルバムエンティティのリストをDTOのリストに変換
        List<AlbumResponseDto> response = albums.stream()
            .map(album -> {
                    // アルバムIDをキーにして画像数と動画数、URLリストのレコードをマップから取得
                    AlbumService.MediaDataResult data = mediaData.getOrDefault(
                        album.getId(),
                        new AlbumService.MediaDataResult(
                            0,
                            0,
                            Collections.emptyList()
                        )
                    );

                    // コンバータでDTOに変換
                    return albumConverter.toAlbumResponseDto(album, data);
                }
            )
            .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /albums/{albumId}<br>
     * 指定したIDのアルバムを更新する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param albumId        アルバムID
     * @param albumData      リクエストデータ(新しいアルバムのタイトル)
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAlbum(
        String xRequestedWith, Long albumId, AlbumRequestDto albumData) {
        // アルバム名が空の場合は400 Bad Requestを返す
        if (albumData.getTitle() == null || albumData.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // サービス層でアルバムを更新する
        albumService.updateAlbum(albumId, albumData.getTitle());
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /albums/{albumId}<br>
     * 指定したIDのアルバムを削除する
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param albumId        アルバムID
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbum(String xRequestedWith, Long albumId) {
        // サービス層でアルバムを削除する
        albumService.deleteAlbum(albumId);
        return ResponseEntity.noContent().build();
    }
}
