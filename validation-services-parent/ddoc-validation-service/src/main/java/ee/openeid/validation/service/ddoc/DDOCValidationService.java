package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.ddoc.configuration.DDOCValidationServiceProperties;
import ee.openeid.validation.service.ddoc.report.DDOCQualifiedReportBuilder;
import ee.openeid.validation.service.ddoc.security.SecureSAXParsers;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.*;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DDOCValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DDOCValidationService.class);
    private final Object lock = new Object();

    private DDOCValidationServiceProperties properties;
    private SignaturePolicyService<ValidationPolicy> signaturePolicyService;

    @PostConstruct
    protected void initConfig() throws DigiDocException, IOException, SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        final File file = File.createTempFile("siva-ddoc-jdigidoc-", ".cfg");
        try (
            InputStream inputStream = getClass().getResourceAsStream(properties.getJdigidocConfigurationFile());
            OutputStream outputStream = new FileOutputStream(file)
        ) {
            LOGGER.info("Copying DDOC configuration file: {}", file.getAbsolutePath());
            IOUtils.copy(inputStream, outputStream);
        } finally {
            LOGGER.info("Removing configuration file: {}", file.getAbsolutePath());
            file.deleteOnExit();
        }
        ConfigManager.init(file.getAbsolutePath());
    }

    @Override
    public QualifiedReport validateDocument(ValidationDocument validationDocument) {
        ValidationPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        validateAgainstXMLEntityAttacks(validationDocument.getBytes());

        synchronized (lock) {
            SignedDoc signedDoc = null;
            List<DigiDocException> signedDocInitializationErrors = new ArrayList<>();

            try {
                DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
                signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(validationDocument.getBytes()), false, signedDocInitializationErrors);
                if (signedDoc == null) {
                    throw new MalformedDocumentException();
                }
                Date validationTime = new Date();
                DDOCQualifiedReportBuilder reportBuilder = new DDOCQualifiedReportBuilder(signedDoc, validationDocument.getName(), validationTime, policy);
                return reportBuilder.build();
            } catch (DigiDocException e) {
                LOGGER.warn("Unexpected exception when validating DDOC document: " + e.getMessage(), e);
                throw new ValidationServiceException(getClass().getSimpleName(), e);
            } finally {
                if (signedDoc != null) {
                    signedDoc.cleanupDfCache();
                }
            }
        }
    }

    protected void validateAgainstXMLEntityAttacks(byte[] xmlContent) {
        try {
            SAXParser saxParser = SecureSAXParsers.createParser();
            saxParser.getXMLReader().parse(new InputSource(new ByteArrayInputStream(xmlContent)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("Exception when validation document against XML entity attacks: " + e.getMessage(), e);
            throw new MalformedDocumentException(e);
        }
    }

    @Autowired
    public void setProperties(DDOCValidationServiceProperties properties) {
        this.properties = properties;
    }

    @Autowired
    @Qualifier(value = "DDOCPolicyService")
    public void setSignaturePolicyService(SignaturePolicyService<ValidationPolicy> signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }
}
