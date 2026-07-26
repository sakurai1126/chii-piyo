package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.controller.converter.MediaConverter;
import link.s_repo.chii_piyo.controller.converter.MediaListConverter;
import link.s_repo.chii_piyo.controller.converter.MediaNavigationConverter;
import link.s_repo.chii_piyo.controller.converter.MediaUploadConverter;
import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.model.gen.Favorites;
import link.s_repo.chii_piyo.model.gen.Media;
import link.s_repo.chii_piyo.model.gen.MediaListResponseDto;
import link.s_repo.chii_piyo.model.gen.MediaResponseDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // お気に入りデータのモックデータ作成ヘルパー
    private Favorites createMockFavorite(Long mockMediaId, Long mockUserId) {
        Favorites favorite = new Favorites();
        favorite.setMediaId(mockMediaId);
        favorite.setUserId(mockUserId);
        return favorite;
    }


    @Nested
    @DisplayName("getMediaList - メディア一覧取得")
    class GetMediaList {

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

        // 各種モックセットヘルパー
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
                .thenReturn(URI.create("https://example.com/thumbnail.jpg"));

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
}
