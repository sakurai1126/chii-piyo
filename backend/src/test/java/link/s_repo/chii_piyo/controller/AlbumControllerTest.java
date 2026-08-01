package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.AlbumConverter;
import link.s_repo.chii_piyo.model.gen.AlbumMediaAddRequestDto;
import link.s_repo.chii_piyo.model.gen.AlbumRequestDto;
import link.s_repo.chii_piyo.model.gen.AlbumResponseDto;
import link.s_repo.chii_piyo.model.gen.Albums;
import link.s_repo.chii_piyo.service.AlbumService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
public class AlbumControllerTest extends BaseControllerTest {
    @MockitoBean
    private AlbumService albumService;
    @MockitoBean
    private AlbumConverter albumConverter;

    @Nested
    @DisplayName("getAlbums - アルバム一覧取得")
    class GetAlbums {
        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-01: アルバム一覧を取得できること")
        void getAlbums_success() throws Exception {
            // モックデータの作成
            Long mockAlbumId1 = 1L;
            Long mockAlbumId2 = 2L;
            String mockAlbumTitle = "運動会";
            AlbumResponseDto response = new AlbumResponseDto()
                .id(mockAlbumId1)
                .title(mockAlbumTitle);
            Albums album1 = new Albums();
            album1.setId(mockAlbumId1);
            Albums album2 = new Albums();
            album2.setId(mockAlbumId2);
            List<Long> albumIds = List.of(mockAlbumId1, mockAlbumId2);

            // メディアデータのモック作成
            Map<Long, AlbumService.MediaDataResult> mediaDataMap = Map.of(
                mockAlbumId1, new AlbumService.MediaDataResult(0, 0, Collections.emptyList()),
                mockAlbumId2, new AlbumService.MediaDataResult(0, 0, Collections.emptyList()));

            // アルバム取得のスタブ化
            when(albumService.getAlbums()).thenReturn(List.of(album1, album2));
            // メディア取得のスタブ化
            when(albumService.getMediaDataByAlbumIds(albumIds)).thenReturn(mediaDataMap);
            // レスポンス取得のスタブ化
            when(albumConverter.toAlbumResponseDto(any(), any())).thenReturn(response);

            // GETリクエストの送信
            mockMvc.perform(get("/albums"))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // 2件で返ってきているか確認
                .andExpect(jsonPath("$.length()").value(2))
                // 配列の1件目のプロパティが期待通りか検証
                .andExpect(jsonPath("$[0].id").value(mockAlbumId1))
                .andExpect(jsonPath("$[0].title").value(mockAlbumTitle));

            // 呼び出しが行われているかの検証
            verify(albumService).getAlbums();
            verify(albumService).getMediaDataByAlbumIds(albumIds);
            verify(albumConverter, times(2)).toAlbumResponseDto(any(), any());
        }
    }

    @Nested
    @DisplayName("getAlbum - アルバム単体取得")
    class GetAlbum {
        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-02: ID指定でアルバムを取得できること")
        void getAlbum_success() throws Exception {
            // モックデータの作成
            Long mockAlbumId = 1L;
            String mockAlbumTitle = "運動会";
            AlbumResponseDto response = new AlbumResponseDto()
                .id(mockAlbumId)
                .title(mockAlbumTitle);
            Albums album = new Albums();
            album.setId(mockAlbumId);
            List<Long> albumIds = List.of(mockAlbumId);

            // メディアデータのモック作成
            Map<Long, AlbumService.MediaDataResult> mediaDataMap = Map.of(
                mockAlbumId, new AlbumService.MediaDataResult(0, 0, Collections.emptyList())
            );

            // アルバム取得のスタブ化（不足していたため追加）
            when(albumService.getAlbumById(mockAlbumId)).thenReturn(album);
            // メディア取得のスタブ化
            when(albumService.getMediaDataByAlbumIds(albumIds)).thenReturn(mediaDataMap);
            // レスポンス取得のスタブ化
            when(albumConverter.toAlbumResponseDto(any(), any())).thenReturn(response);

            // GETリクエストの送信
            mockMvc.perform(get("/albums/{albumId}", mockAlbumId))
                // ステータスコード 200 OKであることを確認
                .andExpect(status().isOk())
                // プロパティが期待通りか検証
                .andExpect(jsonPath("$.id").value(mockAlbumId))
                .andExpect(jsonPath("$.title").value(mockAlbumTitle));

            // 呼び出しが行われているかの検証
            verify(albumService).getAlbumById(mockAlbumId);
            verify(albumService).getMediaDataByAlbumIds(albumIds);
            verify(albumConverter).toAlbumResponseDto(any(), any());
        }
    }

    @Nested
    @DisplayName("createAlbum - アルバム作成")
    class CreateAlbum {
        // 共通モックデータの作成
        String mockAlbumTitle = "運動会";

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("AlbumCtrl-03: アルバムを作成できること")
        void createAlbum_success() throws Exception {
            // リクエストボディの作成
            AlbumRequestDto request = new AlbumRequestDto();
            request.setTitle(mockAlbumTitle);

            // POSTリクエストの送信
            mockMvc.perform(post("/albums")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated());

            // 作成処理が呼ばれていることを検証
            verify(albumService).createAlbum(mockAlbumTitle);
        }

        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-04: 一般ユーザーでアルバム作成を試みた場合アクセスが拒否されること")
        void createAlbum_forbidden() throws Exception {
            // リクエストボディの作成
            AlbumRequestDto request = new AlbumRequestDto();
            request.setTitle(mockAlbumTitle);

            // POSTリクエストの送信
            mockMvc.perform(post("/albums")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 作成処理が呼ばれていないことを検証
            verify(albumService, never()).createAlbum(mockAlbumTitle);
        }
    }

    @Nested
    @DisplayName("updateAlbum - アルバム更新")
    class UpdateAlbum {
        // 共通モックデータの作成
        Long requestId = 1L;
        String mockAlbumTitle = "運動会";

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("AlbumCtrl-05: アルバムを更新できること")
        void updateAlbum_success() throws Exception {
            // リクエストボディの作成
            AlbumRequestDto request = new AlbumRequestDto();
            request.setTitle(mockAlbumTitle);

            // PUTリクエストの送信
            mockMvc.perform(put("/albums/{albumId}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 更新処理が呼ばれていることを検証
            verify(albumService).updateAlbum(requestId, mockAlbumTitle);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("AlbumCtrl-06: 空のタイトルで更新しようとした際拒否されること")
        void updateAlbum_emptyTitle() throws Exception {
            // リクエストボディの作成
            AlbumRequestDto request = new AlbumRequestDto();
            request.setTitle("");

            // PUTリクエストの送信
            mockMvc.perform(put("/albums/{albumId}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 400 Bad Requestであることを確認
                .andExpect(status().isBadRequest());

            // 更新処理が呼ばれていないことを検証
            verify(albumService, never()).updateAlbum(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-07: 一般ユーザーでアルバム更新を試みた場合アクセスが拒否されること")
        void updateAlbum_forbidden() throws Exception {
            // リクエストボディの作成
            AlbumRequestDto request = new AlbumRequestDto();
            request.setTitle(mockAlbumTitle);

            // PUTリクエストの送信
            mockMvc.perform(put("/albums/{albumId}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 更新処理が呼ばれていないことを検証
            verify(albumService, never()).updateAlbum(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteAlbum - アルバム削除")
    class DeleteAlbum {
        // 共通モックデータの作成
        Long requestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("AlbumCtrl-08: アルバムを削除できること")
        void deleteAlbum_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/albums/{albumId}", requestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(albumService).deleteAlbum(requestId);
        }

        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-09: 一般ユーザーでアルバム削除を試みた場合アクセスが拒否されること")
        void deleteAlbum_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/albums/{albumId}", requestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // 削除処理が呼ばれていないことを検証
            verify(albumService, never()).deleteAlbum(any());
        }
    }

    @Nested
    @DisplayName("addAlbumMedia - アルバムにメディアを追加")
    class AddAlbumMedia {
        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-10: アルバムにメディアを追加できること")
        void addAlbumMedia_success() throws Exception {

            // モックデータの作成
            Long requestId = 1L;
            List<Long> mockMediaIds = List.of(1L, 2L);
            AlbumMediaAddRequestDto request = new AlbumMediaAddRequestDto();
            request.setMediaIds(mockMediaIds);

            // POSTリクエストの送信
            mockMvc.perform(post("/albums/{id}/media", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 追加処理が呼ばれていることを検証
            verify(albumService).addAlbumMedia(requestId, mockMediaIds);
        }
    }

    @Nested
    @DisplayName("deleteAlbumMedia - アルバムからメディアを削除")
    class DeleteAlbumMedia {
        @Test
        @WithMockUser
        @DisplayName("AlbumCtrl-11: アルバムからメディアを削除できること")
        void deleteAlbumMedia_success() throws Exception {
            // モックデータの作成
            Long requestId = 1L;
            String requestMediaIds = "1,2";
            List<Long> mockMediaIds = List.of(1L, 2L);

            // DELETEリクエストの送信
            mockMvc.perform(delete("/albums/{id}/media", requestId)
                    .param("mediaIds", requestMediaIds))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 削除処理が呼ばれていることを検証
            verify(albumService).deleteAlbumMedia(requestId, mockMediaIds);
        }
    }
}

