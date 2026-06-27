package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.gen.GrowthRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.GrowthRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.GrowthRecordResponseDto;
import link.s_repo.chii_piyo.service.GrowthRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 成長記録管理コントローラー<br>
 * OpenAPI Generator生成のGrowthRecordManagementApiインターフェースを実装し、成長記録管理のAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class GrowthRecordController implements GrowthRecordManagementApi {
    private final GrowthRecordService growthRecordService;

    /**
     * POST /growth-records<br>
     * 身長・体重記録を登録
     *
     * @param xRequestedWith   X-Requested-With ヘッダ (CSRF防御用)
     * @param growthRecordData 登録する身長・体重記録情報
     * @return 201ステータス
     */
    @Override
    public ResponseEntity<Void> createGrowthRecord(
        String xRequestedWith, GrowthRecordRequestDto growthRecordData) {

        // サービス層で記録登録
        growthRecordService.createGrowthRecord(growthRecordData);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /growth-records/{id} : 身長・体重記録を削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteGrowthRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /growth-records : 身長・体重記録一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param startDate      検索開始日 (optional)
     * @param endDate        検索終了日 (optional)
     * @return 身長・体重記録一覧 (status code 200)
     */
    @Override
    public ResponseEntity<List<GrowthRecordResponseDto>> getGrowthRecords(
        String xRequestedWith, LocalDate startDate, LocalDate endDate) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * PUT /growth-records/{id} : 身長・体重記録を更新
     *
     * @param xRequestedWith   X-Requested-With ヘッダ (CSRF防御用)
     * @param id               リソースの一意な識別子
     * @param growthRecordData 更新する身長・体重記録情報
     * @return 更新された身長・体重記録 (status code 200)
     */
    @Override
    public ResponseEntity<Void> updateGrowthRecord(
        String xRequestedWith, Long id, GrowthRecordRequestDto growthRecordData) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
