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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * アルバム管理コントローラー<br>
 * OpenAPI Generator生成のAlbumManagementApiインターフェースを実装し、アルバムの取得・作成およびメディアとのアルバム紐付けに関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AlbumController implements AlbumManagementApi {
    private final AlbumService albumService;
    private final AlbumConverter albumConverter;

    /**
     * POST /albums/{id}/media
     * アルバムにメディアを追加する
     */
    @Override
    public ResponseEntity<Void> addAlbumMedia(
        String xRequestedWith, Long id, AlbumMediaAddRequestDto albumMediaData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /albums<br>
     * 新しいアルバムを作成する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param albumData      アップロードリクエストDTO
     * @return 作成されたアルバムの情報
     */
    @Override
    public ResponseEntity<AlbumResponseDto> createAlbum(
        String xRequestedWith, AlbumRequestDto albumData) {
        // サービス層でアルバムを作成する
        Albums createdAlbum = albumService.createAlbum(albumData.getTitle());

        // 作成されたアルバムをDTOに変換してレスポンスする
        AlbumResponseDto response = albumConverter.toAlbumResponseDto(
            createdAlbum,
            new AlbumService.MediaDataResult(0, 0, Collections.emptyList())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /albums/{albumId}<br>
     * 指定したIDのアルバムを取得する
     */
    @Override
    public ResponseEntity<AlbumResponseDto> getAlbum(String xRequestedWith, Long albumId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
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
     */
    @Override
    public ResponseEntity<AlbumResponseDto> updateAlbum(
        String xRequestedWith, Long albumId, AlbumRequestDto albumData) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /albums/{albumId}<br>
     * 指定したIDのアルバムを削除する
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param albumId        アルバムID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteAlbum(String xRequestedWith, Long albumId) {
        // サービス層でアルバムを削除する
        albumService.deleteAlbum(albumId);
        return ResponseEntity.noContent().build();
    }
}
