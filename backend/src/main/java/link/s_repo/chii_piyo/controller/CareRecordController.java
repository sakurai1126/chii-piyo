package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.CareRecordListConverter;
import link.s_repo.chii_piyo.controller.gen.CareRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.CareRecordListResponseDto;
import link.s_repo.chii_piyo.model.gen.CareRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.CareRecords;
import link.s_repo.chii_piyo.model.gen.DiaperDetails;
import link.s_repo.chii_piyo.model.gen.HealthDetails;
import link.s_repo.chii_piyo.model.gen.MealDetails;
import link.s_repo.chii_piyo.model.gen.MilkDetails;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import link.s_repo.chii_piyo.service.CareRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 育児記録管理コントローラー<br>
 * 育児記録管理の取得・作成・更新・削除に関するAPIエンドポイントを提供
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CareRecordController implements CareRecordManagementApi {

    private final CareRecordService careRecordService;
    private final CurrentUserProvider currentUserProvider;
    private final CareRecordListConverter careRecordListConverter;

    /**
     * POST /care-records<br>
     * 育児記録を登録
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param careRecordData 登録する育児記録情報
     * @return 201ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
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
     * GET /care-records<br>
     * 育児記録一覧を取得
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param startDate      検索開始日
     * @param endDate        検索終了日
     * @return 育児記録一覧
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CareRecordListResponseDto> getCareRecords(
        String xRequestedWith, LocalDate startDate, LocalDate endDate) {

        // サービス層からデータを取得
        List<CareRecords> careRecords = careRecordService.getCareRecords(startDate, endDate);

        // 育児記録(親テーブル)のIDを抽出
        List<Long> recordIds = careRecords.stream().map(CareRecords::getId).toList();

        // 食事記録取得
        List<MealDetails> mealRecords = careRecordService.getMealRecords(recordIds);

        // ミルク記録取得
        List<MilkDetails> milkRecords = careRecordService.getMilkRecords(recordIds);

        // 排泄記録取得
        List<DiaperDetails> diaperRecords = careRecordService.getDiaperRecords(recordIds);

        // 体調記録取得
        List<HealthDetails> healthRecords = careRecordService.getHealthRecords(recordIds);

        // コンバーターでレスポンス形式に変換
        CareRecordListResponseDto response = careRecordListConverter.toCareRecordListResponseDto(
            careRecords, mealRecords, milkRecords, diaperRecords, healthRecords);

        // レスポンスを返却
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /care-records/{id}<br>
     * 育児記録を更新
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             リソースの一意な識別子
     * @param careRecordData 更新する育児記録情報
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateCareRecord(
        String xRequestedWith, Long id, CareRecordRequestDto careRecordData) {
        // サービス層で更新処理
        careRecordService.updateCareRecord(id, careRecordData);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /care-records/{id}<br>
     * 育児記録を削除
     *
     * @param xRequestedWith CSRF防御用カスタムリクエストヘッダー
     * @param id             リソースの一意な識別子
     * @return 204ステータス
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCareRecord(String xRequestedWith, Long id) {
        // サービス層で削除する
        careRecordService.deleteCareRecord(id);

        // 204ステータスを返却
        return ResponseEntity.noContent().build();
    }
}
