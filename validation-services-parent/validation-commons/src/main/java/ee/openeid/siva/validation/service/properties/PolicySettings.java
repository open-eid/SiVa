package ee.openeid.siva.validation.service.properties;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class PolicySettings {

    private static final Logger log = LoggerFactory.getLogger(PolicySettings.class);

    private String policy;

    public InputStream getPolicyDataStream() {
        if (Files.exists(Paths.get(policy))) {
            try {
                return new FileInputStream(policy);
            } catch (FileNotFoundException ignore) {
                log.warn(ignore.getMessage());
            }
        }
        return getClass().getResourceAsStream(policy);
    }

    public void setPolicy(String policy) {
        log.debug("setting validation policy to {}", policy);
        this.policy = policy;
        validateAgainstSchema();
    }

    private void validateAgainstSchema() {
        PolicySchemaValidator.validate(getPolicyDataStream());
    }
}
