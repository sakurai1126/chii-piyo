package link.s_repo.chii_piyo.integration;

import link.s_repo.chii_piyo.IntegrationTestBase;
import link.s_repo.chii_piyo.component.S3StorageManager;
import link.s_repo.chii_piyo.model.gen.*;
import link.s_repo.chii_piyo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CoreApiWorkflowIntegrationTest extends IntegrationTestBase {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CareRecordRepository careRecordRepository;
    @Autowired
    private SharingGroupRepository sharingGroupRepository;
    @Autowired
    private MediaCommentRepository mediaCommentRepository;
    @Autowired
    private TrashRepository trashRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FirstRecordRepository firstRecordRepository;
    @Autowired
    private WordRecordRepository wordRecordRepository;

    @MockitoBean
    private S3StorageManager s3StorageManager;

    // テスト用のデータを定義
    private static final String cognitoUserId = "cognito-sub-test-123";
    private static final String originalFilename = "image.png";
    private static final String contentType = "image/png";
    private static final Long fileSize = 100L;
    private static final String uploadStatus = "COMPLETED";
    private static final URI mockPresignedUrl = URI.create("https://s3.amazonaws.com/test-bucket/media/image.png");
    private Users testUser;

    // テスト用のユーザーを登録
    @BeforeEach
    void setUp() {
        Users user = new Users();
        user.setCognitoUserId(cognitoUserId);
        user.setEmail("test@example.com");
        user.setDisplayName("テストユーザー");
        user.setRole("ADMIN");
        userRepository.save(user);
        this.testUser = user;
    }

    // テスト用メディア作成ヘルパー
    private Media createMedia() {
        Media media = new Media();
        media.setUploadedBy(testUser.getId());
        media.setMediaType("PHOTO");
        media.setOriginalFilename(originalFilename);
        media.setContentType(contentType);
        media.setFileSize(fileSize);
        media.setS3Key("media/" + originalFilename);
        media.setUploadStatus(uploadStatus);
        mediaRepository.save(media);
        return media;
    }

    // 認証情報作成ヘルパー
    private RequestPostProcessor authJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")).jwt(jwt -> jwt.subject(cognitoUserId));
    }

    @Test
    @DisplayName("IT-09: メディアのアップロードができること")
    void uploadMedia() throws Exception {
        // S3依存のスタブ化
        when(s3StorageManager.generateUploadPresignedUrl(any(), any())).thenReturn(mockPresignedUrl);

        // リクエストデータの作成
        MediaUploadRequestDto request = new MediaUploadRequestDto()
            .mediaType(MediaUploadRequestDto.MediaTypeEnum.PHOTO)
            .originalFilename(originalFilename)
            .contentType(contentType)
            .fileSize(fileSize);

        // リクエスト送信し結果を取得検証
        MvcResult result = mockMvc.perform(post("/media")
                .with(authJwt())
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.mediaId").exists())
            .andExpect(jsonPath("$.presignedUrl").value(mockPresignedUrl.toString()))
            .andReturn();

        // レスポンスボディを取得
        String contentAsString = result.getResponse().getContentAsString();

        // JSONにシリアライズしつつmediaIdを取得
        Long mediaId = objectMapper.readTree(contentAsString).get("mediaId").asLong();

        // 取得したIDから保存されたメディアを取得し検証
        Optional<Media> savedMediaOptional = mediaRepository.findUnscopedById(mediaId);
        assertThat(savedMediaOptional).isPresent();
        Media savedMedia = savedMediaOptional.get();
        assertThat(savedMedia.getUploadedBy()).isEqualTo(testUser.getId());
        assertThat(savedMedia.getOriginalFilename()).isEqualTo(originalFilename);
    }

    @Test
    @DisplayName("IT-10: メディアの一覧取得ができること")
    void getMedia() throws Exception {
        // DBに事前のテスト用メディアデータを準備
        createMedia();

        // S3依存のスタブ化
        when(s3StorageManager.generateDownloadPresignedUrl(any(), any())).thenReturn(mockPresignedUrl);

        // リクエスト送信し結果が登録メディアをあることを検証
        mockMvc.perform(get("/media")
                .with(authJwt())
                .param("offset", "0")
                .param("limit", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.items[0].originalFilename").value(originalFilename))
            .andExpect(jsonPath("$.items[0].uploadStatus").value(uploadStatus));
    }

    @Test
    @DisplayName("IT-11: 育児記録の登録ができること")
    void createCareRecord() throws Exception {
        // リクエストデータの作成
        CareRecordRequestDto request = new CareRecordRequestDto()
            .recordType(CareRecordRequestDto.RecordTypeEnum.MEAL)
            .recordedAt(OffsetDateTime.now(ZoneOffset.UTC))
            .mealDetail(new MealDetailDto().note("ごはん"));

        // POSTリクエスト送信
        mockMvc.perform(post("/care-records")
                .with(authJwt())
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // 現在日時～現在日時の記録を取得し指定日時の育児記録が登録されたことを検証
        LocalDate todayJst = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        List<CareRecords> records = careRecordRepository.findRecordsByDate(todayJst, todayJst);
        assertThat(records).isNotEmpty();

        // 食事かつ登録ユーザーのものであることを確認
        assertThat(records.getFirst().getRecordType()).isEqualTo("MEAL");
        assertThat(records.getFirst().getRecordedBy()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("IT-12: 共有範囲グループと所属メンバーの登録ができること")
    void createSharingGroup() throws Exception {
        // リクエストDTOの作成
        String newGroupName = "友人";
        SharingGroupRequestDto request = new SharingGroupRequestDto()
            .name(newGroupName)
            .userIds(List.of(testUser.getId()));

        // POSTリクエスト送信
        mockMvc.perform(post("/sharing-groups")
                .with(authJwt())
                .contentType("application/json;charset=UTF-8")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // グループおよびメンバーが登録されたことを検証
        List<SharingGroups> groups = sharingGroupRepository.findAllOrderById();
        assertThat(groups).isNotEmpty();
        SharingGroups savedGroup = groups.stream()
            .filter(group -> newGroupName.equals(group.getName()))
            .findFirst()
            .orElse(null);
        assertThat(savedGroup).isNotNull();

        // 登録したメンバーがテストユーザーと一致することを確認
        List<SharingGroupMembers> members = sharingGroupRepository.findMembersByGroupIds(List.of(savedGroup.getId()));
        assertThat(members).hasSize(1);
        assertThat(members.getFirst().getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("IT-13: 他ユーザーのコメントを削除しようとした際に弾かれること")
    void deleteCommentForbidden() throws Exception {
        // 別ユーザーを作成
        Users otherUser = new Users();
        otherUser.setCognitoUserId("cognito-other-user-999");
        otherUser.setEmail("other@example.com");
        otherUser.setDisplayName("他ユーザー");
        otherUser.setRole("VIEWER");
        userRepository.save(otherUser);

        // メディアを作成
        Media media = createMedia();

        // 他ユーザーのコメントを作成
        String otherUserComment = "他ユーザーの書き込んだコメント";
        MediaComments comment = new MediaComments();
        comment.setMediaId(media.getId());
        comment.setUserId(otherUser.getId());
        comment.setContent(otherUserComment);
        mediaCommentRepository.save(comment);

        // テストユーザーのアカウントで他ユーザーのコメント削除を試みる
        mockMvc.perform(delete("/media/comments/{id}", comment.getId())
                .with(authJwt()))
            .andExpect(status().isForbidden());

        // コメントが削除されずに残っていることを検証
        MediaComments savedComment = mediaCommentRepository.findById(comment.getId()).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo(otherUserComment);
    }


    @Test
    @DisplayName("IT-14: ゴミ箱からの完全削除で、関連データ・メディア・ゴミ箱レコードが全削除されること")
    void deleteTrashItem_success() throws Exception {
        // メディアを作成
        Media media = createMedia();

        // コメントデータを作成
        MediaComments comment = new MediaComments();
        comment.setMediaId(media.getId());
        comment.setUserId(testUser.getId());
        comment.setContent("削除対象コメント");
        mediaCommentRepository.save(comment);

        // お気に入りの作成
        Favorites favorite = new Favorites();
        favorite.setMediaId(media.getId());
        favorite.setUserId(testUser.getId());
        favoriteRepository.save(favorite);

        // タグの作成
        MediaTags mediaTag = new MediaTags();
        mediaTag.setMediaId(media.getId());
        mediaTag.setTagId(1L);
        tagRepository.saveMediaTags(List.of(mediaTag));

        // はじめて記録の作成
        FirstRecords firstRecord = new FirstRecords();
        firstRecord.setTitle("つかまり立ちできた");
        firstRecord.setRecordedDate(LocalDate.now());
        firstRecordRepository.save(firstRecord);

        FirstRecordMedia firstRecordMedia = new FirstRecordMedia();
        firstRecordMedia.setFirstRecordId(firstRecord.getId());
        firstRecordMedia.setMediaId(media.getId());
        firstRecordRepository.saveMedia(List.of(firstRecordMedia));

        // ことば記録の作成
        WordRecords wordRecord = new WordRecords();
        wordRecord.setTitle("ママ");
        wordRecord.setRecordedDate(LocalDate.now());
        wordRecordRepository.save(wordRecord);

        WordRecordMedia wordRecordMedia = new WordRecordMedia();
        wordRecordMedia.setWordRecordId(wordRecord.getId());
        wordRecordMedia.setMediaId(media.getId());
        wordRecordRepository.saveMedia(List.of(wordRecordMedia));

        // ゴミ箱データの作成
        TrashItems trashItem = new TrashItems();
        trashItem.setMediaId(media.getId());
        trashItem.setExpiresAt(OffsetDateTime.now(ZoneId.of("Asia/Tokyo")).plusDays(30));
        trashRepository.save(trashItem);

        // 削除リクエストの送信
        mockMvc.perform(delete("/trash/{id}", trashItem.getId())
                .with(authJwt()))
            .andExpect(status().isNoContent());

        // 関連データが全て削除されていることの検証
        assertThat(trashRepository.findById(trashItem.getId())).isEmpty();
        assertThat(mediaRepository.findUnscopedById(media.getId())).isEmpty();
        assertThat(mediaCommentRepository.findById(comment.getId())).isEmpty();
        assertThat(favoriteRepository.findByMediaId(media.getId())).isEmpty();
        assertThat(tagRepository.findMediaTagsByMediaId(media.getId())).isEmpty();
        assertThat(firstRecordRepository.findMediaByRecordIds(List.of(firstRecord.getId()))).isEmpty();
        assertThat(wordRecordRepository.findMediaByRecordIds(List.of(wordRecord.getId()))).isEmpty();
    }
}
