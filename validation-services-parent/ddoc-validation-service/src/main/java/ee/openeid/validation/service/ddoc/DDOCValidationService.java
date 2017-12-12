/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.properties.ValidationPolicy;
import ee.openeid.validation.service.ddoc.configuration.DDOCValidationServiceProperties;
import ee.openeid.validation.service.ddoc.report.DDOCValidationReportBuilder;
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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.util.ArrayList;

@Service
public class DDOCValidationService implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DDOCValidationService.class);
    private final Object lock = new Object();

    private ReportConfigurationProperties reportConfigurationProperties;

    private DDOCValidationServiceProperties properties;
    private SignaturePolicyService<ValidationPolicy> signaturePolicyService;
    private XMLEntityAttackValidator xmlEntityAttackValidator;

    @PostConstruct
    protected void initConfig() throws DigiDocException, IOException, SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        synchronized (lock) {
            final File file = File.createTempFile("siva-ddoc-jdigidoc-", ".cfg");
            try (
                    InputStream inputStream = getClass().getResourceAsStream(properties.getJdigidocConfigurationFile());
                    OutputStream outputStream = new FileOutputStream(file)
            ) {
                LOGGER.info("Copying DDOC configuration file: {}", file.getAbsolutePath());
                LOGGER.info("jdigidoc.cfg original path: {}", getClass().getResource(properties.getJdigidocConfigurationFile()));

                IOUtils.copy(inputStream, outputStream);
                LOGGER.info("jdigidoc.cfg contents: {}", Files.readAllLines(Paths.get(file.getAbsolutePath())));
            } finally {
                LOGGER.info("Removing configuration file: {}", file.getAbsolutePath());
                file.deleteOnExit();
            }
            ConfigManager.init(file.getAbsolutePath());
            LOGGER.info("DDOC hashcode support in configuration load is: {}", ConfigManager.instance().getProperty("DATAFILE_HASHCODE_MODE"));
        }
    }

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {
        ValidationPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        xmlEntityAttackValidator.validateAgainstXMLEntityAttacks(validationDocument.getBytes());

        synchronized (lock) {
            SignedDoc signedDoc = null;

            LOGGER.info("DDOC hashcode support in validation is: {}", ConfigManager.instance().getProperty("DATAFILE_HASHCODE_MODE"));
            try {
                DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
                ArrayList signedDocInitializationErrors = new ArrayList<>();
                signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(validationDocument.getBytes()), false, signedDocInitializationErrors);
                if (signedDoc == null || hasNonWarningErrs(signedDoc, signedDocInitializationErrors) || hasNonWarningErrs(signedDoc, validateContainer(signedDoc))) {
                    throw new MalformedDocumentException();
                }

                DDOCValidationReportBuilder reportBuilder = new DDOCValidationReportBuilder(signedDoc, validationDocument, policy, reportConfigurationProperties.isReportSignatureEnabled());
                return reportBuilder.build();
            } catch (MalformedDocumentException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.warn("Unexpected exception when validating DDOC document: " + e.getMessage(), e);
                throw new ValidationServiceException(getClass().getSimpleName(), e);
            } finally {
                if (signedDoc != null) {
                    signedDoc.cleanupDfCache();
                }
            }
        }
    }

    private boolean isWarning(DigiDocException ex, SignedDoc signedDoc) {
        return ex != null &&
                (ex.getCode() == DigiDocException.ERR_DF_INV_HASH_GOOD_ALT_HASH ||
                        ex.getCode() == DigiDocException.ERR_OLD_VER ||
                        ex.getCode() == DigiDocException.ERR_TEST_SIGNATURE ||
                        ex.getCode() == DigiDocException.WARN_WEAK_DIGEST ||
                        ex.getCode() == DigiDocException.ERR_CERT_REVOKED ||
                        ex.getCode() == DigiDocException.ERR_CERT_UNKNOWN ||
                        (ex.getCode() == DigiDocException.ERR_ISSUER_XMLNS && !signedDoc.getFormat().equals(SignedDoc.FORMAT_SK_XML)));
    }

    private boolean hasNonWarningErrs(SignedDoc signedDoc, ArrayList errors) {
        for (Object error : errors) {
            if (error instanceof DigiDocException) {
                DigiDocException ex = (DigiDocException) error;
                if (!isWarning(ex, signedDoc)) {
                    return true;
                }
            }
        }
        return false;
    }


    private ArrayList validateContainer(SignedDoc signedDoc) {
        return signedDoc.validate(true);
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

    @Autowired
    public void setXMLEntityAttackValidator(XMLEntityAttackValidator xmlEntityAttackValidator) {
        this.xmlEntityAttackValidator = xmlEntityAttackValidator;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }
}
