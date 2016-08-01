package ee.openeid.siva.sample.ci.info;

import rx.Observable;

import java.io.IOException;

/**
 * Load continuous integration server build status and version control commit info
 * from given source location (i.e file system, database, cache)
 */
@FunctionalInterface
public interface BuildInfoFileLoader {
    Observable<BuildInfo> loadBuildInfo() throws IOException;
}
