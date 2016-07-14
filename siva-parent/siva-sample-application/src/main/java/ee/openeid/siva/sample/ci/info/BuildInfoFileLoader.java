package ee.openeid.siva.sample.ci.info;

import java.io.IOException;

public interface BuildInfoFileLoader {
    BuildInfo loadBuildInfo() throws IOException;
}
