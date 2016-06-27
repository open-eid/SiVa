package ee.openeid.siva.xroad;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import ee.ria.xroad.common.CodedException;
import ee.ria.xroad.common.SystemProperties;
import ee.ria.xroad.common.asic.AsicContainer;
import ee.ria.xroad.common.asic.AsicContainerVerifier;
import ee.ria.xroad.common.asic.AsicUtils;
import ee.ria.xroad.common.conf.globalconf.GlobalConf;
import ee.ria.xroad.common.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

@Service
public class XROADValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(XROADValidationService.class);

    @Override
    public QualifiedReport validateDocument(ValidationDocument wsDocument) {
        final InputStream inputStream = new ByteArrayInputStream(wsDocument.getBytes());

        try {
            final AsicContainer container = AsicContainer.read(inputStream);
            final AsicContainerVerifier verifier = new AsicContainerVerifier(container);

            verifier.verify();
            onVerificationSucceeded(verifier);

            QualifiedReport outputReport = new QualifiedReport();
            outputReport.setPolicy(Policy.SIVA_DEFAULT);
            outputReport.setDocumentName(wsDocument.getName());

            return outputReport;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @PostConstruct
    private void loadConf() {
        String confPath = getClass().getResource("/verificationconf").getPath();
        System.setProperty(SystemProperties.CONFIGURATION_PATH, confPath);

        LOGGER.info("Loading configuration from " + "." + "...");
        System.out.println("logging random stuff");
        try {
            GlobalConf.reload();
//            verifyConfPathCorrectness();
        } catch (CodedException e) {
            System.err.println("Unable to load configuration: "
                    + e);
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
//        LOGGER.info("\nWould you like to extract the signed files? (y/n) ");

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
        System.out.println(builder);
        System.out.println("Files successfully extracted.");
//        LOGGER.info("");
    }
}
