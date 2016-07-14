package ee.openeid.siva.sample.ci.info;

import lombok.Data;

@Data
public class BuildInfo {
    private GitHubInfo github;
    private TravisCiInfo travisCi;
}
