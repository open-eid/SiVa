package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.SystemProperties;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.asic.AsicUtils;
import ee.ria.xroad.common.conf.globalconf.GlobalConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class XRoadValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XRoadValidationService.class);

    @Override
    public QualifiedValidationResult validateDocument(ValidationDocument wsDocument) {
        try {
            final InputStream inputStream = new ByteArrayInputStream(wsDocument.getBytes());
            final AsicContainer container = AsicContainer.read(inputStream);

            final AsicContainerVerifier verifier = new AsicContainerVerifier(container);
            verifier.verify();

            onVerificationSucceeded(verifier);
        } catch (Exception e) {
//            onVerificationFailed(e);
        }

        return null;
    }

    @PostConstruct
    private void loadConf(String confPath) {
        System.setProperty(SystemProperties.CONFIGURATION_PATH, confPath);

        LOGGER.info("Loading configuration from " + confPath + "...");
        try {
            GlobalConf.reload();
//            verifyConfPathCorrectness();
        } catch (CodedException e) {
            System.err.println("Unable to load configuration: "
                    + e);
        }
    }

    private void onVerificationSucceeded(AsicContainerVerifier verifier) throws IOException {
        LOGGER.info(AsicUtils.buildSuccessOutput(verifier));
        LOGGER.info("\nWould you like to extract the signed files? (y/n) ");

        AsicContainer asic = verifier.getAsic();
//        writeToFile(AsicContainerEntries.ENTRY_MESSAGE, asic.getMessage());
        LOGGER.info("Files successfully extracted.");
    }
}
