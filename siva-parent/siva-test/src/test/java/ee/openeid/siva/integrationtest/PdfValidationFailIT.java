package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationFailIT extends SiVaRestTests{

    private static final String TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    /***
     * TestCaseID: PDF-ValidationFail-1
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
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-rsa1024-sha1-expired.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-rsa1024-sha1-expired.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("OUT_OF_BOUNDS_NO_POE"))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: PDF-ValidationFail-2
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
    @Test
    public void documentSignedWithRevokedCertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_revoked.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades_lt_revoked.pdf", "pdf", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("REVOKED_NO_POE"))
                .body("signatures[0].errors[0].content", Matchers.is("The revocation time is not posterior to best-signature-time!"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2016-06-29T08:38:31Z"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: PDF-ValidationFail-3
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
    @Test
            // Since DSS 4.7.1.RC1 the given file is identified as PAdES_BASELINE_T
            // When PAdES_BASELINE_T is not in constraint.xml's AcceptableFormats -> Error: The expected format is not found!
            // EU signature policy is used.
    public void missingSignedAttributeForSigningCertificate() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("missing_signing_certificate_attribute.pdf"));
        post(validationRequestWithValidKeys(encodedString, "missing_signing_certificate_attribute.pdf", "pdf", VALID_SIGNATURE_POLICY_2))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_T"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].signedBy", Matchers.is("ALAS,RISTO,38109300259"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_SIGNING_CERTIFICATE_FOUND"))
                .body("signatures[0].errors[0].content", Matchers.is("The signed attribute: 'signing-certificate' is absent!"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2015-05-20T12:51:32Z"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /***
     * TestCaseID: PDF-ValidationFail-4
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
     * TestCaseID: PDF-ValidationFail-5
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
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa2048-expired.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa2048-expired.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("OUT_OF_BOUNDS_NO_POE"))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
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
     * File: hellopades-lt-sha256-rsa1024-expired2.pdf
     ***/
    @Test
    public void documentSignedWithExpiredSha256CertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa1024-expired2.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024-expired2.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("OUT_OF_BOUNDS_NO_POE"))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
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
     * File: hellopades-lt-sha1-rsa1024-expired2.pdf
     ***/
    @Test
    public void documentSignedWithExpiredSha1CertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha1-rsa1024-expired2.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha1-rsa1024-expired2.pdf", "pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("OUT_OF_BOUNDS_NO_POE"))
                .body("signatures[0].errors", Matchers.hasSize(0))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
