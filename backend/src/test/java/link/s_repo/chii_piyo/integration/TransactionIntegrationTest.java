package link.s_repo.chii_piyo.integration;

import link.s_repo.chii_piyo.IntegrationTestBase;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 非staticで@BeforeAllを使用
class TransactionIntegrationTest extends IntegrationTestBase {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private TrashRepository trashRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private MediaCommentRepository mediaCommentRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FirstRecordRepository firstRecordRepository;
    @Autowired
    private WordRecordRepository wordRecordRepository;

    @MockitoSpyBean
    private CareRecordRepository careRecordRepository;

    @MockitoBean
    private S3StorageManager s3StorageManager;

    private String cognitoUserId;
    private Users testUser;

    private RequestPostProcessor authJwt() {
        return jwt()
            .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
            .jwt(jwt -> jwt.subject(cognitoUserId));
    }

    private Media createMedia() {
        Media media = new Media();
        media.setUploadedBy(testUser.getId());
        media.setMediaType("PHOTO");
        media.setOriginalFilename("image.png");
        media.setContentType("image/jpeg");
        media.setFileSize(100L);
        media.setS3Key("media/" + UUID.randomUUID() + "_image.png");
        media.setUploadStatus("COMPLETED");
        mediaRepository.save(media);
        return media;
    }

    @BeforeAll
    void setUpAll() {
        Users user = new Users();
        user.setCognitoUserId("cognito-sub-transaction-test");
        user.setEmail("transaction-test@example.com");
        user.setDisplayName("テストユーザー");
        user.setRole("ADMIN");
        userRepository.save(user);

        this.testUser = user;
        this.cognitoUserId = user.getCognitoUserId();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 検証のためテスト側のトランザクションを無効化
    @DisplayName("IT-15: 削除処理中に例外が発生した場合ロールバックされ全データが残ること")
    void permanentlyDelete() throws Exception {
        // メディアデータを作成
        Media media = createMedia();

        // ゴミ箱データを作成
        TrashItems trashItem = new TrashItems();
        trashItem.setMediaId(media.getId());
        trashItem.setExpiresAt(OffsetDateTime.now(ZoneId.of("Asia/Tokyo")).plusDays(30));
        trashRepository.save(trashItem);

        // コメントを作成
        MediaComments comment = new MediaComments();
        comment.setMediaId(media.getId());
        comment.setUserId(testUser.getId());
        comment.setContent("ロールバック検証コメント");
        mediaCommentRepository.save(comment);

        // お気に入りを作成
        Favorites favorite = new Favorites();
        favorite.setMediaId(media.getId());
        favorite.setUserId(testUser.getId());
        favoriteRepository.save(favorite);

        // タグを作成
        MediaTags mediaTag = new MediaTags();
        mediaTag.setMediaId(media.getId());
        mediaTag.setTagId(1L);
        tagRepository.saveMediaTags(List.of(mediaTag));

        // はじめて記録を作成
        FirstRecords firstRecord = new FirstRecords();
        firstRecord.setTitle("はじめてのハイハイ");
        firstRecord.setRecordedDate(LocalDate.now());
        firstRecordRepository.save(firstRecord);

        FirstRecordMedia firstRecordMedia = new FirstRecordMedia();
        firstRecordMedia.setFirstRecordId(firstRecord.getId());
        firstRecordMedia.setMediaId(media.getId());
        firstRecordRepository.saveMedia(List.of(firstRecordMedia));

        // ことば記録を作成
        WordRecords wordRecord = new WordRecords();
        wordRecord.setTitle("ママ");
        wordRecord.setRecordedDate(LocalDate.now());
        wordRecordRepository.save(wordRecord);

        WordRecordMedia wordRecordMedia = new WordRecordMedia();
        wordRecordMedia.setWordRecordId(wordRecord.getId());
        wordRecordMedia.setMediaId(media.getId());
        wordRecordRepository.saveMedia(List.of(wordRecordMedia));
        try {
            // 例外発生を設定
            doThrow(new RuntimeException("S3から削除中に異常発生"))
                .when(s3StorageManager).deleteObjects(any());

            // 削除リクエストを実行
            mockMvc.perform(delete("/trash/{id}", trashItem.getId())
                    .with(authJwt()))
                .andExpect(status().isInternalServerError());

            // データが全て残存していることの検証
            assertThat(trashRepository.findById(trashItem.getId())).isPresent();
            assertThat(mediaRepository.findUnscopedById(media.getId())).isPresent();
            assertThat(mediaCommentRepository.findById(comment.getId())).isPresent();
            assertThat(favoriteRepository.findByMediaId(media.getId())).isNotEmpty();
            assertThat(tagRepository.findMediaTagsByMediaId(media.getId())).isNotEmpty();
            assertThat(firstRecordRepository.findMediaByRecordIds(List.of(firstRecord.getId()))).isNotEmpty();
            assertThat(wordRecordRepository.findMediaByRecordIds(List.of(wordRecord.getId()))).isNotEmpty();
        } finally {
            // 作成したメディアおよび関連データをクリーンアップ
            trashRepository.delete(trashItem.getId());
            mediaCommentRepository.delete(comment.getId());
            favoriteRepository.deleteByMediaId(media.getId());
            tagRepository.deleteMediaTagsByMediaId(media.getId());
            firstRecordRepository.deleteMediaByRecordId(firstRecord.getId());
            firstRecordRepository.deleteById(firstRecord.getId());
            wordRecordRepository.deleteMediaByRecordId(wordRecord.getId());
            wordRecordRepository.deleteById(wordRecord.getId());
            mediaRepository.deleteById(media.getId());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("IT-16: 記録本体の保存後、詳細保存で例外が発生した場合ロールバックされ記録本体も残らないこと")
    void createCareRecordError() throws Exception {
        // リクエストデータを作成
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Tokyo"));
        CareRecordRequestDto request = new CareRecordRequestDto()
            .recordType(CareRecordRequestDto.RecordTypeEnum.MEAL)
            .recordedAt(now)
            .mealDetail(new MealDetailDto().note("ごはん"));

        // 例外発生を設定
        doThrow(new RuntimeException("詳細記録保存中に異常発生"))
            .when(careRecordRepository).saveMeal(any());

        // リクエスト送信し例外発生を検証
        mockMvc.perform(post("/care-records")
                .with(authJwt())
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());

        // 親レコードが保存されていないことを検証
        assertThat(careRecordRepository.findRecordsByDate(now.toLocalDate(), now.toLocalDate())).isEmpty();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("IT-17: 一部に存在しないメディアIDを含めてアルバム追加リクエストをした場合ロールバックされ部分的な追加が残らないこと")
    void addAlbumMediaError() throws Exception {
        // アルバムデータを作成
        Albums album = new Albums();
        album.setTitle("旅行");
        albumRepository.save(album);

        // メディアデータを作成
        Media media = createMedia();

        try {
            // 存在しないIDを混合したリクエストを作成
            AlbumMediaAddRequestDto request = new AlbumMediaAddRequestDto()
                .mediaIds(List.of(media.getId(), 99L));

            // 追加リクエストの送信
            mockMvc.perform(post("/albums/{id}/media", album.getId())
                    .with(authJwt())
                    .contentType("application/json;charset=UTF-8")
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

            // メディアを取得しアルバムに追加されていないことを検証
            Media reloadedMedia = mediaRepository.findUnscopedById(media.getId()).orElse(null);
            assertThat(reloadedMedia).isNotNull();
            assertThat(reloadedMedia.getAlbumId()).isNull();
        } finally {
            // 作成したメディアおよびアルバムをクリーンアップ
            mediaRepository.deleteById(media.getId());
            albumRepository.deleteById(album.getId());
        }
    }
}
