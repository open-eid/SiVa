package ee.openeid.siva.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SivaDemoApplication {
    public static void main(String... args) {
        SpringApplication.run(SivaDemoApplication.class, args);
    }
}
