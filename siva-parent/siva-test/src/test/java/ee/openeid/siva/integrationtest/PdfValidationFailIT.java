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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.SimpleReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationFailIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    /**
     * TestCaseID: PDF-ValidationFail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with expired certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is expired should fail.
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     */
    @Test
    @Ignore("Unknown reason")
    public void signaturesMadeWithExpiredSigningCertificatesAreInvalid() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-rsa1024-sha1-expired.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-rsa1024-sha1-expired.pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("signatures[0].errors.content", Matchers.hasItem("The certificate validation is not concluant!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is revoked should fail.
     *
     * File: pades_lt_revoked.pdf
     */
    @Test
    @Ignore("Unknown reason")
    public void documentSignedWithRevokedCertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_revoked.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades_lt_revoked.pdf", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QESIG"))
                .body("signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("signatures[0].errors[0].content", Matchers.is("The past signature validation is not conclusive!"))
                .body("signatures[0].claimedSigningTime", Matchers.is("2016-06-29T08:38:31Z"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
     *
     * Expected Result: The PDF-file validation should fail with error.
     *
     * File: hellopades-pades-lt-sha256-auth.pdf
     */
    @Test
    public void signingCertificateWithoutNonRepudiationKeyUsageAttributeShouldFail() {
        SimpleReport report = postForReport("hellopades-pades-lt-sha256-auth.pdf");
        assertInvalidWithError(report.getValidationConclusion().getSignatures().get(0), "The signer's certificate has not expected key-usage!");
    }

    /**
     * TestCaseID: PDF-ValidationFail-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     * period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa2048-expired.pdf
     */
    @Test
    @Ignore //TODO https://jira.nortal.com/browse/SIVARIA-17
    public void documentSignedWithExpiredRsa2048CertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa2048-expired.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa2048-expired.pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdES"))
                .body("signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("signatures[0].errors.content", Matchers.hasItem("The past signature validation is not conclusive!"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     * period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa1024-expired2.pdf
     */
    @Test
    @Ignore //TODO https://jira.nortal.com/browse/SIVARIA-17
    public void documentSignedWithExpiredSha256CertificateShouldFail() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("hellopades-lt-sha256-rsa1024-expired2.pdf"));
        post(validationRequestWithValidKeys(encodedString, "hellopades-lt-sha256-rsa1024-expired2.pdf", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
