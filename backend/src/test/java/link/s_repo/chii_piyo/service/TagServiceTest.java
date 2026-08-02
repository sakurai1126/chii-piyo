package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.TagMediaCount;
import link.s_repo.chii_piyo.model.gen.MediaTags;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Nested
    @DisplayName("createTag - タグの作成")
    class CreateTag {
        @Test
        @DisplayName("Tag-01: タグを作成できること")
        void createTag_success() {
            // リクエストデータの作成
            String requestName = "お出かけ";

            // 対象の実行
            tagService.createTag(requestName);

            // 登録処理が呼ばれたことの確認
            verify(tagRepository).save(any(Tags.class));
        }
    }


    @Nested
    @DisplayName("getTags - タグの取得")
    class GetTags {
        @Test
        @DisplayName("Tag-02: タグの取得処理ができること")
        void getTags_success() {
            // モックデータの作成
            Long mockTagId = 1L;
            Tags mockTags = new Tags();
            mockTags.setId(mockTagId);

            // 取得処理のスタブ化
            when(tagRepository.findAllOrderById()).thenReturn(List.of(mockTags));

            // 対象の実行
            List<Tags> result = tagService.getTags();

            // 結果の検証
            assertThat(result.getFirst().getId()).isEqualTo(mockTagId);
        }
    }

    @Nested
    @DisplayName("getTagById - ID指定でのタグの取得")
    class GetTagById {
        // 共通リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("Tag-03: ID指定でのタグの取得ができること")
        void getTagById_success() {
            // モックデータの作成
            Long mockTagId = 1L;
            Tags mockTag = new Tags();
            mockTag.setId(mockTagId);

            // 取得処理のスタブ化
            when(tagRepository.findById(requestId)).thenReturn(Optional.of(mockTag));

            // 対象の実行
            Tags result = tagService.getTagById(requestId);

            // 結果の検証
            assertThat(result.getId()).isEqualTo(mockTagId);
        }

        @Test
        @DisplayName("Tag-04: 存在しないタグID指定に例外処理を返すこと")
        void getTagById_notFound() {
            // 取得処理のスタブ化
            when(tagRepository.findById(requestId)).thenReturn(Optional.empty());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> tagService.getTagById(requestId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getMediaTags - メディアに紐づくタグ一覧を取得")
    class GetMediaTags {
        // 共通リクエストデータの作成
        Long requestMediaId = 1L;

        @Test
        @DisplayName("Tag-05: メディアに紐づくタグ一覧を取得できること")
        void getMediaTags_success() {
            // モックデータの作成
            String mockTagName = "お出かけ";
            Long mockTagId = 2L;
            MediaTags mockMediaTag = new MediaTags();
            mockMediaTag.setTagId(mockTagId);

            Tags mockTag = new Tags();
            mockTag.setName(mockTagName);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId)).thenReturn(List.of(mockMediaTag));
            when(tagRepository.findByIds(List.of(mockTagId))).thenReturn(List.of(mockTag));

            // 対象の実行
            List<Tags> result = tagService.getMediaTags(requestMediaId);

            // 結果の検証
            assertThat(result.getFirst().getName()).isEqualTo(mockTagName);
        }

        @Test
        @DisplayName("Tag-06: タグが紐づかないメディアIDを渡すと空リストを返すこと")
        void getMediaTags_emptyTag() {
            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId)).thenReturn(List.of());

            // 対象の実行
            List<Tags> result = tagService.getMediaTags(requestMediaId);

            // 結果の検証
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことの確認
            verify(tagRepository, never()).findByIds(any());
        }
    }

    @Nested
    @DisplayName("syncMediaTags - メディアに紐づくタグを一括更新")
    class SyncMediaTags {
        // 共通リクエストの作成
        Long requestMediaId = 1L;
        Long currentTagId = 2L;
        Long deleteTargetId = 3L;
        Long newTagId = 4L;

        // メディアタグデータの作成ヘルパー
        private MediaTags createMediaTags(Long mediaId, Long tagId) {
            MediaTags mediaTag = new MediaTags();
            mediaTag.setTagId(tagId);
            mediaTag.setMediaId(mediaId);
            return mediaTag;
        }

        @Test
        @DisplayName("Tag-07: 既存タグに無いタグIDを渡しメディアに紐づくタグを一括で追加更新できること")
        void syncMediaTags_add() {
            // モックデータの作成
            MediaTags mockMediaTag = createMediaTags(requestMediaId, currentTagId);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId))
                .thenReturn(List.of(mockMediaTag));

            // 新規タグIDを入れて対象の実行
            tagService.syncMediaTags(requestMediaId, List.of(currentTagId, newTagId));

            // 削除処理が呼ばれていないことの確認
            verify(tagRepository, never()).deleteMediaTagsByMediaIdAndTagIds(any(), any());

            // 追加処理が想定通りの引数で呼ばれたことの確認
            verify(tagRepository).saveMediaTags(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(requestMediaId) && list.getFirst().getTagId().equals(newTagId)
            ));
        }

        @Test
        @DisplayName("Tag-08: 既存タグにあるタグIDの一部のみを渡しメディアに紐づくタグを一括で追加削除できること")
        void syncMediaTags_remove() {
            // モックデータの作成
            MediaTags mockMediaTag = createMediaTags(requestMediaId, currentTagId);
            MediaTags mockMediaTagDeleteTarget = createMediaTags(requestMediaId, deleteTargetId);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId))
                .thenReturn(List.of(mockMediaTag, mockMediaTagDeleteTarget));

            // 削除対象の既存タグIDを除外して対象の実行
            tagService.syncMediaTags(requestMediaId, List.of(currentTagId));

            // 削除処理が呼ばれていることの確認
            verify(tagRepository).deleteMediaTagsByMediaIdAndTagIds(requestMediaId,
                List.of(deleteTargetId));

            // 追加処理が呼ばれていないことの確認
            verify(tagRepository, never()).saveMediaTags(any());
        }

        @Test
        @DisplayName("Tag-09: 追加削除両方の処理が同時にできること")
        void syncMediaTags_addAndRemove() {
            // モックデータの作成
            MediaTags mockMediaTag = createMediaTags(requestMediaId, currentTagId);
            MediaTags mockMediaTagDeleteTarget = createMediaTags(requestMediaId, deleteTargetId);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId))
                .thenReturn(List.of(mockMediaTag, mockMediaTagDeleteTarget));

            // 対象の実行
            tagService.syncMediaTags(requestMediaId, List.of(currentTagId, newTagId));

            // 削除処理が呼ばれていることの確認
            verify(tagRepository).deleteMediaTagsByMediaIdAndTagIds(requestMediaId,
                List.of(deleteTargetId));

            // 追加処理が想定通りの引数で呼ばれたことの確認
            verify(tagRepository).saveMediaTags(argThat(list ->
                list.size() == 1 && list.getFirst().getMediaId().equals(requestMediaId) && list.getFirst().getTagId().equals(newTagId)
            ));
        }

        @Test
        @DisplayName("Tag-10: 既存タグと同一のタグIDを渡すと追加も削除も行われないこと")
        void syncMediaTags_noChange() {
            // モックデータの作成
            MediaTags mockMediaTag = createMediaTags(requestMediaId, currentTagId);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId)).thenReturn(List.of(mockMediaTag));

            // 対象の実行
            tagService.syncMediaTags(requestMediaId, List.of(currentTagId));

            // 削除処理が呼ばれていないことの確認
            verify(tagRepository, never()).deleteMediaTagsByMediaIdAndTagIds(any(), any());

            // 追加処理が呼ばれていないことの確認
            verify(tagRepository, never()).saveMediaTags(any());
        }

        @Test
        @DisplayName("Tag-11: 空のタグIDリストを渡すことで全件削除できること")
        void syncMediaTags_emptyList() {
            // モックデータの作成
            MediaTags mockMediaTag = createMediaTags(requestMediaId, currentTagId);

            // 取得処理のスタブ化
            when(tagRepository.findMediaTagsByMediaId(requestMediaId)).thenReturn(List.of(mockMediaTag));

            // 対象の実行
            tagService.syncMediaTags(requestMediaId, List.of());

            // 削除処理が呼ばれていることの確認
            verify(tagRepository).deleteMediaTagsByMediaIdAndTagIds(requestMediaId,
                List.of(currentTagId));

            // 追加処理が呼ばれていないことの確認
            verify(tagRepository, never()).saveMediaTags(any());
        }
    }

    @Nested
    @DisplayName("getMediaCountByTagId - タグIDごとのメディア数取得")
    class GetMediaCountByTagId {
        @Test
        @DisplayName("Tag-12: タグIDごとのメディア数を取得できること")
        void getMediaCountByTagId_success() {
            // モックデータの作成
            Long mockTagId = 1L;
            Long mockMediaCount = 2L;

            TagMediaCount mockTagMediaCount = new TagMediaCount();
            mockTagMediaCount.setTagId(mockTagId);
            mockTagMediaCount.setMediaCount(mockMediaCount);

            // 取得処理のスタブ化
            when(tagRepository.selectMediaCountByTagId()).thenReturn(List.of(mockTagMediaCount));

            // 対象の実行
            Map<Long, Long> result = tagService.getMediaCountByTagId();

            // 結果の検証
            assertThat(result).hasSize(1).containsEntry(mockTagId, mockMediaCount);
        }
    }

    @Nested
    @DisplayName("updateTag - タグの更新")
    class UpdateTag {
        Long requestId = 1L;
        String requestName = "お出かけ";

        @Test
        @DisplayName("Tag-13: タグ名の更新ができること")
        void updateTag_success() {
            // モックデータの作成
            Tags mockTag = new Tags();

            // 取得処理のスタブ化
            when(tagRepository.findById(requestId)).thenReturn(Optional.of(mockTag));

            // 対象の実行
            tagService.updateTag(requestId, requestName);

            // 新しい名前の引数で更新処理が呼ばれていることの確認
            verify(tagRepository).update(argThat(tag ->
                requestName.equals(tag.getName())
            ));
        }

        @Test
        @DisplayName("Tag-14: 存在しないタグIDを渡すと例外処理で返すこと")
        void updateTag_notFound() {
            // 取得処理のスタブ化
            when(tagRepository.findById(requestId)).thenReturn(Optional.empty());

            // 例外が吐かれるか確認
            assertThatThrownBy(() -> tagService.updateTag(requestId, requestName))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteTag - タグの削除")
    class DeleteTag {
        @Test
        @DisplayName("Tag-15: タグの削除ができること")
        void deleteTag_success() {
            // リクエストデータの作成
            Long requestId = 1L;

            // 対象の実行
            tagService.deleteTag(requestId);

            // 削除処理が呼ばれたことの確認
            verify(tagRepository).deleteMediaTagsByTagId(requestId);
            verify(tagRepository).delete(requestId);
        }
    }

    @Nested
    @DisplayName("count - タグIDリストから一致するタグの件数取得")
    class Count {
        @Test
        @DisplayName("Tag-16: タグIDリストから一致するタグの件数取得ができること")
        void count_success() {
            // リクエストデータの作成
            List<Long> requestTagIds = List.of(1L);
            Long mockCountResult = 2L;

            // 取得処理のスタブ化
            when(tagRepository.countByTagIds(requestTagIds)).thenReturn(mockCountResult);

            // 対象の実行
            Long result = tagService.count(requestTagIds);

            // 結果の確認
            assertThat(result).isEqualTo(mockCountResult);

            // 取得処理が行われたことの確認
            verify(tagRepository).countByTagIds(requestTagIds);
        }
    }
}
