package link.s_repo.chii_piyo.service;

import link.s_repo.chii_piyo.exception.ResourceAccessDeniedException;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.repository.MediaCommentRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MediaCommentServiceTest {
    @Mock
    private MediaCommentRepository mediaCommentRepository;

    @InjectMocks
    private MediaCommentService mediaCommentService;

    @Nested
    @DisplayName("createMediaComment - コメントの作成")
    class CreateMediaComment {
        @Test
        @DisplayName("Comment-01: コメントの作成ができること")
        void createMediaComment_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;
            Long requestUserId = 2L;
            String requestContent = "かわいい！";

            // 対象の実行
            mediaCommentService.createMediaComment(requestMediaId, requestUserId, requestContent);

            // リクエストデータで保存処理が呼ばれているか検証
            verify(mediaCommentRepository).save(
                argThat(comment -> comment.getMediaId().equals(requestMediaId)
                    && comment.getUserId().equals(requestUserId)
                    && comment.getContent().equals(requestContent)
                ));
        }
    }

    @Nested
    @DisplayName("getMediaComment - コメントのID指定取得")
    class GetMediaComment {
        // 共有リクエストデータの作成
        Long requestId = 1L;

        @Test
        @DisplayName("Comment-02: コメントのID指定取得ができること")
        void getMediaComments_success() {
            // モックデータの作成
            MediaComments mockComment = new MediaComments();

            // 取得処理のスタブ化
            when(mediaCommentRepository.findById(requestId)).thenReturn(Optional.of(mockComment));

            // 対象の実行
            MediaComments result = mediaCommentService.getMediaComment(requestId);

            // 取得結果の検証
            assertThat(result).isSameAs(mockComment);

            // 取得処理が呼ばれているか検証
            verify(mediaCommentRepository).findById(requestId);
        }

        @Test
        @DisplayName("Comment-03: 存在しないID指定でコメントを取得しようとした際例外で処理されること")
        void getMediaComments_notFound() {
            // 取得処理のスタブ化
            when(mediaCommentRepository.findById(requestId)).thenReturn(Optional.empty());

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaCommentService.getMediaComment(requestId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getMediaComments - メディアに紐づくコメントの取得")
    class GetMediaComments {
        @Test
        @DisplayName("Comment-04: メディアに紐づくコメントの取得ができること")
        void getMediaComments_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;

            // 対象の実行
            mediaCommentService.getMediaComments(requestMediaId);

            // 取得処理が呼ばれているか検証
            verify(mediaCommentRepository).findByMediaId(requestMediaId);
        }
    }

    @Nested
    @DisplayName("getCommentCountsByMediaIds - コメントの件数取得")
    class GetCommentCountsByMediaIds {
        @Test
        @DisplayName("Comment-05: メディアごとのコメント件数が取得できること")
        void getCommentCountsByMediaIds_success() {
            // リクエストデータの作成
            Long requestMediaId = 1L;

            // モックデータの作成
            MediaComments mockComment = new MediaComments();
            mockComment.setMediaId(requestMediaId);

            // 取得処理のスタブ化
            when(mediaCommentRepository.findByMediaIds(List.of(requestMediaId))).thenReturn(List.of(mockComment));

            // 対象の実行
            Map<Long, Long> result =
                mediaCommentService.getCommentCountsByMediaIds(List.of(requestMediaId));

            // 結果の確認
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(requestMediaId)).isEqualTo(1L);

            // 取得処理が呼ばれていることを確認
            verify(mediaCommentRepository).findByMediaIds(List.of(requestMediaId));
        }

        @Test
        @DisplayName("Comment-06: 空のメディアIDリストでリクエストした場合空マップが返ること")
        void getCommentCountsByMediaIds_empty() {
            // 対象の実行
            Map<Long, Long> result =
                mediaCommentService.getCommentCountsByMediaIds(List.of());

            // 結果の確認
            assertThat(result.size()).isZero();

            // 取得処理が呼ばれていないことを確認
            verify(mediaCommentRepository, never()).findByMediaIds(any());
        }
    }

    @Nested
    @DisplayName("deleteMediaComment - コメントの削除")
    class DeleteMediaComment {
        @Test
        @DisplayName("Comment-07: 自身のコメントを削除できること")
        void deleteMediaComment_success() {
            // リクエストデータの作成
            MediaComments requestComment = new MediaComments();
            Long mockCurrentUserId = 1L;

            Long mockId = 2L;
            requestComment.setUserId(mockCurrentUserId);
            requestComment.setId(mockId);

            // 対象の実行
            mediaCommentService.deleteMediaComment(requestComment, mockCurrentUserId);

            // 削除処理が呼ばれていることを確認
            verify(mediaCommentRepository).delete(mockId);
        }

        @Test
        @DisplayName("Comment-08: 他ユーザーのコメントを削除しようとすると例外がスローされること")
        void deleteMediaComment_unauthorized() {
            // リクエストデータの作成
            MediaComments requestComment = new MediaComments();
            Long mockCurrentUserId = 1L;

            // ユーザーIDに別IDを指定
            requestComment.setUserId(2L);

            // 対象を実行し例外が吐かれるか確認
            assertThatThrownBy(() -> mediaCommentService.deleteMediaComment(requestComment, mockCurrentUserId))
                .isInstanceOf(ResourceAccessDeniedException.class);

            // 削除処理が呼ばれていないことを確認
            verify(mediaCommentRepository, never()).delete(any());
        }
    }
}
