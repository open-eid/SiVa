/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.reports.wrapper.CertificateWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.DiagnosticData;
import eu.europa.esig.dss.validation.reports.wrapper.SignatureWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.TimestampWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.time.DateUtils.addMilliseconds;

@Service
public class GenericValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericValidationService.class);
    private static final ValidationLevel VALIDATION_LEVEL = ValidationLevel.ARCHIVAL_DATA;
    private static final int REVOCATION_FRESHNESS_DAY_DIFFERENCE = 86400000;
    private static final int REVOCATION_FRESHNESS_FIFTEEN_MINUTES_DIFFERENCE = 900000;
    private static final String REVOCATION_FRESHNESS_FAULT = "The revocation information is not considered as 'fresh'.";
    private static final String CRL_REVOCATION_SOURCE = "CRLToken";

    private TrustedListsCertificateSource trustedListsCertificateSource;
    private ConstraintLoadingSignaturePolicyService signaturePolicyService;
    private ReportConfigurationProperties reportConfigurationProperties;

    private static boolean isInRangeMillis(Date date1, Date date2, int rangeInMillis) {
        Date latestTime = addMilliseconds(date2, rangeInMillis);
        Date earliestTime = addMilliseconds(date2, -rangeInMillis);
        return !date1.before(latestTime) || !date1.after(earliestTime);
    }

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

            validateRevocationFreshness(reports);

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
                    VALIDATION_LEVEL,
                    validationDocument,
                    policy,
                    reportConfigurationProperties.isReportSignatureEnabled()
            );
            return reportBuilder.build();
        } catch (InvalidPolicyException e) {
            endExceptionally(e);
            throw e;
        } catch (MalformedDocumentException | DSSException e) {
            endExceptionally(e);
            throw constructMalformedDocumentException(e);
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    protected SignedDocumentValidator createValidatorFromDocument(final ValidationDocument validationDocument) {
        final DSSDocument dssDocument = createDssDocument(validationDocument);
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);

        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier(trustedListsCertificateSource,
                new AlwaysFailingCRLSource(), new AlwaysFailingOCSPSource(), new CommonsDataLoader());
        LOGGER.info("Certificate pool size: {}", getCertificatePoolSize(certificateVerifier));
        validator.setCertificateVerifier(certificateVerifier);
        validator.setValidationLevel(VALIDATION_LEVEL);

        return validator;
    }

    void validateRevocationFreshness(eu.europa.esig.dss.validation.reports.Reports reports) {

        DiagnosticData diagnosticData = reports.getDiagnosticData();

        if (diagnosticData.getUsedCertificates() != null && diagnosticData.getFirstSigningCertificateId() != null) {
            for (CertificateWrapper certificateWrapper : diagnosticData.getUsedCertificates()) {
                for (SignatureWrapper signatureWrapper : diagnosticData.getSignatures()) {
                    if (certificateWrapper.getId().equals(signatureWrapper.getSigningCertificateId()) && !signatureWrapper.getTimestampList().isEmpty()) {
                        TimestampWrapper timeStampWrapper = getFirstTimestamp(signatureWrapper.getTimestampList());
                        if (timeStampWrapper.getProductionTime() == null)
                            return;
                        boolean revocationFreshnessCheckInvokeError = isRevocationFreshnessCheckInvalid(certificateWrapper, timeStampWrapper);
                        if (revocationFreshnessCheckInvokeError) {
                            reports.getSimpleReport().getErrors(signatureWrapper.getId()).add(REVOCATION_FRESHNESS_FAULT);
                        } else {
                            boolean revocationFreshnessCheckInvokeWarning = certificateWrapper.getRevocationData().stream().anyMatch(
                                    r -> !CRL_REVOCATION_SOURCE.equals(r.getSource()) && isInRangeMillis(r.getProductionDate(), timeStampWrapper.getProductionTime(), REVOCATION_FRESHNESS_FIFTEEN_MINUTES_DIFFERENCE));
                            if (revocationFreshnessCheckInvokeWarning) {
                                reports.getSimpleReport().getWarnings(signatureWrapper.getId()).add(REVOCATION_FRESHNESS_FAULT);
                            }
                        }
                    }
                }
            }
        }
    }

    DSSDocument createDssDocument(final ValidationDocument validationDocument) {
        if (validationDocument == null) {
            return null;
        }
        final InMemoryDocument dssDocument = new InMemoryDocument(validationDocument.getBytes());
        dssDocument.setName(validationDocument.getName());
        dssDocument.setMimeType(MimeType.fromFileName(validationDocument.getName()));

        return dssDocument;
    }

    private boolean isRevocationFreshnessCheckInvalid(CertificateWrapper certificateWrapper, TimestampWrapper timeStampWrapper) {
        return certificateWrapper.getRevocationData().stream().anyMatch(
                r -> {
                    if (CRL_REVOCATION_SOURCE.equals(r.getSource())) {
                        return !(timeStampWrapper.getProductionTime().after(r.getThisUpdate()) && timeStampWrapper.getProductionTime().before(r.getNextUpdate()));
                    }
                    return isInRangeMillis(r.getProductionDate(), timeStampWrapper.getProductionTime(), REVOCATION_FRESHNESS_DAY_DIFFERENCE);
                });
    }

    private TimestampWrapper getFirstTimestamp(List<TimestampWrapper> timestamps) {
        return Collections.min(timestamps, Comparator.comparing(TimestampWrapper::getProductionTime));
    }

    private int getCertificatePoolSize(CommonCertificateVerifier certificateVerifier) {
        return certificateVerifier.getTrustedCertSource().getCertificatePool().getNumberOfCertificates();
    }

    protected RuntimeException constructMalformedDocumentException(RuntimeException cause) {
        return new MalformedDocumentException(cause);
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

    @Autowired
    @Qualifier(value = "GenericPolicyService")
    public void setSignaturePolicyService(ConstraintLoadingSignaturePolicyService signaturePolicyService) {
        this.signaturePolicyService = signaturePolicyService;
    }

    @Autowired
    public void setTrustedListsCertificateSource(TrustedListsCertificateSource trustedListsCertificateSource) {
        this.trustedListsCertificateSource = trustedListsCertificateSource;
    }

    @Autowired
    public void setReportConfigurationProperties(ReportConfigurationProperties reportConfigurationProperties) {
        this.reportConfigurationProperties = reportConfigurationProperties;
    }
}
