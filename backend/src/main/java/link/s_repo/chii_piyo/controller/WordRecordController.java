package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.WordRecordConverter;
import link.s_repo.chii_piyo.controller.gen.WordRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.WordRecordResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;

import link.s_repo.chii_piyo.service.WordRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WordRecordController implements WordRecordManagementApi {

    private final WordRecordService wordRecordService;
    private final WordRecordConverter wordRecordConverter;
    private final S3StorageManager s3StorageManager;
    private final MediaConverter mediaConverter;

    /**
     * POST /word-records<br>
     * ことばの記録を登録
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param wordRecordData 登録することばの記録情報
     * @return 201ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createWordRecord(
        String xRequestedWith, WordRecordRequestDto wordRecordData) {

        // サービス層で登録処理
        wordRecordService.createWordRecord(wordRecordData);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /word-records<br>
     * ことばの記録一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return ことばの記録一覧
     */
    @Override
    public ResponseEntity<List<WordRecordResponseDto>> getWordRecords(String xRequestedWith) {
        // サービス層で記録一覧を全取得(記録一覧と関連メディアをまとめたレコードで取得)
        List<WordRecordService.WordRecordWithMedia> recordsWithMedia =
            wordRecordService.getWordRecords();

        // WordRecordResponseDtoを生成してリスト化
        List<WordRecordResponseDto> response = recordsWithMedia.stream()
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
                return wordRecordConverter.toWordRecordResponseDto(
                    recordWithMedia.record(),
                    mediaResponseList
                );
            })
            .toList();

        // レスポンスを返す
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /word-records/{id}<br>
     * ことばの記録を更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             記録ID
     * @param wordRecordData 更新することばの記録情報
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateWordRecord(
        String xRequestedWith, Long id, WordRecordRequestDto wordRecordData) {
        // サービス層で更新する
        wordRecordService.updateWordRecord(id, wordRecordData);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /word-records/{id}<br>
     * ことばの記録を削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             記録ID
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWordRecord(String xRequestedWith, Long id) {
        // サービス層で削除する
        wordRecordService.deleteWordRecord(id);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }
}
