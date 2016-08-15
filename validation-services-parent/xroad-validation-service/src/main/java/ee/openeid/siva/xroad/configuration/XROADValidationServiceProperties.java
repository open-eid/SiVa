package ee.openeid.siva.xroad.configuration;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zeroturnaround.zip.ZipUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@ConfigurationProperties(prefix = "siva.xroad.validation.service")
public class XROADValidationServiceProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(XROADValidationServiceProperties.class);

    private static final String DEFAULT_CONFIGURATION_DIRECTORY = "/verificationconf";
    private static final String JAR_PACKAGED_CONFIGURATION = "/xroad-conf.zip";
    private String configurationDirectoryPath;

    @PostConstruct
    public void setDefaultPath() {
        if (configurationDirectoryPath == null) {
            configurationDirectoryPath = unpackPackagedConfiguration();
        }
    }

    private String unpackPackagedConfiguration() {
        Path tempXroadConfiguration = null;
        try (InputStream configurationZipStream = getClass().getResourceAsStream(JAR_PACKAGED_CONFIGURATION)) {
            tempXroadConfiguration = Files.createTempDirectory("siva-xroad");
            Path zipFilename = Paths.get(tempXroadConfiguration + File.separator + JAR_PACKAGED_CONFIGURATION);
            Files.copy(configurationZipStream, zipFilename);

            ZipUtil.unpack(new File(zipFilename.toAbsolutePath().toString()), tempXroadConfiguration.toFile());

            LOGGER.info("XROAD Configuraiton directory: {}", zipFilename);
            return tempXroadConfiguration.toAbsolutePath().toString() + File.separator + DEFAULT_CONFIGURATION_DIRECTORY;
        } catch (IOException e) {
            LOGGER.error("XROAD default configuration unpacking failed with error: {}", e.getMessage());
        } finally {
            if (tempXroadConfiguration != null) {
                tempXroadConfiguration.toFile().deleteOnExit();
            }
        }

        return StringUtils.EMPTY;
    }
}
