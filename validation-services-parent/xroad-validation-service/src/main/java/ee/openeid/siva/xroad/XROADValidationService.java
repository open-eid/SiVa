package ee.openeid.siva.xroad;

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
import ee.ria.xroad.common.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;

@Service
public class XROADValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XROADValidationService.class);
    private XROADValidationServiceProperties properties;

    @Override
    public QualifiedReport validateDocument(ValidationDocument wsDocument) {
        final InputStream inputStream = new ByteArrayInputStream(wsDocument.getBytes());

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

    private static void appendCert(StringBuilder builder, X509Certificate cert) {
        builder.append("        Subject: " + cert.getSubjectDN().getName() + "\n");
        builder.append("        Issuer: " + cert.getIssuerDN().getName() + "\n");
        builder.append("        Serial number: " + cert.getSerialNumber() + "\n");
        builder.append("        Valid from: " + cert.getNotBefore() + "\n");
        builder.append("        Valid until: " + cert.getNotAfter() + "\n");
    }

    private void onVerificationSucceeded(AsicContainerVerifier verifier) throws IOException {
        LOGGER.info(AsicUtils.buildSuccessOutput(verifier));

        AsicContainer asic = verifier.getAsic();
        StringBuilder builder = new StringBuilder();

        Signature signature = verifier.getSignature();

        builder.append("Verification successful.\n");
        builder.append("Signer\n");
        builder.append("    Certificate:\n");
        appendCert(builder, verifier.getSignerCert());
        builder.append("    ID: " + verifier.getSignerName() + "\n");
        builder.append("OCSP response\n");
        builder.append("    Signed by:\n");
        appendCert(builder, verifier.getOcspCert());
        builder.append("    Produced at: " + verifier.getOcspDate() + "\n");
        builder.append("Timestamp\n");
        builder.append("    Signed by:\n");
        appendCert(builder, verifier.getTimestampCert());
        builder.append("    Date: " + verifier.getTimestampDate() + "\n");

//        writeToFile(AsicContainerEntries.ENTRY_MESSAGE, asic.getMessage());
        LOGGER.info("");
    }

    @Autowired
    public void setProperties(XROADValidationServiceProperties properties) {
        this.properties = properties;
    }
}
