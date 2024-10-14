/*
 * Copyright 2016 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.generic;

import com.google.common.io.BaseEncoding;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.tsl.configuration.AlwaysFailingCRLSource;
import ee.openeid.validation.service.generic.validator.RevocationFreshnessValidatorFactory;
import ee.openeid.validation.service.generic.validator.ocsp.OCSPSourceFactory;
import ee.openeid.validation.service.generic.validator.container.ContainerValidatorFactory;
import ee.openeid.validation.service.generic.validator.report.GenericValidationReportBuilder;
import ee.openeid.validation.service.generic.validator.report.ReportBuilderData;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.TokenExtractionStrategy;
import eu.europa.esig.dss.exception.IllegalInputException;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import org.digidoc4j.impl.asic.xades.XadesValidationReportProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_POLICY_SERVICE_BEAN_NAME;
import static ee.openeid.validation.service.generic.GenericValidationConstants.GENERIC_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME;

@Service
public class GenericValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericValidationService.class);
    private static final ValidationLevel VALIDATION_LEVEL = ValidationLevel.ARCHIVAL_DATA;

    private TrustedListsCertificateSource trustedListsCertificateSource;
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    private ReportConfigurationProperties reportConfigurationProperties;
    private ProxyConfig proxyConfig;
    private ContainerValidatorFactory containerValidatorFactory;
    private RevocationFreshnessValidatorFactory revocationFreshnessValidatorFactory;
    private OCSPSourceFactory ocspSourceFactory;

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) throws DSSException {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new ValidationServiceException(getClass().getSimpleName(), new Exception("No request document found"));
            }
            SignedDocumentValidator validator = createValidatorFromDocument(validationDocument);

            final ConstraintDefinedPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());

            final eu.europa.esig.dss.validation.reports.Reports reports = validator.validateDocument(policy.getConstraintDataStream());
            XadesValidationReportProcessor.process(reports);

            //For large PDF files the getSignatures() method is currently expensive.
            //Initialize once and use in different components to reduce response time for large PDF files validation.
            List<AdvancedSignature> signatures = validator.getSignatures();

            revocationFreshnessValidatorFactory.create(reports, policy).validate();
            containerValidatorFactory.create(reports, validationDocument).validate();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getSignaturesCount(),
                        reports.getSimpleReport().getValidSignaturesCount()
                );

                LOGGER.info("WsValidateDocument: end");
            }
            ReportBuilderData reportBuilderData = ReportBuilderData.builder()
                    .dssReports(reports)
                    .validationLevel(VALIDATION_LEVEL)
                    .validationDocument(validationDocument)
                    .policy(policy)
                    .isReportSignatureEnabled(reportConfigurationProperties.isReportSignatureEnabled())
                    .trustedListsCertificateSource(trustedListsCertificateSource)
                    .signatures(signatures)
                    .build();

            return new GenericValidationReportBuilder(reportBuilderData).build();
        } catch (InvalidPolicyException e) {
            endExceptionally(e);
            throw e;
        } catch (MalformedDocumentException | DSSException | IllegalInputException e) {
            endExceptionally(e);
            throw constructMalformedDocumentException(e);
        } catch (Exception e) {

            endExceptionally(e);
            if (e.getCause() instanceof BaseEncoding.DecodingException) {
                throw constructMalformedDocumentException(e);
            }
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    protected SignedDocumentValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
        final DSSDocument dssDocument = createDssDocument(validationDocument);
        SignedDocumentValidator validator = createSignedDocumentValidator(dssDocument);
        CommonCertificateVerifier certificateVerifier = createCertificateVerifier();

        LOGGER.debug("Certificate pool size: {}", getCertificatePoolSize(certificateVerifier));
        validator.setCertificateVerifier(certificateVerifier);
        validator.setValidationLevel(VALIDATION_LEVEL);
        Optional.ofNullable(validationDocument.getValidationTime()).ifPresent(validator::setValidationTime);

        validator.setTokenExtractionStrategy(TokenExtractionStrategy.EXTRACT_TIMESTAMPS_AND_REVOCATION_DATA);
        return validator;
    }

    protected DSSDocument createDssDocument(final ValidationDocument validationDocument) {
        if (validationDocument == null) {
            return null;
        }
        final InMemoryDocument dssDocument = new InMemoryDocument(validationDocument.getBytes());
        dssDocument.setName(validationDocument.getName());
        dssDocument.setMimeType(MimeType.fromFileName(validationDocument.getName()));

        return dssDocument;
    }

    private SignedDocumentValidator createSignedDocumentValidator(DSSDocument dssDocument) {
        try {
            return SignedDocumentValidator.fromDocument(dssDocument);
        } catch (UnsupportedOperationException e) {
            // Some of the validation proxies make decisions based on that specific exception message
            // TODO: find a better mechanism for communicating to the validation proxies
            //  when such a document format is not supported by DSS
            throw new IllegalInputException(e.getMessage(), e);
        }
    }

    private CommonCertificateVerifier createCertificateVerifier() {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier(true);
        certificateVerifier.setTrustedCertSources(trustedListsCertificateSource);
        certificateVerifier.setOcspSource(ocspSourceFactory.create());
        certificateVerifier.setCrlSource(new AlwaysFailingCRLSource());

        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(proxyConfig);

        DefaultAIASource aiaSource = new DefaultAIASource(dataLoader);
        certificateVerifier.setAIASource(aiaSource);

        return certificateVerifier;
    }

    private int getCertificatePoolSize(CommonCertificateVerifier certificateVerifier) {
        return certificateVerifier.getTrustedCertSources().getNumberOfCertificates();
    }

    protected RuntimeException constructMalformedDocumentException(Exception cause) {
        return new MalformedDocumentException(cause);
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

    @Autowired
    public void setProxyConfig(ProxyConfig proxyConfig){
        this.proxyConfig = proxyConfig;
    }

    @Autowired
    @Qualifier(value = GENERIC_POLICY_SERVICE_BEAN_NAME)
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    @Qualifier(value = GENERIC_TRUSTED_LISTS_CERTIFICATE_SOURCE_BEAN_NAME)
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListsCertificateSource) {
        this.trustedListsCertificateSource = trustedListsCertificateSource;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }

    @Autowired
    public void setContainerValidatorFactory(ContainerValidatorFactory containerValidatorFactory) {
        this.containerValidatorFactory = containerValidatorFactory;
    }

    @Autowired
    public void setRevocationFreshnessValidatorFactory(RevocationFreshnessValidatorFactory revocationFreshnessValidatorFactory) {
        this.revocationFreshnessValidatorFactory = revocationFreshnessValidatorFactory;
    }

    @Autowired
    public void setOcspSourceFactory(OCSPSourceFactory ocspSourceFactory) {
        this.ocspSourceFactory = ocspSourceFactory;
    }
}
