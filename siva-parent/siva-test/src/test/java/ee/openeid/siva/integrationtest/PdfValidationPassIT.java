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
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationPassIT extends SiVaRestTests {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";
    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    /**
     * TestCaseID: PDF-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa1024-not-expired.pdf
     */
    @Test
    public void validSignaturesRemainValidAfterSigningCertificateExpires() {
        post(validationRequestFor("hellopades-lt-sha256-rsa1024-not-expired.pdf", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: PDF-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
     *
     * Expected Result: Document signed with certificate that expired after signing should pass.
     *
     * File: hellopades-lt-sha256-rsa2048-7d.pdf
     */
    @Test
    public void certificateExpired7DaysAfterDocumentSigningShouldPass() {
        post(validationRequestFor("hellopades-lt-sha256-rsa2048-7d.pdf", VALID_SIGNATURE_POLICY_3, null))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));

    }

    /**
     * TestCaseID: PDF-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: Pdf with single valid signature
     *
     * Expected Result: Document should pass.
     *
     * File: PdfValidSingleSignature.pdf
     */
    @Test
    public void validSignature() {
        setTestFilesDirectory("document_format_test_files/");
        post(validationRequestFor("PdfValidSingleSignature.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.notNullValue())
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.notNullValue())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: The PDF-file has OCSP more than 15 minutes after TS but earlier than 24h
     *
     * Expected Result: Warning should be returned but validation should pass
     *
     * File: hellopades-lt-sha256-ocsp-15min1s.pdf
     */
    @Test
    public void ocsp15MinutesAfterTsShouldPassWithWarning() {
        setTestFilesDirectory("pdf/signature_revocation_value_test_files/");
        post(validationRequestFor("hellopades-lt-sha256-ocsp-15min1s.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.signatures[0].warnings[0].content", Matchers.is("The trusted certificate doesn't match the trust service"))
                .body("validationReport.validationConclusion.signatures[0].warnings[1].content", Matchers.is("The revocation information is not considered as 'fresh'."))
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.serialNumber", Matchers.notNullValue())
                .body("validationReport.validationConclusion.signatures[0].subjectDistinguishedName.commonName", Matchers.notNullValue())
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
                .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: PDF-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4
     *
     * Title: The PDF-file has OCSP almost 24h before TS
     *
     * Expected Result: Warning should be returned but validation should pass
     *
     * File: hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf
     */
    @Test
    public void ocspAlmost24hBeforeTsShouldPassWithWarning() {
        setTestFilesDirectory("pdf/signature_revocation_value_test_files/");
        post(validationRequestFor("hellopades-lt-sha256-rsa2048-ocsp-before-ts.pdf"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.emptyOrNullString())
                .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("PAdES_BASELINE_LT"))
                .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
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
