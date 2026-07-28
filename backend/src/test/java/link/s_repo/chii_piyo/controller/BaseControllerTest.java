package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.UserSyncComponent;
import link.s_repo.chii_piyo.config.TestSecurityConfig;
import link.s_repo.chii_piyo.security.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * コントローラーテストの共通設定を規定する抽象クラス
 */
// application-test.yamlの呼び出し設定
@ActiveProfiles("test")
// テスト用セキュリティ設定の取り込み
@Import(TestSecurityConfig.class)
// @PreAuthorize等のメソッドセキュリティを有効化
@EnableMethodSecurity(proxyTargetClass = true)
public abstract class BaseControllerTest {
    // 擬似的なHTTPリクエストを送信、検証する
    @Autowired
    protected MockMvc mockMvc;

    // 送信リクエスト時に、Javaオブジェクト（DTO）をJSON文字列に変換する
    @Autowired
    protected ObjectMapper objectMapper;

    // コントローラー共通で使用するモック
    @MockitoBean
    protected UserSyncComponent userSyncComponent;

    @MockitoBean
    protected CurrentUserProvider currentUserProvider;
}
