package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.FirstRecordConverter;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.gen.FirstRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.service.FirstRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * はじめて記録管理コントローラー<br>
 * はじめて記録の取得・作成・更新・削除およびメディアとのアルバム紐付けに関するAPIエンドポイントを提供
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FirstRecordController implements FirstRecordManagementApi {
    private final FirstRecordService firstRecordService;
    private final FirstRecordConverter firstRecordConverter;
    private final S3StorageManager s3StorageManager;
    private final MediaConverter mediaConverter;

    /**
     * POST /first-records<br>
     * はじめて記録を登録
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param firstRecordData 登録するはじめて記録情報
     * @return 201ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createFirstRecord(
        String xRequestedWith, FirstRecordRequestDto firstRecordData) {
        // サービス層で登録処理
        firstRecordService.createFirstRecord(firstRecordData);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /first-records<br>
     * はじめて記録一覧を取得
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @return はじめて記録一覧
     */
    @Override
    public ResponseEntity<List<FirstRecordResponseDto>> getFirstRecords(String xRequestedWith) {
        // サービス層で記録一覧を全取得(記録一覧と関連メディアをまとめたレコードで取得)
        List<FirstRecordService.FirstRecordWithMedia> recordsWithMedia = firstRecordService.getFirstRecords();

        // FirstRecordResponseDtoを生成してリスト化
        List<FirstRecordResponseDto> response = recordsWithMedia.stream()
            .map(recordWithMedia -> {
                // 記録毎のMediaResponseDtoのリストを作成
                List<MediaResponseDto> mediaResponseList = recordWithMedia.mediaList().stream()
                    .map(media -> {
                        // URLを生成
                        URI thumbnailPresignedUrl = media.getThumbnailS3Key() != null
                            ? s3StorageManager.generateDownloadPresignedUrl(media.getThumbnailS3Key(), media.getOriginalFilename())
                            : null;
                        return mediaConverter.toMediaResponseDto(
                            media, List.of(), null, thumbnailPresignedUrl,
                            false, 0L, null, null, null, null, List.of()
                        );
                    })
                    .toList();
                // はじめて記録とメディアのレスポンスDTOリストを渡しコンバータで変換
                return firstRecordConverter.toFirstRecordResponseDto(
                    recordWithMedia.record(),
                    mediaResponseList
                );
            })
            .toList();

        // レスポンスを返す
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /first-records/{id}<br>
     * はじめて記録を更新
     *
     * @param xRequestedWith  CSRF防御用カスタムリクエストヘッダー
     * @param id              記録ID
     * @param firstRecordData 更新するはじめて記録情報
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFirstRecord(
        String xRequestedWith, Long id, FirstRecordRequestDto firstRecordData) {
        // サービス層で更新する
        firstRecordService.updateFirstRecord(id, firstRecordData);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /first-records/{id}<br>
     * はじめて記録を削除
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             記録ID
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFirstRecord(String xRequestedWith, Long id) {
        // サービス層で削除する
        firstRecordService.deleteFirstRecord(id);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }

}
