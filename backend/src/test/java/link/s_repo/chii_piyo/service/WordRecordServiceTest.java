package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.WordRecordMedia;
import link.s_repo.chii_piyo.model.gen.WordRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.WordRecords;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.WordRecordRepository;
import link.s_repo.chii_piyo.repository.MediaRepository;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WordRecordServiceTest {
    @Mock
    private WordRecordRepository wordRecordRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private WordRecordService wordRecordService;
    @Nested
    @DisplayName("createWordRecord - ことば記録の作成")
    class CreateWordRecord {
        // 共通モックデータの作成
        Long mockCurrentUserId = 1L;

        // 共通リクエストデータの作成ヘルパー
        private WordRecordRequestDto createRequest(List<Long> mediaIds) {
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setComment("はじめて呼んだ");
            request.setRecordedDate(LocalDate.now());
            request.setMediaIds(mediaIds);
            return request;
        }

        @Test
        @DisplayName("Word-01: ことば記録の作成ができること")
        void createWordRecord_success() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            WordRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of(new Media()));

            // 対象の実行
            wordRecordService.createWordRecord(request);

            // 保存処理が呼ばれていることの確認
            verify(wordRecordRepository).save(any(WordRecords.class));
            verify(wordRecordRepository).saveMedia(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(mockMediaIds.getFirst())
            ));
        }

        @Test
        @DisplayName("Word-02: メディアなしでの記録の作成ができること")
        void createWordRecord_notMedia() {
            // リクエストデータの作成
            WordRecordRequestDto request = createRequest(List.of());

            // 対象の実行
            wordRecordService.createWordRecord(request);

            // メディアの取得処理が呼ばれていないことの確認
            verify(mediaRepository, never()).findByIds(any(), any());
        }

        @Test
        @DisplayName("Word-03: 存在しないメディアIDを渡して記録の作成をしようとすると例外で処理されること")
        void createWordRecord_notFound() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            WordRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> wordRecordService.createWordRecord(request))
                .isInstanceOf(ResourceNotFoundException.class);

            // メディア情報保存処理が呼ばれていないことの確認
            verify(wordRecordRepository, never()).saveMedia(any());
        }
    }

    @Nested
    @DisplayName("getWordRecords - ことば記録の取得")
    class GetWordRecords {
        @Test
        @DisplayName("Word-04: ことば記録の取得ができること")
        void getWordRecords_success() {
            // モックデータの作成
            Long mockRecordId = 1L;
            Long mockMediaId = 2L;
            Long mockUserId = 3L;

            // ことば記録のモックを作成
            WordRecords mockRecord = new WordRecords();
            mockRecord.setId(mockRecordId);

            // 記録メディア情報のモックを作成
            WordRecordMedia mockRecordMedia = new WordRecordMedia();
            mockRecordMedia.setWordRecordId(mockRecordId);
            mockRecordMedia.setMediaId(mockMediaId);

            // メディアのモックを作成
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);

            // ことば記録取得処理のスタブ化
            when(wordRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(wordRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of(mockRecordMedia));

            // ユーザーID取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // メディア取得処理のスタブ化
            when(mediaRepository.findByIds(List.of(mockMediaId), mockUserId))
                .thenReturn(List.of(mockMedia));

            // 対象を実行し結果を取得
            List<WordRecordService.WordRecordWithMedia> result = wordRecordService.getWordRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList()).hasSize(1);
            assertThat(result.getFirst().mediaList().getFirst().getId()).isEqualTo(mockMediaId);
        }

        @Test
        @DisplayName("Word-05: 記録が0件で空リストが返ること")
        void getWordRecords_empty() {
            // 各処理のスタブ化
            when(wordRecordRepository.findAll()).thenReturn(List.of());

            // 対象を実行し結果を取得
            List<WordRecordService.WordRecordWithMedia> result = wordRecordService.getWordRecords();

            // 結果の確認
            assertThat(result.size()).isZero();

            // 後続の取得処理が呼ばれていないことを確認
            verify(wordRecordRepository, never()).findMediaByRecordIds(any());
        }

        @Test
        @DisplayName("Word-06: メディアが紐づかない記録に記録のみが返ること")
        void getWordRecords_noMedia() {
            // モックデータの作成
            Long mockRecordId = 1L;

            // ことば記録のモックを作成
            WordRecords mockRecord = new WordRecords();
            mockRecord.setId(mockRecordId);

            // ことば記録取得処理のスタブ化
            when(wordRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(wordRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of());

            // 対象を実行し結果を取得
            List<WordRecordService.WordRecordWithMedia> result = wordRecordService.getWordRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList().size()).isZero();
        }

        @Test
        @DisplayName("Word-07: ゴミ箱内のメディアを含む場合除外された値が返ること")
        void getWordRecords_trashItem() {
            // モックデータの作成
            Long mockRecordId = 1L;
            Long mockMediaId1 = 2L;
            Long mockMediaId2 = 3L;
            Long mockUserId = 4L;

            // ことば記録のモックを作成
            WordRecords mockRecord = new WordRecords();
            mockRecord.setId(mockRecordId);

            // 記録メディア情報のモックを作成
            WordRecordMedia mockRecordMedia1 = new WordRecordMedia();
            mockRecordMedia1.setWordRecordId(mockRecordId);
            mockRecordMedia1.setMediaId(mockMediaId1);
            WordRecordMedia mockRecordMedia2 = new WordRecordMedia();
            mockRecordMedia2.setWordRecordId(mockRecordId);
            mockRecordMedia2.setMediaId(mockMediaId2);

            // メディアのモックを作成
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId1);

            // ことば記録取得処理のスタブ化
            when(wordRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(wordRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of(mockRecordMedia1, mockRecordMedia2));

            // ユーザーID取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // メディア取得処理のスタブ化 - ID2件に対し1件フィルタリング想定で1件のみ返却
            when(mediaRepository.findByIds(List.of(mockMediaId1, mockMediaId2), mockUserId))
                .thenReturn(List.of(mockMedia));

            // 対象を実行し結果を取得
            List<WordRecordService.WordRecordWithMedia> result = wordRecordService.getWordRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList()).hasSize(1);
            assertThat(result.getFirst().mediaList().getFirst().getId()).isEqualTo(mockMediaId1);
        }
    }

    @Nested
    @DisplayName("updateWordRecord - ことば記録の更新")
    class UpdateWordRecord {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        // 共通モックデータの作成
        Long mockCurrentUserId = 2L;

        // 共通リクエストデータの作成ヘルパー
        private WordRecordRequestDto createRequest(List<Long> mediaIds) {
            WordRecordRequestDto request = new WordRecordRequestDto();
            request.setTitle("まま");
            request.setComment("はじめて呼んだ");
            request.setRecordedDate(LocalDate.now());
            request.setMediaIds(mediaIds);
            return request;
        }

        @Test
        @DisplayName("Word-08: ことば記録の更新ができること")
        void updateWordRecord_success() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            WordRecordRequestDto request = createRequest(mockMediaIds);

            // モックデータの作成
            WordRecords record = new WordRecords();

            // 各処理のスタブ化
            when(wordRecordRepository.findById(requestId)).thenReturn(Optional.of(record));
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of(new Media()));

            // 対象の実行
            wordRecordService.updateWordRecord(requestId, request);

            // 画像削除処理/更新処理/画像保存処理が呼ばれていることの確認
            verify(wordRecordRepository).deleteMediaByRecordId(requestId);
            verify(wordRecordRepository).update(any(WordRecords.class));
            verify(wordRecordRepository).saveMedia(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(mockMediaIds.getFirst())
            ));
        }

        @Test
        @DisplayName("Word-09: 存在しないIDで更新リクエストをした場合例外で処理されること")
        void updateWordRecord_notFound() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            WordRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(wordRecordRepository.findById(requestId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> wordRecordService.updateWordRecord(requestId, request))
                .isInstanceOf(ResourceNotFoundException.class);

            // 画像削除処理/更新処理/画像保存処理が呼ばれていないことの確認
            verify(wordRecordRepository, never()).deleteMediaByRecordId(requestId);
            verify(wordRecordRepository, never()).update(any());
            verify(wordRecordRepository, never()).saveMedia(any());
        }
    }

    @Nested
    @DisplayName("deleteWordRecord - ことば記録の削除")
    class DeleteWordRecord {
        @Test
        @DisplayName("Word-10: ことば記録の削除ができること")
        void deleteWordRecord_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            WordRecords record = new WordRecords();

            // 各処理のスタブ化
            when(wordRecordRepository.findById(requestId)).thenReturn(Optional.of(record));

            // 対象の実行
            wordRecordService.deleteWordRecord(requestId);

            // 画像削除処理/記録削除処理が呼ばれていることの確認
            verify(wordRecordRepository).deleteMediaByRecordId(requestId);
            verify(wordRecordRepository).deleteById(requestId);
        }
    }
}
