package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.TrashItemConverter;
import link.s_repo.chii_piyo.controller.converter.TrashItemListConverter;
import link.s_repo.chii_piyo.controller.gen.TrashManagementApi;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItemListResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItemResponseDto;
import link.s_repo.chii_piyo.model.gen.TrashItems;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.S3Service;
import link.s_repo.chii_piyo.service.TrashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * ゴミ箱管理コントローラー<br>
 * OpenAPI Generator生成のTrashManagementApiインターフェースを実装し、ゴミ箱のアイテム取得、復元に関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TrashController implements TrashManagementApi {
    private final TrashItemConverter trashItemConverter;
    private final TrashItemListConverter trashItemListConverter;
    private final TrashService trashService;
    private final MediaService mediaService;
    private final MediaConverter mediaConverter;
    private final S3Service s3Service;

    /**
     * DELETE /trash/{id}<br>
     * ゴミ箱からアイテムを完全に削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             対象のリソースID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteTrashItem(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /trash<br>
     * ゴミ箱の中身をすべて空にする
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> emptyTrash(String xRequestedWith) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * GET /trash<br>
     * ゴミ箱内のアイテム一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param offset         取得開始位置
     * @param limit          取得件数
     * @return ゴミ箱アイテム一覧
     */
    @Override
    public ResponseEntity<TrashItemListResponseDto> getTrashItems(
        String xRequestedWith, Integer offset, Integer limit) {
        // サービス層でデータ取得
        List<TrashItems> trashItems = trashService.getTrashItems(offset, limit);

        //  一番近い完全削除までの日数を取得
        Long earliest = trashService.getEarliestDeadline();

        // 総件数を取得
        Long totalCount = trashService.getTotalCount();

        // hasNextの判定
        boolean hasNext = offset + trashItems.size() < totalCount;

        List<MediaService.TrashItemAndMediaResult> trashItemAndMedia =
            mediaService.getTrashItemAndMedia(trashItems);


        // コンバーターでレスポンスDTOに変換
        List<TrashItemResponseDto> itemsDto =
            trashItemAndMedia.stream().map(c -> {
                // サムネイル生成処理
                URI thumbnailPresignedUrl = c.media().getThumbnailS3Key() != null
                    ? s3Service.generateDownloadPresignedUrl(c.media().getThumbnailS3Key(),
                    c.media().getOriginalFilename())
                    : null;
                // サムネイルとメタデータのみのレスポンスDTOを作成
                MediaResponseDto mediaDto = mediaConverter.toMediaResponseDto(
                    c.media(),
                    null,
                    null,
                    thumbnailPresignedUrl,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                );
                // DTOに変換
                return trashItemConverter.toTrashItemResponseDto(c.trashItem(), mediaDto);
            }).toList();
        TrashItemListResponseDto response = trashItemListConverter.toTrashItemListResponseDto(
            itemsDto, earliest, totalCount, hasNext);

        // レスポンスを返す
        return ResponseEntity.ok(response);
    }

    /**
     * POST /trash/{id}/restore<br>
     * ゴミ箱からアイテムを復元
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             対象のリソースID
     * @return 復元されたメディア情報
     */
    @Override
    public ResponseEntity<MediaResponseDto> restoreTrashItem(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
