package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.xroad.configuration.XROADValidationServiceProperties;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.SystemProperties;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.asic.AsicUtils;
import ee.ria.xroad.common.conf.globalconf.GlobalConf;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class XROADValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XROADValidationService.class);
    private XROADValidationServiceProperties properties;

    @Override
    public QualifiedReport validateDocument(ValidationDocument wsDocument) {
        final InputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(wsDocument.getDataBase64Encoded()));

        try {
            final AsicContainer container = AsicContainer.read(inputStream);
            final AsicContainerVerifier verifier = new AsicContainerVerifier(container);

            verifier.verify();
            onVerificationSucceeded(verifier);

            return new XROADQualifiedReportBuilder(verifier, wsDocument.getName(), new Date())
                    .build();
        } catch (Exception e) {
            LOGGER.warn("There was an error validating the document", e);
        }

        return null;
    }

    @PostConstruct
    void loadXroadConfigurationDirectory() {
        String configurationDirectoryPath = properties.getConfigurationDirectoryPath();
        System.setProperty(SystemProperties.CONFIGURATION_PATH, configurationDirectoryPath);

        LOGGER.info("Loading configuration from path: {}", configurationDirectoryPath);
        try {
            GlobalConf.reload();
//            verifyConfPathCorrectness();
        } catch (CodedException e) {
            LOGGER.error("Unable to load configuration: ", e);
        }
    }

    private void onVerificationSucceeded(AsicContainerVerifier verifier) throws IOException {
        LOGGER.info(AsicUtils.buildSuccessOutput(verifier));

        verifier.getAsic();
        verifier.getSignature();

        LOGGER.info("");
    }

    @Autowired
    public void setProperties(XROADValidationServiceProperties properties) {
        this.properties = properties;
    }
}
