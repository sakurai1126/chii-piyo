package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.FirstRecordMedia;
import link.s_repo.chii_piyo.model.gen.FirstRecordRequestDto;
import link.s_repo.chii_piyo.model.gen.FirstRecords;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.repository.FirstRecordRepository;
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
public class FirstRecordServiceTest {
    @Mock
    private FirstRecordRepository firstRecordRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private FirstRecordService firstRecordService;

    @Nested
    @DisplayName("createFirstRecord - はじめて記録の作成")
    class CreateFirstRecord {
        // 共通モックデータの作成
        Long mockCurrentUserId = 1L;

        // 共通リクエストデータの作成ヘルパー
        private FirstRecordRequestDto createRequest(List<Long> mediaIds) {
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("はじめてのつかまり立ち");
            request.setComment("台につかまって立った");
            request.setRecordedDate(LocalDate.now());
            request.setMediaIds(mediaIds);
            return request;
        }

        @Test
        @DisplayName("First-01: はじめて記録の作成ができること")
        void createFirstRecord_success() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            FirstRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of(new Media()));

            // 対象の実行
            firstRecordService.createFirstRecord(request);

            // 保存処理が呼ばれていることの確認
            verify(firstRecordRepository).save(any(FirstRecords.class));
            verify(firstRecordRepository).saveMedia(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(mockMediaIds.getFirst())
            ));
        }

        @Test
        @DisplayName("First-02: メディアなしでの記録の作成ができること")
        void createFirstRecord_notMedia() {
            // リクエストデータの作成
            FirstRecordRequestDto request = createRequest(List.of());

            // 対象の実行
            firstRecordService.createFirstRecord(request);

            // メディアの取得処理が呼ばれていないことの確認
            verify(mediaRepository, never()).findByIds(any(), any());
        }

        @Test
        @DisplayName("First-03: 存在しないメディアIDを渡して記録の作成をしようとすると例外で処理されること")
        void createFirstRecord_notFound() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            FirstRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> firstRecordService.createFirstRecord(request))
                .isInstanceOf(ResourceNotFoundException.class);

            // メディア情報保存処理が呼ばれていないことの確認
            verify(firstRecordRepository, never()).saveMedia(any());
        }
    }

    @Nested
    @DisplayName("getFirstRecords - はじめて記録の取得")
    class GetFirstRecords {
        @Test
        @DisplayName("First-04: はじめて記録の取得ができること")
        void getFirstRecords_success() {
            // モックデータの作成
            Long mockRecordId = 1L;
            Long mockMediaId = 2L;
            Long mockUserId = 3L;

            // はじめて記録のモックを作成
            FirstRecords mockRecord = new FirstRecords();
            mockRecord.setId(mockRecordId);

            // 記録メディア情報のモックを作成
            FirstRecordMedia mockRecordMedia = new FirstRecordMedia();
            mockRecordMedia.setFirstRecordId(mockRecordId);
            mockRecordMedia.setMediaId(mockMediaId);

            // メディアのモックを作成
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId);

            // はじめて記録取得処理のスタブ化
            when(firstRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(firstRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of(mockRecordMedia));

            // ユーザーID取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // メディア取得処理のスタブ化
            when(mediaRepository.findByIds(List.of(mockMediaId), mockUserId))
                .thenReturn(List.of(mockMedia));

            // 対象を実行し結果を取得
            List<FirstRecordService.FirstRecordWithMedia> result = firstRecordService.getFirstRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList()).hasSize(1);
            assertThat(result.getFirst().mediaList().getFirst().getId()).isEqualTo(mockMediaId);
        }

        @Test
        @DisplayName("First-05: 記録が0件で空リストが返ること")
        void getFirstRecords_empty() {
            // 各処理のスタブ化
            when(firstRecordRepository.findAll()).thenReturn(List.of());

            // 対象を実行し結果を取得
            List<FirstRecordService.FirstRecordWithMedia> result = firstRecordService.getFirstRecords();

            // 結果の確認
            assertThat(result.size()).isZero();

            // 後続の取得処理が呼ばれていないことを確認
            verify(firstRecordRepository, never()).findMediaByRecordIds(any());
        }

        @Test
        @DisplayName("First-06: メディアが紐づかない記録に記録のみが返ること")
        void getFirstRecords_noMedia() {
            // モックデータの作成
            Long mockRecordId = 1L;

            // はじめて記録のモックを作成
            FirstRecords mockRecord = new FirstRecords();
            mockRecord.setId(mockRecordId);

            // はじめて記録取得処理のスタブ化
            when(firstRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(firstRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of());

            // 対象を実行し結果を取得
            List<FirstRecordService.FirstRecordWithMedia> result = firstRecordService.getFirstRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList().size()).isZero();
        }

        @Test
        @DisplayName("First-07: ゴミ箱内のメディアを含む場合除外された値が返ること")
        void getFirstRecords_trashItem() {
            // モックデータの作成
            Long mockRecordId = 1L;
            Long mockMediaId1 = 2L;
            Long mockMediaId2 = 3L;
            Long mockUserId = 4L;

            // はじめて記録のモックを作成
            FirstRecords mockRecord = new FirstRecords();
            mockRecord.setId(mockRecordId);

            // 記録メディア情報のモックを作成
            FirstRecordMedia mockRecordMedia1 = new FirstRecordMedia();
            mockRecordMedia1.setFirstRecordId(mockRecordId);
            mockRecordMedia1.setMediaId(mockMediaId1);
            FirstRecordMedia mockRecordMedia2 = new FirstRecordMedia();
            mockRecordMedia2.setFirstRecordId(mockRecordId);
            mockRecordMedia2.setMediaId(mockMediaId2);

            // メディアのモックを作成
            Media mockMedia = new Media();
            mockMedia.setId(mockMediaId1);

            // はじめて記録取得処理のスタブ化
            when(firstRecordRepository.findAll()).thenReturn(List.of(mockRecord));

            // 記録に紐づくメディア情報の取得処理をスタブ化
            when(firstRecordRepository.findMediaByRecordIds(List.of(mockRecordId)))
                .thenReturn(List.of(mockRecordMedia1, mockRecordMedia2));

            // ユーザーID取得処理のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // メディア取得処理のスタブ化 - ID2件に対し1件フィルタリング想定で1件のみ返却
            when(mediaRepository.findByIds(List.of(mockMediaId1, mockMediaId2), mockUserId))
                .thenReturn(List.of(mockMedia));

            // 対象を実行し結果を取得
            List<FirstRecordService.FirstRecordWithMedia> result = firstRecordService.getFirstRecords();

            // 各種結果の検証
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().record().getId()).isEqualTo(mockRecordId);
            assertThat(result.getFirst().mediaList()).hasSize(1);
            assertThat(result.getFirst().mediaList().getFirst().getId()).isEqualTo(mockMediaId1);
        }
    }

    @Nested
    @DisplayName("updateFirstRecord - はじめて記録の更新")
    class UpdateFirstRecord {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        // 共通モックデータの作成
        Long mockCurrentUserId = 2L;

        // 共通リクエストデータの作成ヘルパー
        private FirstRecordRequestDto createRequest(List<Long> mediaIds) {
            FirstRecordRequestDto request = new FirstRecordRequestDto();
            request.setTitle("はじめてのつかまり立ち");
            request.setComment("台につかまって立った");
            request.setRecordedDate(LocalDate.now());
            request.setMediaIds(mediaIds);
            return request;
        }

        @Test
        @DisplayName("First-08: はじめて記録の更新ができること")
        void updateFirstRecord_success() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            FirstRecordRequestDto request = createRequest(mockMediaIds);

            // モックデータの作成
            FirstRecords record = new FirstRecords();

            // 各処理のスタブ化
            when(firstRecordRepository.findById(requestId)).thenReturn(Optional.of(record));
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);
            when(mediaRepository.findByIds(mockMediaIds, mockCurrentUserId)).thenReturn(List.of(new Media()));

            // 対象の実行
            firstRecordService.updateFirstRecord(requestId, request);

            // 画像削除処理/更新処理/画像保存処理が呼ばれていることの確認
            verify(firstRecordRepository).deleteMediaByRecordId(requestId);
            verify(firstRecordRepository).update(any(FirstRecords.class));
            verify(firstRecordRepository).saveMedia(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(mockMediaIds.getFirst())
            ));
        }

        @Test
        @DisplayName("First-09: 存在しないIDで更新リクエストをした場合例外で処理されること")
        void updateFirstRecord_notFound() {
            // リクエストデータの作成
            List<Long> mockMediaIds = List.of(1L);
            FirstRecordRequestDto request = createRequest(mockMediaIds);

            // 各処理のスタブ化
            when(firstRecordRepository.findById(requestId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> firstRecordService.updateFirstRecord(requestId, request))
                .isInstanceOf(ResourceNotFoundException.class);

            // 画像削除処理/更新処理/画像保存処理が呼ばれていないことの確認
            verify(firstRecordRepository, never()).deleteMediaByRecordId(requestId);
            verify(firstRecordRepository, never()).update(any());
            verify(firstRecordRepository, never()).saveMedia(any());
        }
    }

    @Nested
    @DisplayName("deleteFirstRecord - はじめて記録の削除")
    class DeleteFirstRecord {
        @Test
        @DisplayName("First-10: はじめて記録の削除ができること")
        void deleteFirstRecord_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // モックデータの作成
            FirstRecords record = new FirstRecords();

            // 各処理のスタブ化
            when(firstRecordRepository.findById(requestId)).thenReturn(Optional.of(record));

            // 対象の実行
            firstRecordService.deleteFirstRecord(requestId);

            // 画像削除処理/記録削除処理が呼ばれていることの確認
            verify(firstRecordRepository).deleteMediaByRecordId(requestId);
            verify(firstRecordRepository).deleteById(requestId);
        }
    }
}
