package ee.openeid.siva.sample.ci.info;

import lombok.Data;

@Data
public class BuildInfo {
    GitHubInfo github;
    TravisCiInfo travisCi;
}
