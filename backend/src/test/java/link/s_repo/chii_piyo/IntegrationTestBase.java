package link.s_repo.chii_piyo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres;

    // JVM起動時にDB起動し、全テストでコンテナを共有
    static {
        postgres = new PostgreSQLContainer<>("postgres:18-alpine");
        postgres.start();
    }

    @Autowired
    protected MockMvc mockMvc;
}
