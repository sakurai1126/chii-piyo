package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaTagsUpdateRequestDto;
import link.s_repo.chii_piyo.model.gen.TagRequestDto;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
public class TagControllerTest extends BaseControllerTest {
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private TagConverter tagConverter;
    @MockitoBean
    private MediaService mediaService;


    @Nested
    @DisplayName("getTags - タグの取得")
    class GetTags {

        @Test
        @WithMockUser
        @DisplayName("TagCtrl-01: タグ一覧を取得できること")
        void getTags_success() throws Exception {
            Long mockTagId = 1L;
            String mockTagName = "お散歩";
            Long mockMediaCount = 2L;

            // テスト用のダミーデータを生成
            Tags mockTag = new Tags();
            mockTag.setId(mockTagId);
            mockTag.setName(mockTagName);

            // サービスから返されるデータにダミーを設定
            when(tagService.getTags()).thenReturn(List.of(mockTag));
            when(tagService.getMediaCountByTagId()).thenReturn(Map.of(mockTagId, mockMediaCount));

            // コンバーターで変換された後に返されるDTOを設定
            TagResponseDto expectedDto = new TagResponseDto();
            expectedDto.setId(mockTagId);
            expectedDto.setName(mockTagName);
            expectedDto.setMediaCount(mockMediaCount);

            when(tagConverter.toTagResponseDto(mockTag, mockMediaCount)).thenReturn(expectedDto);

            // mockMvc.performでHTTPリクエストを送信しステータスを検証
            mockMvc.perform(get("/tags"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // 配列の1件目の各プロパティが期待通りか検証
                .andExpect(jsonPath("$[0].id").value(mockTagId))
                .andExpect(jsonPath("$[0].name").value(mockTagName))
                .andExpect(jsonPath("$[0].mediaCount").value(mockMediaCount));

            // 取得処理が呼ばれていることを検証
            verify(tagService).getTags();
            verify(tagService).getMediaCountByTagId();
            verify(tagConverter).toTagResponseDto(mockTag, mockMediaCount);
        }
    }

    @Nested
    @DisplayName("createTag - タグの作成")
    class CreateTag {
        String mockTagName = "お散歩";

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-02: タグを作成できること")
        void createTag_success() throws Exception {

            // リクエストボディの作成
            TagRequestDto request = new TagRequestDto();
            request.setName(mockTagName);

            // POSTリクエストの送信
            mockMvc.perform(post("/tags")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを検証
            verify(tagService).createTag(mockTagName);
        }

        @Test
        @WithMockUser
        @DisplayName("TagCtrl-03: 一般ユーザーで作成を試みた場合アクセスが拒否されること")
        void createTag_forbidden() throws Exception {
            // リクエストボディの作成
            TagRequestDto request = new TagRequestDto();
            request.setName(mockTagName);

            // POSTリクエストの送信
            mockMvc.perform(post("/tags")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていないことを検証
            verify(tagService, never()).createTag(any());
        }
    }

    @Nested
    @DisplayName("updateTag - タグの更新")
    class UpdateTag {
        String mockTagName = "お散歩";
        Long requestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-04: タグを更新できること")
        void updateTag_success() throws Exception {
            // リクエストボディの作成
            TagRequestDto request = new TagRequestDto();
            request.setName(mockTagName);

            // PUTリクエストの送信
            mockMvc.perform(put("/tags/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを検証
            verify(tagService).updateTag(requestId, mockTagName);
        }

        @Test
        @WithMockUser
        @DisplayName("TagCtrl-05: 一般ユーザーで更新を試みた場合アクセスが拒否されること")
        void updateTag_forbidden() throws Exception {
            // リクエストボディの作成
            TagRequestDto request = new TagRequestDto();
            request.setName(mockTagName);

            // PUTリクエストの送信
            mockMvc.perform(put("/tags/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを検証
            verify(tagService, never()).updateTag(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-06: 空の更新内容で更新した場合拒否されること")
        void updateTag_emptyName() throws Exception {
            // リクエストボディの作成
            TagRequestDto request = new TagRequestDto();

            // PUTリクエストの送信
            mockMvc.perform(put("/tags/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 400 Bad Requestであることを確認
                .andExpect(status().isBadRequest());

            // 更新処理が呼ばれていないことを検証
            verify(tagService, never()).updateTag(any(), any());
        }
    }

    @Nested
    @DisplayName("updateMediaTags - タグに紐づくメディアの更新")
    class UpdateMediaTags {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-07: メディアのタグを一括更新できること")
        void updateMediaTags_success() throws Exception {
            // リクエストボディの作成
            MediaTagsUpdateRequestDto request = new MediaTagsUpdateRequestDto();
            request.setTagIds(List.of(1L, 2L));

            // サービスから返されるデータにダミーを設定
            when(mediaService.getMedia(1L)).thenReturn(new Media());

            // PUTリクエストの送信
            mockMvc.perform(put("/media/1/tags")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを検証
            verify(tagService).syncMediaTags(1L, List.of(1L, 2L));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-08: 存在しないメディアIDを渡した場合拒否されること")
        void updateMediaTags_notFound() throws Exception {
            // リクエストボディの作成
            MediaTagsUpdateRequestDto request = new MediaTagsUpdateRequestDto();

            // mediaService.getMedia(1L) が呼ばれたときに ResourceNotFoundException をスローするように設定
            when(mediaService.getMedia(1L))
                .thenThrow(new ResourceNotFoundException("メディアが見つかりません mediaId=1"));

            // PUTリクエストの送信
            mockMvc.perform(put("/media/1/tags")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // 更新処理が呼ばれていないことを検証
            verify(tagService, never()).syncMediaTags(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("TagCtrl-09: 一般ユーザーでメディアタグ更新を試みた場合アクセスが拒否されること")
        void updateMediaTags_forbidden() throws Exception {
            // リクエストボディの作成
            MediaTagsUpdateRequestDto request = new MediaTagsUpdateRequestDto();

            // PUTリクエストの送信
            mockMvc.perform(put("/media/1/tags")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを検証
            verify(tagService, never()).syncMediaTags(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteTag - タグの削除")
    class DeleteTag {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("TagCtrl-10: タグを削除できること")
        void deleteTag_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/tags/1"))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(tagService).deleteTag(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("TagCtrl-11: 一般ユーザーで削除を試みた場合アクセスが拒否されること")
        void deleteTag_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/tags/1"))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを検証
            verify(tagService, never()).deleteTag(any());
        }
    }
}
