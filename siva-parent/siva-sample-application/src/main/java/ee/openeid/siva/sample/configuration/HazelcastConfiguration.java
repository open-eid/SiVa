package ee.openeid.siva.sample.configuration;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {
    @Bean
    public Config config() {
        return new Config();
    }
}
