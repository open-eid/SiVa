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

package ee.openeid.validation.service.generic;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Reports;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.ValidationService;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import ee.openeid.siva.validation.service.signature.policy.properties.ConstraintDefinedPolicy;
import ee.openeid.tsl.configuration.AlwaysFailingCRLSource;
import ee.openeid.tsl.configuration.AlwaysFailingOCSPSource;
import ee.openeid.validation.service.generic.validator.report.DigestValidationReportBuilder;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestDocument;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;

@Service
public class DigestValidationService extends GenericValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DigestValidationService.class);

    @Override
    public Reports validateDocuments(ValidationDocument validationDocument, List<DigestDocument> digestDocuments, DSSDocument signatureDocument) throws DSSException {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WsValidateDocument: begin");
            }
            if (CollectionUtils.isEmpty(digestDocuments)) {
                throw new ValidationServiceException(getClass().getSimpleName(), new Exception("No request document(s) found"));
            }
            final ConstraintDefinedPolicy policy = signaturePolicyService.getPolicy(validationDocument.getSignaturePolicy());
            SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signatureDocument);
            CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier(this.trustedListsCertificateSource,
                new AlwaysFailingCRLSource(), new AlwaysFailingOCSPSource(), new CommonsDataLoader());
            LOGGER.info("Certificate pool size: {}", this.getCertificatePoolSize(certificateVerifier));
            validator.setCertificateVerifier(certificateVerifier);
            validator.setValidationLevel(VALIDATION_LEVEL);
            validator.setDetachedContents(digestDocuments.stream().collect(Collectors.toList()));
            final eu.europa.esig.dss.validation.reports.Reports reports = validator.validateDocument(policy.getConstraintDataStream());
            this.validateRevocationFreshness(reports);
            //this.validateBestSignatureTime(reports);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                    "Validation completed. Total signature count: {} and valid signature count: {}",
                    reports.getSimpleReport().getSignaturesCount(),
                    reports.getSimpleReport().getValidSignaturesCount()
                );
                LOGGER.info("WsValidateDocument: end");
            }
            final DigestValidationReportBuilder reportBuilder = new DigestValidationReportBuilder(
                reports,
                VALIDATION_LEVEL,
                validationDocument,
                policy
            );
            return reportBuilder.build();
        } catch (InvalidPolicyException e) {
            endExceptionally(e);
            throw e;
        } catch (MalformedDocumentException | DSSException e) {
            endExceptionally(e);
            throw new MalformedDocumentException(e);
        } catch (Exception e) {
            endExceptionally(e);
            throw new ValidationServiceException(getClass().getSimpleName(), e);
        }
    }

    private void endExceptionally(Exception e) {
        LOGGER.error(e.getMessage(), e);
        LOGGER.info("WsValidateDocument: end with exception");
    }

}
