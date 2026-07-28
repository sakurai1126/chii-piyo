package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.MediaListConverter;
import link.s_repo.chii_piyo.controller.converter.MediaNavigationConverter;
import link.s_repo.chii_piyo.controller.converter.MediaUploadConverter;
import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.exception.ResourceNotFoundException;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.service.FavoriteService;
import link.s_repo.chii_piyo.service.MediaCommentService;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TagService;
import link.s_repo.chii_piyo.service.TrashService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MediaController.class)
public class MediaControllerTest extends BaseControllerTest {
    @MockitoBean
    private MediaService mediaService;
    @MockitoBean
    private MediaConverter mediaConverter;
    @MockitoBean
    private MediaListConverter mediaListConverter;
    @MockitoBean
    private MediaCommentService mediaCommentService;
    @MockitoBean
    private S3StorageManager s3StorageManager;
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private TagConverter tagConverter;
    @MockitoBean
    private MediaNavigationConverter mediaNavigationConverter;
    @MockitoBean
    private MediaUploadConverter mediaUploadConverter;
    @MockitoBean
    private FavoriteService favoriteService;
    @MockitoBean
    private TrashService trashService;

    // メディアデータのモックデータ作成ヘルパー
    private Media createMockMedia(Long mockMediaId) {
        Media media = new Media();
        media.setId(mockMediaId);
        media.setThumbnailS3Key("thumbnails/sample.jpg");
        media.setOriginalFilename("sample.jpg");
        return media;
    }

    @Nested
    @DisplayName("getMediaList - メディア一覧取得")
    class GetMediaList {
        // お気に入りデータのモックデータ作成ヘルパー
        private Favorites createMockFavorite(Long mockMediaId, Long mockUserId) {
            Favorites favorite = new Favorites();
            favorite.setMediaId(mockMediaId);
            favorite.setUserId(mockUserId);
            return favorite;
        }

        // 単体レスポンスデータのモックデータ作成ヘルパー
        private MediaResponseDto createMockMediaResponseDto(
            Long mockMediaId, Long mockCommentCount) {
            return new MediaResponseDto()
                .id(mockMediaId)
                .isFavorite(true)
                .commentCount(mockCommentCount);
        }

        // 一覧レスポンスデータのモックデータ作成ヘルパー
        private MediaListResponseDto createMockMediaListResponseDto(
            List<MediaResponseDto> responseList, Long mockTotalCount, boolean hasNext) {
            return new MediaListResponseDto()
                .items(responseList)
                .totalCount(mockTotalCount)
                .hasNext(hasNext);
        }

        // 一覧用各種モックセットヘルパー
        private void setupDefaultMocks(
            Long mockCurrentUserId, Long mockTotalCount, Media media, Favorites favorite,
            Long mockMediaId, Long mockCommentCount,
            MediaResponseDto response, MediaListResponseDto responseList) {

            // ログインユーザーID取得のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // 総件数取得のモック化
            when(mediaService.countMedia(any())).thenReturn(mockTotalCount);

            // メディア取得処理のモック化
            when(mediaService.getMediaList(any())).thenReturn(List.of(media));

            // お気に入り取得処理のモック化
            when(favoriteService.getFavoriteList(any())).thenReturn(List.of(favorite));

            // メディアコメントのIDリスト取得処理のモック化
            when(mediaCommentService.getCommentCountsByMediaIds(any()))
                .thenReturn(Map.of(mockMediaId, mockCommentCount));

            // S3ダウンロード用URL生成処理のモック化
            when(s3StorageManager.generateDownloadPresignedUrl(any(), any()))
                .thenReturn(URI.create("https://example.com/image.jpg"));

            // 単体のレスポンス変換処理をモック化
            when(mediaConverter.toMediaResponseDto(
                eq(media), isNull(), isNull(), any(), anyBoolean(), eq(mockCommentCount),
                isNull(), isNull(), isNull(), isNull(), any()
            )).thenReturn(response);

            // 一覧のレスポンス変換処理をモック化
            when(mediaListConverter.toMediaListResponseDto(
                any(), eq(mockTotalCount), anyBoolean()
            )).thenReturn(responseList);
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-01: メディア一覧を取得できること")
        void getMediaList_success() throws Exception {
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            Long mockTotalCount = 10L;
            Long mockCommentCount = 5L;
            boolean hasNext = true;

            // メディアのモックデータ
            Media media = createMockMedia(mockMediaId);
            // お気に入りのモックデータ
            Favorites favorite = createMockFavorite(mockMediaId, mockCurrentUserId);
            // 単体レスポンスの作成
            MediaResponseDto response = createMockMediaResponseDto(mockMediaId, mockCommentCount);
            // 一覧レスポンスの作成
            MediaListResponseDto responseList = createMockMediaListResponseDto(List.of(response), mockTotalCount, hasNext);

            // モックデータの設定
            setupDefaultMocks(mockCurrentUserId, mockTotalCount, media, favorite, mockMediaId,
                mockCommentCount, response, responseList);

            // リクエスト実行および検証
            mockMvc.perform(get("/media")
                    .param("offset", "0")
                    .param("limit", "20"))
                // ステータスコード 200 OK の確認
                .andExpect(status().isOk());

            // メディア・お気に入り判定・コメント件数が整形されてConverterに渡ること
            verify(mediaConverter).toMediaResponseDto(
                eq(media), isNull(), isNull(), any(), eq(true), eq(mockCommentCount),
                isNull(), isNull(), isNull(), isNull(), any());

            // 総件数がConverterに渡ること
            verify(mediaListConverter).toMediaListResponseDto(any(), eq(mockTotalCount), anyBoolean());
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-02: 取得件数が総件数と一致した場合次のページがないこと")
        void getMediaList_hasNextFalse() throws Exception {
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            // メディア数1件に対し総件数1件に指定
            Long mockTotalCount = 1L;
            Long mockCommentCount = 5L;
            boolean hasNext = false;

            // メディアのモックデータ
            Media media = createMockMedia(mockMediaId);
            // お気に入りのモックデータ
            Favorites favorite = createMockFavorite(mockMediaId, mockCurrentUserId);
            // 単体レスポンスの作成
            MediaResponseDto response = createMockMediaResponseDto(mockMediaId, mockCommentCount);

            // 一覧レスポンスの作成
            MediaListResponseDto responseList = createMockMediaListResponseDto(
                List.of(response), mockTotalCount, hasNext);

            // モックデータの設定
            setupDefaultMocks(mockCurrentUserId, mockTotalCount, media, favorite, mockMediaId,
                mockCommentCount, response, responseList);

            // リクエスト実行および検証
            mockMvc.perform(get("/media")
                .param("offset", "0")
                .param("limit", "20"));

            // Controllerが算出したhasNextがfalseであることを検証
            verify(mediaListConverter).toMediaListResponseDto(any(), eq(mockTotalCount), eq(false));
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-03: 取得件数が総件数より少ない場合次のページがあること")
        void getMediaList_hasNextTrue() throws Exception {
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            // メディア数1件に対し総件数2件に指定
            Long mockTotalCount = 2L;
            Long mockCommentCount = 5L;
            boolean hasNext = true;

            // メディアのモックデータ
            Media media = createMockMedia(mockMediaId);
            // お気に入りのモックデータ
            Favorites favorite = createMockFavorite(mockMediaId, mockCurrentUserId);
            // 単体レスポンスの作成
            MediaResponseDto response = createMockMediaResponseDto(mockMediaId, mockCommentCount);
            // 一覧レスポンスの作成
            MediaListResponseDto responseList = createMockMediaListResponseDto(
                List.of(response), mockTotalCount, hasNext);

            // モックデータの設定
            setupDefaultMocks(mockCurrentUserId, mockTotalCount, media, favorite, mockMediaId,
                mockCommentCount, response, responseList);

            // リクエスト実行および検証
            mockMvc.perform(get("/media")
                .param("offset", "0")
                .param("limit", "1"));

            // Controllerが算出したhasNextがtrueであることを検証
            verify(mediaListConverter).toMediaListResponseDto(any(), eq(mockTotalCount), eq(true));
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-04: お気に入り済みのメディア一覧を取得できること")
        void getMediaList_isFavorite() throws Exception {
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            Long mockTotalCount = 10L;
            Long mockCommentCount = 5L;
            boolean hasNext = false;

            // メディアのモックデータ
            Media media = createMockMedia(mockMediaId);
            // お気に入りのモックデータ
            Favorites favorite = createMockFavorite(mockMediaId, mockCurrentUserId);
            // 単体レスポンスの作成
            MediaResponseDto response = createMockMediaResponseDto(mockMediaId, mockCommentCount);
            // 一覧レスポンスの作成
            MediaListResponseDto responseList = createMockMediaListResponseDto(
                List.of(response), mockTotalCount, hasNext);

            // モックデータの設定
            setupDefaultMocks(mockCurrentUserId, mockTotalCount, media, favorite, mockMediaId,
                mockCommentCount, response, responseList);

            // リクエスト実行および検証
            mockMvc.perform(get("/media")
                .param("offset", "0")
                .param("limit", "20"));

            // お気に入り判定がtrueで呼ばれていることの検証
            verify(mediaConverter).toMediaResponseDto(
                eq(media), isNull(), isNull(), any(), eq(true), eq(mockCommentCount),
                isNull(), isNull(), isNull(), isNull(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-05: サムネイルS3キーがnullのメディアを取得できること")
        void getMediaList_noThumbnail() throws Exception {
            // サムネイルS3キーがnullのメディアを含む	該当メディアのサムネイルURLがnullで返る
            // モックデータの準備
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            Long mockTotalCount = 10L;
            Long mockCommentCount = 5L;
            boolean hasNext = false;

            // メディアのモックデータ
            Media media = createMockMedia(mockMediaId);
            // サムネイルS3キーをnullで上書き
            media.setThumbnailS3Key(null);
            // お気に入りのモックデータ
            Favorites favorite = createMockFavorite(mockMediaId, mockCurrentUserId);
            // 単体レスポンスの作成
            MediaResponseDto response = createMockMediaResponseDto(mockMediaId, mockCommentCount);
            // 一覧レスポンスの作成
            MediaListResponseDto responseList = createMockMediaListResponseDto(
                List.of(response), mockTotalCount, hasNext);

            // モックデータの設定
            setupDefaultMocks(mockCurrentUserId, mockTotalCount, media, favorite, mockMediaId,
                mockCommentCount, response, responseList);

            // リクエスト実行および検証
            mockMvc.perform(get("/media")
                .param("offset", "0")
                .param("limit", "20"));

            // サムネイルS3キーがnullの場合はgenerateDownloadPresignedUrlが呼ばれていないことを確認
            verify(s3StorageManager, never()).generateDownloadPresignedUrl(any(), any());

            // ConverterにthumbnailPresignedUrlとしてnullが渡っていることを検証
            verify(mediaConverter).toMediaResponseDto(
                eq(media), isNull(), isNull(), isNull(), eq(true), eq(mockCommentCount),
                isNull(), isNull(), isNull(), isNull(), any());
        }
    }

    @Nested
    @DisplayName("getMedia - メディア単体取得")
    class GetMedia {
        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-06: メディアID指定で取得できること")
        void getMedia_success() throws Exception {
            // モックデータの準備
            Long mockMediaId = 1L;

            // メディアのモックデータ
            Media mockMedia = createMockMedia(mockMediaId);

            // 単体レスポンスの作成
            MediaResponseDto response = new MediaResponseDto().id(mockMediaId);

            // 画像取得処理のモック化
            when(mediaService.getMedia(eq(mockMediaId))).thenReturn(mockMedia);

            // 単体のレスポンス変換処理をモック化
            when(mediaConverter.toMediaResponseDto(
                eq(mockMedia), any(), any(), any(), anyBoolean(), isNull(), isNull(), isNull(), isNull(), isNull(), any()
            )).thenReturn(response);

            // リクエスト実行および検証
            mockMvc.perform(get("/media/{id}", mockMediaId))
                // ステータスコード 200 OK の確認
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockMediaId));

            // getMediaが指定のIDで呼ばれていることを検証
            verify(mediaService).getMedia(mockMediaId);

            // コンバーター処理がmockMediaで呼ばれていることを確認
            verify(mediaConverter).toMediaResponseDto(
                eq(mockMedia), any(), any(), any(), anyBoolean(), isNull(), isNull(), isNull(), isNull(), isNull(), any()
            );
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-07: 存在しないメディアIDでの取得リクエストに対し404を返すこと")
        void getMedia_notFound() throws Exception {
            Long nonExistentId = 10L;
            // mediaService.getMedia(nonExistentId) が呼ばれたときに ResourceNotFoundException をスローするように設定
            when(mediaService.getMedia(nonExistentId))
                .thenThrow(new ResourceNotFoundException("メディアが見つかりません mediaId=" + nonExistentId));

            // リクエスト実行および検証
            mockMvc.perform(get("/media/{id}", nonExistentId))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // 指定のIDで取得処理が呼ばれたことを確認
            verify(mediaService).getMedia(eq(nonExistentId));

            // 後続のコンバーター処理が実行されなかったことを確認
            verify(mediaConverter, never()).toMediaResponseDto(any(), any(), any(), any(), anyBoolean(), any(), any(), any(), any(), any(), any());

        }
    }

    @Nested
    @DisplayName("createMedia - メディア作成")
    class CreateMedia {
        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-08: アップロードリクエストで201とメディアID・アップロードURLが返ること")
        void createMedia_success() throws Exception {
            // モックデータの作成
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;
            Media mockMedia = createMockMedia(mockMediaId);
            URI mockPresignedUrl = URI.create("https://example.com/image.jpg");
            MediaService.CreateMediaResult mockResult = new MediaService.CreateMediaResult(mockMedia, mockPresignedUrl);

            // リクエストボディの作成
            MediaUploadRequestDto request = new MediaUploadRequestDto()
                .mediaType(MediaUploadRequestDto.MediaTypeEnum.PHOTO)
                .originalFilename("image.jpg")
                .contentType("image/jpeg")
                .fileSize(100L);

            // ログインユーザーID取得のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // サービス層処理のモック化
            when(mediaService.createMedia(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(mockResult);

            // レスポンスのモックデータ
            MediaUploadResponseDto response = new MediaUploadResponseDto()
                .mediaId(mockMediaId)
                .presignedUrl(mockPresignedUrl);

            // コンバーター処理のモック化
            when(mediaUploadConverter.toMediaUploadResponseDto(any(), any()))
                .thenReturn(response);

            // POSTリクエストの送信
            mockMvc.perform(post("/media")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 201 Createdであることを確認
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mediaId").value(mockMediaId))
                .andExpect(jsonPath("$.presignedUrl").value(mockPresignedUrl.toString()));

            // サービス層の処理が指定の値で呼ばれたか検証
            verify(mediaService).createMedia(
                eq(mockCurrentUserId), eq("PHOTO"), eq("image.jpg"), eq("image/jpeg"),
                eq(100L), isNull(), isNull(), isNull(), isNull(), isNull()
            );

            // コンバーター処理で呼ばれた引数が値と一致しているか検証
            verify(mediaUploadConverter).toMediaUploadResponseDto(eq(mockMediaId), eq(mockPresignedUrl));
        }
    }

    @Nested
    @DisplayName("updateMediaUploadStatus - アップロードステータスの更新")
    class UpdateMediaUploadStatus {
        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-09: アップロードステータス更新が成功すること")
        void updateMediaUploadStatus_success() throws Exception {
            // モックデータの作成
            Long mockCurrentUserId = 1L;
            Long mockMediaId = 2L;

            // リクエストボディの作成
            MediaUploadStatusRequestDto request = new MediaUploadStatusRequestDto()
                .uploadStatus(MediaUploadStatusRequestDto.UploadStatusEnum.COMPLETED);

            // ログインユーザーID取得のモック化
            when(currentUserProvider.getUserId()).thenReturn(mockCurrentUserId);

            // PATCHリクエストの送信
            mockMvc.perform(patch("/media/{id}/status", mockMediaId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // コンバーター処理で呼ばれた引数が値と一致しているか検証
            verify(mediaService).updateUploadStatus(eq(mockMediaId), eq(mockCurrentUserId),
                eq(MediaUploadStatusRequestDto.UploadStatusEnum.COMPLETED.getValue()));
        }
    }

    @Nested
    @DisplayName("updateMedia - メディアの更新")
    class UpdateMedia {
        Long requestId = 1L;
        MediaUpdateRequestDto request = new MediaUpdateRequestDto();

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("MediaCtrl-10: メディアが更新できること")
        void updateMedia_success() throws Exception {
            // PATCHリクエストの送信
            mockMvc.perform(patch("/media/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 渡したリクエストでサービス層への受け渡しが行われたか確認
            verify(mediaService).updateMedia(eq(requestId), eq(request));
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-11: 一般ユーザーでメディアの更新を試みた場合アクセスが拒否されること")
        void updateMedia_forbidden() throws Exception {
            // PATCHリクエストの送信
            mockMvc.perform(patch("/media/{id}", requestId)
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // サービス層への受け渡しが行われていないことを確認
            verify(mediaService, never()).updateMedia(any(), any());
        }
    }

    @Nested
    @DisplayName("updateMediaBatch - メディアメタデータの一括更新")
    class UpdateMediaBatch {
        MediaBatchUpdateRequestDto request = new MediaBatchUpdateRequestDto();

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("MediaCtrl-12: メディアメタデータの一括更新ができること")
        void updateMediaBatch_success() throws Exception {
            // PATCHリクエストの送信
            mockMvc.perform(patch("/media/batch")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 渡したリクエストでサービス層への受け渡しが行われたか確認
            verify(mediaService).updateMediaBatch(eq(request));
        }

        // MediaCtrl-13	updateMediaBatch	異常	一般ユーザーで一括更新	403が返る
        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-13: 一般ユーザーでメディアメタデータの更新を試みた場合アクセスが拒否されること")
        void updateMediaBatch_forbidden() throws Exception {
            // PATCHリクエストの送信
            mockMvc.perform(patch("/media/batch")
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // サービス層への受け渡しが行われていないことを確認
            verify(mediaService, never()).updateMediaBatch(any());
        }
    }

    @Nested
    @DisplayName("deleteMedia - メディアの削除(ゴミ箱へ移動)")
    class DeleteMedia {

        Long requestId = 1L;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("MediaCtrl-14: メディアのゴミ箱移動ができること")
        void deleteMedia_success() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/media/{id}", requestId))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // 指定リクエストでサービス層への受け渡しが行われたか確認
            verify(trashService).createTrashItem(eq(requestId));
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-15: 一般ユーザーでのメディアのゴミ箱移動が拒否されること")
        void deleteMedia_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/media/{id}", requestId))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // サービス層への受け渡しが行われていないことを確認
            verify(trashService, never()).createTrashItem(any());
        }
    }

    @Nested
    @DisplayName("deleteMultipleMedia - メディアの一括削除(ゴミ箱へ移動)")
    class DeleteMultipleMedia {
        String requestMediaIds = "1,2";

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("MediaCtrl-16: 複数メディアの一括ゴミ箱移動ができること")
        void deleteMultipleMedia_success() throws Exception {

            // モックデータを作成
            List<Long> expectedMediaIds = List.of(1L, 2L);
            List<Media> mockMediaList = List.of(new Media(), new Media());

            // メディア取得のモック化
            when(mediaService.getMediabyIds(any())).thenReturn(mockMediaList);

            // DELETEリクエストの送信
            mockMvc.perform(delete("/media")
                    .param("mediaIds", requestMediaIds))
                // ステータスコード 204 No Contentであることを確認
                .andExpect(status().isNoContent());

            // メディアの取得処理がリクエストのIDで呼ばれていることを確認
            verify(mediaService).getMediabyIds(eq(expectedMediaIds));

            // サービス層への受け渡しが行われたか確認
            verify(trashService).createTrashItems(eq(expectedMediaIds));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("MediaCtrl-17: 存在しないメディアIDに404が返ること")
        void deleteMultipleMedia_notFound() throws Exception {
            // モックデータを作成
            List<Media> mockMediaList = List.of();

            // メディアの取得処理をモック化
            when(mediaService.getMediabyIds(any())).thenReturn(mockMediaList);

            // DELETEリクエストの送信
            mockMvc.perform(delete("/media")
                    .param("mediaIds", requestMediaIds))
                // ステータスコード 404 Not Foundであることを確認
                .andExpect(status().isNotFound());

            // サービス層への受け渡しが行われていないことを確認
            verify(trashService, never()).createTrashItems(any());
        }

        @Test
        @WithMockUser
        @DisplayName("MediaCtrl-18: 一般ユーザーでの複数メディアの一括ゴミ箱移動が拒否されること")
        void deleteMultipleMedia_forbidden() throws Exception {
            // DELETEリクエストの送信
            mockMvc.perform(delete("/media")
                    .param("mediaIds", requestMediaIds))
                // ステータスコード 403 Forbiddenであることを確認
                .andExpect(status().isForbidden());

            // サービス層への受け渡しが行われていないことを確認
            verify(trashService, never()).createTrashItems(any());
        }
    }
}
