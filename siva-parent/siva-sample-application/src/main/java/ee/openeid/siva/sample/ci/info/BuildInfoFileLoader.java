package ee.openeid.siva.sample.ci.info;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ee.openeid.siva.sample.configuration.BuildInfoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class BuildInfoFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoFileLoader.class);
    private static final byte[] EMPTY_CONTENT = new byte[0];
    private BuildInfoProperties properties;

    public BuildInfo loadBuildInfo() {
        try {
            final byte[] yamlFile = loadYamlFile();
            return mapToBuildInfo(yamlFile);
        } catch (final IOException e) {
            LOGGER.warn("Failed to load build info file: {}", getBuildInfoFilePath(), e);
        }

        return new BuildInfo();
    }

    private static BuildInfo mapToBuildInfo(final byte[] yamlFile) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(yamlFile, BuildInfo.class);
        } catch (JsonMappingException ex) {
            LOGGER.warn("Failed to parse JSON with with message: {}", ex.getMessage());
        }

        return new BuildInfo();
    }

    private byte[] loadYamlFile() throws IOException {
        final Path yamlFilePath = getBuildInfoFilePath();
        if (!Files.exists(yamlFilePath)) {
            LOGGER.warn("No such file exists: {}", yamlFilePath);
            return EMPTY_CONTENT;
        }

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