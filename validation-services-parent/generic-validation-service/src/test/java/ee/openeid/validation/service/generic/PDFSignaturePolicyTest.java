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
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.junit.Ignore;
import org.junit.Test;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.ADES_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PDFSignaturePolicyTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";
    private static final String PDF_WITH_SOFT_CERT_SIGNATURE = "soft-cert-signature.pdf";

    @Test
    public void softCertSignatureShouldBeValidWithNoTypePolicy() throws Exception {
        SimpleReport report = validateWithPolicy("POLv3", PDF_WITH_SOFT_CERT_SIGNATURE);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(validationConclusion.getSignaturesCount(), validationConclusion.getValidSignaturesCount());
    }

    @Test @Ignore //TODO: New test file is needed
    public void softCertSignatureShouldBeInvalidWithQESPolicy() throws Exception {
        SimpleReport report = validateWithPolicy("POLv4", PDF_WITH_SOFT_CERT_SIGNATURE);
        assertTrue(report.getValidationConclusion().getValidSignaturesCount() == 0);
    }

    @Test
    public void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() throws Exception {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv3").getValidationConclusion().getPolicy();
        assertEquals(ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv4").getValidationConclusion().getPolicy();
        assertEquals(QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() throws Exception {
        expectedException.expect(InvalidPolicyException.class);
        validateWithPolicy("non-existing-policy");
    }

    private SimpleReport validateWithPolicy(String policyName) throws Exception {
        return validateWithPolicy(policyName, PDF_WITH_ONE_VALID_SIGNATURE);
    }

    private SimpleReport validateWithPolicy(String policyName, String document) throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(document);
        validationDocument.setSignaturePolicy(policyName);
        return validationService.validateDocument(validationDocument).getSimpleReport();
    }
}
