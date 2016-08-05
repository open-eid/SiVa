package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationFail extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    /***
     * TestCaseID: PDF-ValidationFail-1
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that is expired before signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired before signing should fail.
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     ***/
    @Test
    public void certificateExpiredBeforeDocumentSigningShouldFail() {
        QualifiedReport report = postForReport("hellopades-lt-rsa1024-sha1-expired.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "No revocation data for the certificate");
    }

    /***
     * TestCaseID: PDF-ValidationFail-2
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with expired certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is expired should fail.
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     ***/
    @Test
    public void signaturesMadeWithExpiredSigningCertificatesAreInvalid() {
        QualifiedReport report = postForReport("hellopades-lt-rsa1024-sha1-expired.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "No revocation data for the certificate");
    }

    /***
     * TestCaseID: PDF-ValidationFail-3
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is revoked should fail.
     *
     * File: pades_lt_revoked.pdf
     ***/
    @Ignore("Error: The trusted service of the revocation has not expected type identifier!")
    @Test
    public void documentSignedWithRevokedCertificateShouldFail() {
        QualifiedReport report = postForReport("pades_lt_revoked.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "The certificate is revoked!");
    }

    /***
     * TestCaseID: PDF-ValidationFail-4
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate that missing signed attribute (PAdES Baseline LT)
     *
     * Expected Result: PDF-file validation should fail
     *
     * File: missing_signing_certificate_attribute.pdf
     ***/
    @Ignore("Error: The expected format is not found!")
    @Test
    public void missingSignedAttributeForSigningCertificate() {
        QualifiedReport report = postForReport("missing_signing_certificate_attribute.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "The signed attribute: 'signing-certificate' is absent!");
    }

    /***
     * TestCaseID: PDF-ValidationFail-5
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
     *
     * Expected Result: The PDF-file validation should fail with error.
     *
     * File: hellopades-pades-lt-sha256-auth.pdf
     ***/
    @Test
    public void signingCertificateWithoutNonRepudiationKeyUsageAttributeShouldFail() {
        QualifiedReport report = postForReport("hellopades-pades-lt-sha256-auth.pdf");
        assertInvalidWithError(report.getSignatures().get(0), "The signer's certificate has not expected key-usage!");
    }

    /***
     * TestCaseID: PDF-ValidationFail-6
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     *                  period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa2048-expired.pdf
     ***/
    @Test
    public void documentSignedWithExpiredRsa2048CertificateShouldFail() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lt-sha256-rsa2048-expired.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationFail-7
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     *                  period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa1024-expired2.pdf
     ***/
    @Test
    public void documentSignedWithExpiredRsa1024CertificateShouldFail() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lt-sha256-rsa1024-expired2.pdf"));
    }

    /***
     * TestCaseID: PDF-ValidationFail-8
     *
     * TestType: Automated
     *
     * RequirementID:
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     *                  period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha1-rsa1024-expired2.pdf
     ***/
    @Test
    public void documentSignedWithExpiredSha1CertificateShouldFail() {
        assertAllSignaturesAreInvalid(postForReport("hellopades-lt-sha1-rsa1024-expired2.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
