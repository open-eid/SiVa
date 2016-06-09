package ee.openeid.siva.sample.ci.info;

import ee.openeid.siva.sample.configuration.BuildInfoProperties;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildInfoFileLoaderTest {
    private BuildInfoFileLoader loader = new BuildInfoFileLoader();

    @Test
    public void givenValidInfoFileWillReturnCorrectBuildInfo() throws Exception {
        loader.setProperties(createBuildProperties("/test-info.yml"));
        BuildInfo buildInfo = loader.loadBuildInfo();

        assertThat(buildInfo.getTravisCi().getBuildNumber()).isEqualTo("#106");
        assertThat(buildInfo.getGithub().getAuthorName()).isEqualTo("Andres Voll");
    }

    @Test
    public void givenInvalidInfoFilePathWillReturnEmptyBuildInfo() throws Exception {
        BuildInfoProperties properties = new BuildInfoProperties();
        properties.setInfoFile("wrong.info-file.yml");

        loader.setProperties(properties);
        BuildInfo buildInfo = loader.loadBuildInfo();

        assertThat(buildInfo.getGithub()).isNull();
        assertThat(buildInfo.getTravisCi()).isNull();
    }

    @Test
    public void givenInfoFileWithMissingGithubInfoWillReturnBuildInfo() throws Exception {
        loader.setProperties(createBuildProperties("/info-missing-github.yml"));
        BuildInfo buildInfo = loader.loadBuildInfo();

        assertThat(buildInfo.getGithub()).isNull();
        assertThat(buildInfo.getTravisCi().getBuildNumber()).isEqualTo("#106");
    }

    private BuildInfoProperties createBuildProperties(String filePath) {
        final String infoFilePath = getClass().getResource(filePath).getPath();

        BuildInfoProperties properties = new BuildInfoProperties();
        properties.setInfoFile(infoFilePath);
        return properties;
    }
}