package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.controller.converter.TagConverter;
import link.s_repo.chii_piyo.model.gen.TagResponseDto;
import link.s_repo.chii_piyo.model.gen.Tags;
import link.s_repo.chii_piyo.service.MediaService;
import link.s_repo.chii_piyo.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
public class TagControllerTest extends BaseControllerTest {
    // TagControllerのコンストラクタにあるものをモック化する
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private TagConverter tagConverter;
    @MockitoBean
    private MediaService mediaService;

    @Test
    @WithMockUser
    @DisplayName("GET /tags - タグ一覧を取得できるかのテスト")
    void getTags_success() throws Exception {
        // テスト用のダミーデータを生成
        Tags tag = new Tags();
        tag.setId(1L);
        tag.setName("おでかけ");

        // サービスから返されるデータにダミーを設定
        when(tagService.getTags()).thenReturn(List.of(tag));
        when(tagService.getMediaCountByTagId()).thenReturn(Map.of(1L, 3L));

        // コンバーターで変換された後に返されるDTOを設定
        TagResponseDto expectedDto = new TagResponseDto();
        expectedDto.setId(1L);
        expectedDto.setName("おでかけ");
        expectedDto.setMediaCount(3L);

        when(tagConverter.toTagResponseDto(tag, 3L)).thenReturn(expectedDto);

        // mockMvc.performでHTTPリクエストを送信しステータスを検証
        mockMvc.perform(get("/tags"))
            // ステータスコード 200 OKであることを確認
            .andExpect(status().isOk())
            // 配列の1件目の各プロパティが期待通りか検証
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("おでかけ"))
            .andExpect(jsonPath("$[0].mediaCount").value(3L));
    }
}
