package ee.openeid.validation.service.pdf;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.Policy;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.signature.policy.InvalidPolicyException;
import org.junit.Test;

import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.NO_TYPE_POLICY;
import static ee.openeid.siva.validation.service.signature.policy.PredefinedValidationPolicySource.QES_POLICY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PDFSignaturePolicyTest extends PDFValidationServiceTest {

    private static final String PDF_WITH_ONE_VALID_SIGNATURE = "hellopades-pades-lt-sha256-sign.pdf";
    private static final String PDF_WITH_SOFT_CERT_SIGNATURE = "soft-cert-signature.pdf";

    @Test
    public void softCertSignatureShouldBeValidWithNoTypePolicy() throws Exception {
        QualifiedReport report = validateWithPolicy("POLv1", PDF_WITH_SOFT_CERT_SIGNATURE);
        assertEquals(report.getSignaturesCount(), report.getValidSignaturesCount());
    }

    @Test
    public void softCertSignatureShouldBeInvalidWithQESPolicy() throws Exception {
        QualifiedReport report = validateWithPolicy("POLv2", PDF_WITH_SOFT_CERT_SIGNATURE);
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    @Test
    public void validationReportShouldContainDefaultPolicyWhenPolicyIsNotExplicitlyGiven() throws Exception {
        Policy policy = validateWithPolicy("").getPolicy();
        assertEquals(NO_TYPE_POLICY.getName(), policy.getPolicyName());
        assertEquals(NO_TYPE_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(NO_TYPE_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainNoTypePolicyWhenNoTypePolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv1").getPolicy();
        assertEquals(NO_TYPE_POLICY.getName(), policy.getPolicyName());
        assertEquals(NO_TYPE_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(NO_TYPE_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void validationReportShouldContainQESPolicyWhenQESPolicyIsGivenToValidator() throws Exception {
        Policy policy = validateWithPolicy("POLv2").getPolicy();
        assertEquals(QES_POLICY.getName(), policy.getPolicyName());
        assertEquals(QES_POLICY.getDescription(), policy.getPolicyDescription());
        assertEquals(QES_POLICY.getUrl(), policy.getPolicyUrl());
    }

    @Test
    public void whenNonExistingPolicyIsGivenThenValidatorShouldThrowException() throws Exception {
        expectedException.expect(InvalidPolicyException.class);
        validateWithPolicy("non-existing-policy").getPolicy();
    }

    private QualifiedReport validateWithPolicy(String policyName) throws Exception {
        return validateWithPolicy(policyName, PDF_WITH_ONE_VALID_SIGNATURE);
    }

    private QualifiedReport validateWithPolicy(String policyName, String document) throws Exception {
        ValidationDocument validationDocument = buildValidationDocument(document);
        validationDocument.setSignaturePolicy(policyName);
        return validationService.validateDocument(validationDocument);
    }
}
