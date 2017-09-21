/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.ConstraintLoadingSignaturePolicyService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.tsl.configuration.AlwaysFailingCRLSource;
import ee.openeid.tsl.configuration.AlwaysFailingOCSPSource;
import ee.openeid.validation.service.generic.validator.report.GenericValidationReportBuilder;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class GenericValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericValidationService.class);
    private final Object lock = new Object();

    private TrustedListsCertificateSource trustedListsCertificateSource;
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;

    @Override
    public Reports validateDocument(ValidationDocument validationDocument) throws DSSException {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WsValidateDocument: begin");
            }

            if (validationDocument == null) {
                throw new ValidationServiceException(getClass().getSimpleName(), new Exception("No request document found"));
            }

            final DSSDocument dssDocument = createDssDocument(validationDocument);
            final ConstraintDefinedPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());
            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);

            CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier(trustedListsCertificateSource,
                    new AlwaysFailingCRLSource(), new AlwaysFailingOCSPSource(), new CommonsDataLoader());
            LOGGER.info("Certificate pool size: {}", getCertificatePoolSize(certificateVerifier));
            validator.setCertificateVerifier(certificateVerifier);
            final eu.europa.esig.dss.validation.reports.Reports reports;
            synchronized (lock) {
                reports = validator.validateDocument(policy.getConstraintDataStream());
            }

            final ZonedDateTime validationTimeInGMT = ZonedDateTime.now(ZoneId.of("GMT"));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "Validation completed. Total signature count: {} and valid signature count: {}",
                        reports.getSimpleReport().getSignaturesCount(),
                        reports.getSimpleReport().getValidSignaturesCount()
                );

                LOGGER.info("WsValidateDocument: end");
            }

            final GenericValidationReportBuilder reportBuilder = new GenericValidationReportBuilder(
                    reports,
                    validationTimeInGMT,
                    validationDocument,
                    policy
            );
            return reportBuilder.build();
        } catch (MalformedDocumentException | InvalidPolicyException | DSSException e) {
            endExceptionally(e);
            throw e;
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private int getCertificatePoolSize(CommonCertificateVerifier certificateVerifier) {
        return certificateVerifier.getTrustedCertSource().getCertificatePool().getNumberOfCertificates();
    }

    private DSSDocument createDssDocument(final ValidationDocument ValidationDocument) {
        if (ValidationDocument == null) {
            return null;
        }
        final InMemoryDocument dssDocument = new InMemoryDocument(ValidationDocument.getBytes());
        dssDocument.setName(ValidationDocument.getName());
        dssDocument.setMimeType(MimeType.fromFileName(ValidationDocument.getName()));

        return dssDocument;
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

    @Autowired
    @Qualifier(value = "PDFPolicyService")
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListsCertificateSource) {
        this.trustedListsCertificateSource = trustedListsCertificateSource;
    }
}
