package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.FirstRecordConverter;
import link.s_repo.chii_piyo.controller.gen.FirstRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecordResponseDto;
import link.s_repo.chii_piyo.service.FirstRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * はじめて記録管理コントローラー<br>
 * OpenAPI Generator生成のFirstRecordManagementApiインターフェースを実装し、はじめて記録の取得・作成およびメディアとのアルバム紐付けに関するAPIエンドポイントを提供する
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FirstRecordController implements FirstRecordManagementApi {
    private final FirstRecordService firstRecordService;
    private final FirstRecordConverter firstRecordConverter;

    /**
     * POST /first-records : はじめて記録を登録
     *
     * @param xRequestedWith  X-Requested-With ヘッダ (CSRF防御用)
     * @param firstRecordData 登録するはじめて記録情報
     * @return 201ステータス
     */
    @Override
    public ResponseEntity<Void> createFirstRecord(
        String xRequestedWith, FirstRecordRequestDto firstRecordData) {
        // サービス層で登録処理
        firstRecordService.createFirstRecord(firstRecordData);

        // 201ステータスコードを返却
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /first-records/{id} : はじめて記録を削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             記録ID
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteFirstRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /first-records/{id} : はじめて記録をID指定で1件取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             記録ID
     * @return はじめて記録情報
     */
    @Override
    public ResponseEntity<FirstRecordResponseDto> getFirstRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /first-records : はじめて記録一覧を取得
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @return はじめて記録一覧
     */
    @Override
    public ResponseEntity<List<FirstRecordResponseDto>> getFirstRecords(String xRequestedWith) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /first-records/{id} : はじめて記録を更新
     *
     * @param xRequestedWith  X-Requested-With ヘッダ (CSRF防御用)
     * @param id              記録ID
     * @param firstRecordData 更新するはじめて記録情報
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateFirstRecord(
        String xRequestedWith, Long id, FirstRecordRequestDto firstRecordData) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
