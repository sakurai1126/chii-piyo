package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.gen.CareRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.CareRecordListResponseDto;
import link.s_repo.chii_piyo.model.gen.CareRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.CareRecordResponseDto;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.CareRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 育児記録管理コントローラー<br>
 * OpenAPI Generator生成のCareRecordManagementApiインターフェースを実装し、育児記録管理のAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CareRecordController implements CareRecordManagementApi {

    private final CareRecordService careRecordService;
    private final CurrentUserProvider currentUserProvider;

    /**
     * POST /care-records<br>
     * 育児記録を登録
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param careRecordData 登録する育児記録情報
     * @return 201ステータス
     */
    @Override
    public ResponseEntity<Void> createCareRecord(
        String xRequestedWith, CareRecordRequestDto careRecordData) {
        // 認証情報から現在のユーザーIDを取得
        Long currentUserId = currentUserProvider.getUserId();

        // サービス層で記録登録
        careRecordService.createCareRecord(careRecordData, currentUserId);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /care-records/{id}<br>
     * 育児記録を削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteCareRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * GET /care-records/{id}<br>
     * 育児記録をID指定で1件取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @return 育児記録情報
     */
    @Override
    public ResponseEntity<CareRecordResponseDto> getCareRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /care-records<br>
     * 育児記録一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param recordType     育児記録種別で絞り込み (optional)
     * @param startDate      検索開始日 (optional)
     * @param endDate        検索終了日 (optional)
     * @return 育児記録一覧
     */
    @Override
    public ResponseEntity<CareRecordListResponseDto> getCareRecords(
        String xRequestedWith, String recordType, LocalDate startDate, LocalDate endDate) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * PUT /care-records/{id}<br>
     * 育児記録を更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @param careRecordData 更新する育児記録情報
     */
    @Override
    public ResponseEntity<Void> updateCareRecord(
        String xRequestedWith, Long id, CareRecordRequestDto careRecordData) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
