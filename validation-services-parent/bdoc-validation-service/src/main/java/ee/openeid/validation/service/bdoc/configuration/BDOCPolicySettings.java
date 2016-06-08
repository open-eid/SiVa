package ee.openeid.validation.service.bdoc.configuration;

import ee.openeid.siva.validation.service.properties.PolicySettings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.*;

@ConfigurationProperties(prefix = "siva.bdoc")
public class BDOCPolicySettings extends PolicySettings {

    private static final Logger log = LoggerFactory.getLogger(BDOCPolicySettings.class);

    public String getAbsolutePath() {
        log.debug("creating policy file from path {}", getPolicy());
        final InputStream inputStream = getPolicyDataStream();
        try {
            final File file = File.createTempFile("bdoc_constraint", "xml");
            file.deleteOnExit();
            final OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            log.error("Unable to create temporary file from bdoc policy resource", e);
            throw new BdocPolicyFileCreationException(e);
        }
    }
}
