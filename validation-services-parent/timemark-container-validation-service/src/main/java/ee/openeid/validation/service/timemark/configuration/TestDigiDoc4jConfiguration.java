package ee.openeid.validation.service.timemark.configuration;


import org.digidoc4j.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestDigiDoc4jConfiguration {
    @Bean
    private org.digidoc4j.Configuration getConfiguration() {
        org.digidoc4j.Configuration configuration = new org.digidoc4j.Configuration(Configuration.Mode.TEST);
        return configuration;
    }
}

