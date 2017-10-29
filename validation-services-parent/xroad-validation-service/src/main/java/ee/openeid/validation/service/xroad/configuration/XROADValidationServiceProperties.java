/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.xroad.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zeroturnaround.zip.ZipUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean isDefaultConfiguration = false;
    private String configurationDirectoryPath;

    @PostConstruct
    public void setDefaultPath() {
        if (configurationDirectoryPath == null) {
            configurationDirectoryPath = unpackPackagedConfiguration();
            isDefaultConfiguration = true;
            LOGGER.info("Setting default XROAD configuration path: {}", configurationDirectoryPath);
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (isDefaultConfiguration) {
            LOGGER.info("Starting to remove XROAD configuration directory in path: {}", configurationDirectoryPath);
            int substringEnd = configurationDirectoryPath.lastIndexOf(DEFAULT_CONFIGURATION_DIRECTORY);
            String tempDirectoryPath = configurationDirectoryPath.substring(0, substringEnd);

            FileUtils.deleteDirectory(new File(tempDirectoryPath));
        }
    }

    private String unpackPackagedConfiguration() {
        Path tempXroadConfiguration = null;
        try (InputStream configurationZipStream = getClass().getResourceAsStream(JAR_PACKAGED_CONFIGURATION)) {
            tempXroadConfiguration = Files.createTempDirectory("siva-xroad-globalconf-");
            Path zipFilename = Paths.get(tempXroadConfiguration + File.separator + JAR_PACKAGED_CONFIGURATION);
            Files.copy(configurationZipStream, zipFilename);

            ZipUtil.unpack(new File(zipFilename.toAbsolutePath().toString()), tempXroadConfiguration.toFile());

            LOGGER.info("XROAD Configuration directory: {}", zipFilename);
            return tempXroadConfiguration.toAbsolutePath().toString() + DEFAULT_CONFIGURATION_DIRECTORY;
        } catch (IOException e) {
            LOGGER.error("XROAD default configuration unpacking failed with error: {}", e.getMessage(), e);
        } finally {
            if (tempXroadConfiguration != null) {
                tempXroadConfiguration.toFile().deleteOnExit();
            }
        }

        return StringUtils.EMPTY;
    }
}
