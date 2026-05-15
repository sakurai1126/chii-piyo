package link.s_repo.chii_piyo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ChiiPiyoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChiiPiyoApplication.class, args);
    }
}
