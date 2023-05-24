/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.ADES_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.UA_POLICY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PDFSignaturePolicyTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";
    private static final String PDF_WITH_SOFT_CERT_SIGNATURE = "soft-cert-signature.pdf";

    @Test
    @Disabled //TODO: New test file is needed
    public void softCertSignatureShouldBeValidWithNoTypePolicy() {
        SimpleReport report = validateWithPolicy("POLv3", PDF_WITH_SOFT_CERT_SIGNATURE);
        ValidationConclusion validationConclusion = report.getValidationConclusion();
        assertEquals(validationConclusion.getSignaturesCount(), validationConclusion.getValidSignaturesCount());
    }

    @Test
    @Disabled //TODO: New test file is needed
    public void softCertSignatureShouldBeInvalidWithQESPolicy() {
        SimpleReport report = validateWithPolicy("POLv4", PDF_WITH_SOFT_CERT_SIGNATURE);
        assertTrue(report.getValidationConclusion().getValidSignaturesCount() == 0);
    }

    @Test
    public void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() {
        Policy policy = validateWithPolicy("").getValidationConclusion().getPolicy();
        assertEquals(QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainAdesPolicyWhenAdesPolicyIsGivenToValidator() {
        Policy policy = validateWithPolicy("POLv3").getValidationConclusion().getPolicy();
        assertEquals(ADES_POLICY.getName(), policy.getPolicyName());
        assertEquals(ADES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(ADES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() {
        Policy policy = validateWithPolicy("POLv4").getValidationConclusion().getPolicy();
        assertEquals(QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainUAPolicyWhenUAPolicyIsGivenToValidator() {
        Policy policy = validateWithPolicy("POLv5").getValidationConclusion().getPolicy();
        assertEquals(UA_POLICY.getName(), policy.getPolicyName());
        assertEquals(UA_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(UA_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() {
        assertThrows(
                InvalidPolicyException.class, () -> validateWithPolicy("non-existing-policy")
        );
    }

    private SimpleReport validateWithPolicy(String policyName) {
        return validateWithPolicy(policyName, PDF_WITH_ONE_VALID_SIGNATURE);
    }

    private SimpleReport validateWithPolicy(String policyName, String document) {
        ValidationDocument validationDocument = buildValidationDocument(document);
        validationDocument.setSignaturePolicy(policyName);
        return validateAndAssertReports(validationDocument).getSimpleReport();
    }
}
