package ee.openeid.siva.monitoring.util;

import com.jcabi.manifests.Manifests;
import com.jcabi.manifests.ServletMfs;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;

@Slf4j
public class ManifestReader {

    public ManifestReader(ServletContext servletContext) {
        try {
            Manifests.DEFAULT.append(new ServletMfs(servletContext));
        } catch (Exception e) {
            log.error("Failed to register manifests: " + e.getMessage(), e);
        }
    }

    public String readFromManifest(String parameterName) {
        try {
            return Manifests.read(parameterName);
        } catch (Exception e) {
            log.warn("Failed to fetch parameter '" + parameterName + "' from manifest file! Either you are not running the application as a jar/war package or there is a problem with the build configuration. " + e.getMessage());
            return null;
        }
    }

}
