package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.MediaCommentConverter;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.MediaCommentRequestDto;
import link.s_repo.chii_piyo.model.gen.MediaCommentResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaComments;
import link.s_repo.chii_piyo.model.gen.Users;
import link.s_repo.chii_piyo.service.MediaCommentService;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MediaCommentController.class)
public class MediaCommentControllerTest extends BaseControllerTest {
    @MockitoBean
    private MediaCommentService mediaCommentService;
    @MockitoBean
    private MediaCommentConverter mediaCommentConverter;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MediaService mediaService;

    @Nested
    @DisplayName("getMediaComments - コメント一覧の取得")
    class GetMediaComments {
        Long mockRequestId = 1L;
        Long mockResponseId = 2L;

        @Test
        @WithMockUser
        @DisplayName("CommentCtrl-01: コメント一覧の取得ができること")
        void getMediaComments_success() throws Exception {
            // 処理内で使用されるユーザーデータのモックを作成
            Long mockUserId = 1L;
            Users mockUser = new Users();
            mockUser.setId(mockUserId);

            // コメントデータのモックを作成
            MediaComments comment = new MediaComments();
            comment.setUserId(mockUserId);

            // コメント取得のスタブ化
            when(mediaCommentService.getMediaComments(mockRequestId)).thenReturn(List.of(comment));

            // レスポンス取得のスタブ化
            when(mediaCommentConverter.toMediaCommentResponseDto(any(), any()))
                .thenReturn(new MediaCommentResponseDto().id(mockResponseId));

            // ユーザー情報取得のスタブ化
            when(userService.getUsersById(List.of(mockUserId))).thenReturn(List.of(mockUser));

            // GETリクエストの送信
            mockMvc.perform(get("/media/{mediaId}/comments", mockRequestId))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockResponseId));

            // リクエストで取得処理が行われているか検証
            verify(mediaCommentService).getMediaComments(mockRequestId);
            verify(userService).getUsersById(List.of(mockUserId));
            verify(mediaCommentConverter).toMediaCommentResponseDto(comment, mockUser);
        }

        @Test
        @WithMockUser
        @DisplayName("CommentCtrl-02: コメントのないメディアIDのリクエストに空リストを返すこと")
        void getMediaComments_emptyComment() throws Exception {
            // コメント取得のスタブ化
            when(mediaCommentService.getMediaComments(mockRequestId)).thenReturn(List.of());

            // GETリクエストの送信
            mockMvc.perform(get("/media/{mediaId}/comments", mockRequestId))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("createMediaComment - コメントの作成")
    class CreateMediaComment {
        // モックデータの作成
        Long mockUserId = 1L;
        Long mockRequestId = 2L;
        String mockContent = "可愛い！";

        @Test
        @WithMockUser
        @DisplayName("CommentCtrl-03: コメントの作成ができること")
        void createMediaComment_success() throws Exception {

            MediaCommentRequestDto request = new MediaCommentRequestDto();
            request.setContent(mockContent);

            // ユーザーID取得のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // POSTリクエストの送信
            mockMvc.perform(post("/media/{mediaId}/comments", mockRequestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // コメント作成処理が呼ばれているか確認
            verify(mediaCommentService).createMediaComment(mockRequestId, mockUserId, mockContent);
        }

        @Test
        @WithMockUser
        @DisplayName("CommentCtrl-04: 存在しないメディアへのコメント追加リクエストに404を返すこと")
        void createMediaComment_notFound() throws Exception {
            // ユーザーID取得のスタブ化
            MediaCommentRequestDto request = new MediaCommentRequestDto();
            request.setContent(mockContent);

            // メディア取得処理のスタブ化
            when(mediaService.getMedia(any()))
                .thenThrow(new ResourceNotFoundException("メディアが見つかりません mediaId=" + mockRequestId));

            // POSTリクエストの送信
            mockMvc.perform(post("/media/{mediaId}/comments", mockRequestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // コメント作成処理が呼ばれていないか確認
            verify(mediaCommentService, never()).createMediaComment(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("deleteMediaComment - コメントを削除")
    class DeleteMediaComment {
        // モックデータの作成
        Long mockUserId = 1L;
        Long mockRequestId = 2L;
        MediaComments mockComment = new MediaComments();

        @Test
        @WithMockUser
        @DisplayName("CommentCtrl-05: コメントを削除できること")
        void deleteMediaComment_success() throws Exception {
            // ユーザーID取得のスタブ化
            when(currentUserProvider.getUserId()).thenReturn(mockUserId);

            // 取得処理のスタブ化
            when(mediaCommentService.getMediaComment(mockRequestId)).thenReturn(mockComment);

            // DELETEリクエストの送信
            mockMvc.perform(delete("/media/comments/{id}", mockRequestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // コメント削除処理が呼ばれているか確認
            verify(mediaCommentService).deleteMediaComment(mockComment, mockUserId);
        }
    }
}
