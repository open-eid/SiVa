package ee.openeid.siva.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SivaSampleApplication {

    public static void main(String... args) {
        SpringApplication.run(SivaSampleApplication.class, args);
    }
}
