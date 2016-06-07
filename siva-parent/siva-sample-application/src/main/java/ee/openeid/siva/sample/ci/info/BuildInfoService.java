package ee.openeid.siva.sample.ci.info;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ee.openeid.siva.sample.configuration.BuildInfoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BuildInfoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoService.class);
    private BuildInfoProperties properties;

    public BuildInfo loadBuildInfo() {
        try {
            final byte[] yamlFile = loadYamlFile();
            return mapToBuildInfo(yamlFile);
        } catch (IOException e) {
            LOGGER.warn("Failed to load build info file: {}", getBuildInfoFilePath(), e);
        }

        return new BuildInfo();
    }

    private static BuildInfo mapToBuildInfo(final byte[] yamlFile) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(yamlFile, BuildInfo.class);
    }

    private byte[] loadYamlFile() throws IOException {
        final Path yamlFilePath = getBuildInfoFilePath();
        LOGGER.info("Start loading in build info YAML file: {}", yamlFilePath);

        return Files.readAllBytes(yamlFilePath);
    }

    private Path getBuildInfoFilePath() {
        return Paths.get(Paths.get("").toAbsolutePath() + File.separator + properties.getInfoFile());
    }

    @Autowired
    public void setProperties(final BuildInfoProperties properties) {
        this.properties = properties;
    }
}