package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignatureRevocationValueIt extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "pdf/signature_revocation_value_test_files/";

    /**
     * TestCaseID: PDF-SigRevocVal-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation that is more than 15 minutes later than the signatures Time Stamp.
     *
     * Expected Result: Document with ocsp over 15 min delay should pass but warn
     *
     * File: hellopades-lt-sha256-ocsp-15min1s.pdf
     */
    @Test
    public void documentWithOcspOver15MinDelayShouldPassWithoutErrors() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-ocsp-15min1s.pdf"));
    }

    /**
     * TestCaseID: PDF-SigRevocVal-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation that is more than 15 minutes later than the signatures Time Stamp.
     *
     * Expected Result: Document with ocsp over 15 min delay should pass but warn
     *
     * File: hellopades-lt-sha256-ocsp-15min1s.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this warn functionality is no longer implemented
    public void documentWithOcspOver15MinDelayShouldHaveCorrectWarningInReport() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-ocsp-15min1s.pdf");
        assertHasWarning(report.getSignatures().get(0), "The validation failed, because OCSP is too long after the best-signature-time!");
    }

    /**
     * TestCaseID: PDF-SigRevocVal-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has PAdES-LT profile signature and an OCSP confirmation more than 24 hours later than the signatures Time Stamp.
     *
     * Expected Result: Document with over 24h delay should fail
     *
     * File: hellopades-lt-sha256-ocsp-28h.pdf
     */
    @Test @Ignore //TODO: With DigiDoc4J 1.0.4 update this error functionality is no longer implemented
    public void documentWithOcspOver24hDelayShouldFail() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lt-sha256-ocsp-28h.pdf"));
    }

    /**
     * TestCaseID: PDF-SigRevocVal-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with PAdES Baseline LTA profile signature, the signature contains CRL.
     *
     * Expected Result: Document with no ocsp or crl in signature should fail
     *
     * File: hellopades-lta-no-ocsp.pdf
     */
    @Test @Ignore //TODO: VAL-98 File size limit exceeded with this file
    public void documentWithNoOcspNorCrlInSignatureShouldFail() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lta-no-ocsp.pdf"));
    }

    /**
     * TestCaseID: PDF-SigRevocVal-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: PDF signature has OCSP confirmation before Time Stamp
     *
     * Expected Result: Document signed with ocsp time value before best signature time should fail
     *
     * File: hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf
     */
    @Test @Ignore //TODO: new test file is needed; the current one has issues with QC / SSCD
    public void documentSignedWithOcspTimeValueBeforeBestSignatureTimeShouldFail() {
        QualifiedReport report = postForReport("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "The validation failed, because OCSP is before the best-signature-time!");
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
