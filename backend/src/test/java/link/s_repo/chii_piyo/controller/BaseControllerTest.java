package link.s_repo.chii_piyo.controller;

import link.s_repo.chii_piyo.component.UserSyncComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * コントローラーテストの共通設定を規定する抽象クラス
 */
@ActiveProfiles("test") // application-test.yamlの呼び出し設定
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    // コントローラー共通で使用するモック
    @MockitoBean
    protected UserSyncComponent userSyncComponent;
}
