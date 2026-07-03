package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.gen.WordRecordManagementApi;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.WordRecordResponseDto;

import link.s_repo.chii_piyo.service.WordRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WordRecordController implements WordRecordManagementApi {

    private final WordRecordService wordRecordService;

    /**
     * POST /word-records<br>
     * ことばの記録を登録
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param wordRecordData 登録することばの記録情報
     * @return 201ステータス
     */
    @Override
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
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PUT /word-records/{id}<br>
     * ことばの記録を更新
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @param wordRecordData 更新することばの記録情報
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> updateWordRecord(
        String xRequestedWith, Long id, WordRecordRequestDto wordRecordData) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /word-records/{id}<br>
     * ことばの記録を削除
     *
     * @param xRequestedWith X-Requested-With ヘッダ (CSRF防御用)
     * @param id             リソースの一意な識別子
     * @return 204ステータス
     */
    @Override
    public ResponseEntity<Void> deleteWordRecord(String xRequestedWith, Long id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
