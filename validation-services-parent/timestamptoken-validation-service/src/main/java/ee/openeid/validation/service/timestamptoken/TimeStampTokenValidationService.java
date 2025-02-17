/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.DocumentRequirementsException;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.tsl.configuration.AlwaysFailingCRLSource;
import ee.openeid.tsl.configuration.AlwaysFailingOCSPSource;
import ee.openeid.validation.service.timestamptoken.util.TimestampNotGrantedValidationUtils;
import ee.openeid.validation.service.timestamptoken.validator.report.TimeStampTokenValidationReportBuilder;
import eu.europa.esig.dss.asic.cades.validation.ASiCContainerWithCAdESValidator;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.TokenExtractionStrategy;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static eu.europa.esig.dss.asic.common.ASiCUtils.META_INF_FOLDER;
import static eu.europa.esig.dss.asic.common.ASiCUtils.MIME_TYPE;

@Service
public class TimeStampTokenValidationService implements ValidationService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeStampTokenValidationService.class);
    private static final ValidationLevel VALIDATION_LEVEL = ValidationLevel.ARCHIVAL_DATA;

    private static final String TIMESTAMP_FILE = "TIMESTAMP.TST";
    private static final String SIGNATURE_FILE_EXTENSION_P7S = "SIGNATURE.P7S";
    private static final String EVIDENCE_RECORD_FILE_EXTENSION_ERS = "EVIDENCERECORD.ERS";
    private static final String EVIDENCE_RECORD_FILE_EXTENSION_XML = "EVIDENCERECORD.XML";
    private static final String SIGNATURE_FILE_EXTENSION_XML = "SIGNATURES.XML";

    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    private TrustedListsCertificateSource trustedListsCertificateSource;
    private ReportConfigurationProperties reportConfigurationProperties;


    @Override
    public Reports validateDocument(ValidationDocument validationDocument) {
        String datafileName = validateContainerAndGetDatafileName(validationDocument);

        try {
            ASiCContainerWithCAdESValidator validator = createValidatorFromDocument(validationDocument);
            setValidationTimeIfNeeded(validator, validationDocument.getValidationTime());

            final ConstraintDefinedPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());
            final eu.europa.esig.dss.validation.reports.Reports reports = validator.validateDocument(policy.getConstraintDataStream());
            TimestampNotGrantedValidationUtils.convertNotGrantedErrorsToWarnings(reports);

            return new TimeStampTokenValidationReportBuilder(
                reports,
                validator.getDetachedTimestamps(),
                validationDocument,
                datafileName,
                policy,
                trustedListsCertificateSource,
                reportConfigurationProperties.isReportSignatureEnabled()
            ).build();
        } catch (InvalidPolicyException e) {
            throw e;
        } catch (DSSException e) {
            throw new MalformedDocumentException(e);
        } catch (Exception e) {
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    protected ASiCContainerWithCAdESValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
        final DSSDocument dssDocument = createDssDocument(validationDocument);

        ASiCContainerWithCAdESValidator validator = new ASiCContainerWithCAdESValidator(dssDocument);
        CommonCertificateVerifier certificateVerifier = createCertificateVerifier();

        LOGGER.info("Certificate pool size: {}", certificateVerifier.getTrustedCertSources().getNumberOfCertificates());
        validator.setCertificateVerifier(certificateVerifier);
        validator.setValidationLevel(VALIDATION_LEVEL);

        validator.setTokenExtractionStrategy(TokenExtractionStrategy.EXTRACT_TIMESTAMPS_AND_REVOCATION_DATA);

        return validator;
    }

    private void setValidationTimeIfNeeded(ASiCContainerWithCAdESValidator validator, Date validationTime) {
        Optional.ofNullable(validationTime).ifPresent(validator::setValidationTime);
    }

    private static DSSDocument createDssDocument(final ValidationDocument validationDocument) {
        if (validationDocument == null) {
            return null;
        }
        final InMemoryDocument dssDocument = new InMemoryDocument(validationDocument.getBytes());
        dssDocument.setName(validationDocument.getName());
        dssDocument.setMimeType(MimeType.fromFileName(validationDocument.getName()));

        return dssDocument;
    }

    private CommonCertificateVerifier createCertificateVerifier() {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier(true);
        certificateVerifier.setTrustedCertSources(trustedListsCertificateSource);
        certificateVerifier.setOcspSource(new AlwaysFailingOCSPSource());
        certificateVerifier.setCrlSource(new AlwaysFailingCRLSource());

        return certificateVerifier;
    }

    private String validateContainerAndGetDatafileName(ValidationDocument validationDocument) {
        String dataFileName = null;

        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(validationDocument.getBytes()))) {
            long dataFileCount = 0;
            long timeStampCount = 0;

            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                String n = entry.getName();

                if (META_INF_FOLDER.equals(n)) {
                    continue;
                }

                if (n.startsWith(META_INF_FOLDER)) {
                    if (n.toUpperCase().endsWith(TIMESTAMP_FILE)) {
                        if (++timeStampCount > 1) {
                            throw new DocumentRequirementsException();
                        }
                    } else if (StringUtils.equalsAny(
                        getFileFromFullPath(n.toUpperCase()),
                        SIGNATURE_FILE_EXTENSION_P7S,
                        SIGNATURE_FILE_EXTENSION_XML,
                        EVIDENCE_RECORD_FILE_EXTENSION_ERS,
                        EVIDENCE_RECORD_FILE_EXTENSION_XML
                    )) {
                        throw new DocumentRequirementsException();
                    }
                } else if (!n.endsWith(MIME_TYPE)) {
                    if (++dataFileCount > 1) {
                        throw new DocumentRequirementsException();
                    }
                    dataFileName = n;
                }
            }

            if (dataFileCount == 0 || timeStampCount == 0) {
                throw new DocumentRequirementsException();
            }
        } catch (IOException e) {
            throw new MalformedDocumentException(e);
        }
        return dataFileName;
    }

    private static String getFileFromFullPath(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    @Autowired
    @Qualifier("timestampPolicyService")
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    @Qualifier(value = "genericTrustedListsCertificateSource")
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListsCertificateSource) {
        this.trustedListsCertificateSource = trustedListsCertificateSource;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }

}
