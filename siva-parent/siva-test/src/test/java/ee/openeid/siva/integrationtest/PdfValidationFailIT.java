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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationFailIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: PDF-ValidationFail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with expired certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is expired should fail.
     *
     * File: hellopades-lt-rsa1024-sha1-expired.pdf
     */
    @Test
    public void signaturesMadeWithExpiredSigningCertificatesAreInvalid() {
        post(validationRequestFor("hellopades-lt-rsa1024-sha1-expired.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QES"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("TRY_LATER"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("No revocation data for the certificate"))
                .body("validationReport.validationConclusion.signatures[0].warnings.content[0]", Matchers.is("The signature/seal is an INDETERMINATE AdES!"))
                .body("validationReport.validationConclusion.signatures[0].warnings.content[1]", Matchers.is("The certificate is not for eSig at signing time!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with revoked certificate (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that is revoked should fail.
     *
     * File: pades_lt_revoked.pdf
     */
    @Test
    public void documentSignedWithRevokedCertificateShouldFail() {
        post(validationRequestFor( "pades_lt_revoked.pdf", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("INDETERMINATE_QESIG"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("NURM,AARE,38211015222"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.signatures[0].claimedSigningTime", Matchers.is("2016-06-29T08:38:31Z"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The signature/seal is an INDETERMINATE AdES!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: The PDF-file has been signed with certificate which has no non repudiation key usage attribute (PAdES Baseline LT)
     *
     * Expected Result: The PDF-file validation should fail with error.
     *
     * File: hellopades-pades-lt-sha256-auth.pdf
     */
    @Test
    public void signingCertificateWithoutNonRepudiationKeyUsageAttributeShouldFail() {
        post(validationRequestFor( "hellopades-pades-lt-sha256-auth.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("NOT_ADES_QC_QSCD"))
                .body("validationReport.validationConclusion.signatures[0].signedBy", Matchers.is("SINIVEE,VEIKO,36706020210"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("SIG_CONSTRAINTS_FAILURE"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The signer's certificate has not expected key-usage!"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     * period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa2048-expired.pdf
     */
    @Ignore //TODO: Needs new test file
    @Test
    public void documentSignedWithExpiredRsa2048CertificateShouldFail() {
        post(validationRequestFor( "hellopades-lt-sha256-rsa2048-expired.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("INDETERMINATE"))
                .body("validationReport.validationConclusion.signatures[0].subIndication", Matchers.is("NO_POE"))
                .body("validationReport.validationConclusion.signatures[0].errors.content", Matchers.hasItem("The past signature validation is not conclusive!"))
                .body("validationReport.validationConclusion.signatures[0].warnings", Matchers.hasSize(0))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: hellopadess been signed with an expired certificate, where signing time is within the original validity
     * period of the certificate, but OCSP confirmation and Time Stamp are current date (PAdES Baseline LT).
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa1024-expired2.pdf
     */
    @Test
    @Ignore //Testfile needed
    public void documentSignedWithExpiredSha256CertificateShouldFail() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1024-expired2.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: OCSP is taken more than 24h after TS
     *
     * Expected Result: Document signed with expired certificate should fail
     *
     * File: hellopades-lt-sha256-rsa1024-expired2.pdf
     */
    @Test
    public void ocspTaken24hAfterTsShouldFail() {
        setTestFilesDirectory("pdf/signature_revocation_value_test_files/");
        post(validationRequestFor("hellopades-lt-sha256-ocsp-28h.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationFail-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: CRL is out of thisUpdate and nextUpdate range
     *
     * Expected Result: Validation should fail
     *
     * File: pades-lt-CRL-taken-days-later.pdf
     */
    @Test
    public void crlTaken24hAfterTsShouldFail() {
        setTestFilesDirectory("pdf/signature_revocation_value_test_files/");
        post(validationRequestFor("pades-lt-CRL-taken-days-later.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.isEmptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].errors[0].content", Matchers.is("The revocation information is not considered as 'fresh'."))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(0))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }
}
