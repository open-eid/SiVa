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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfValidationPassIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "pdf/signing_certifacte_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: PDF-ValidationPass-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     * <p>
     * Title: The PDF-file has been signed with certificate that is expired after signing (PAdES Baseline LT)
     * <p>
     * Expected Result: Document signed with certificate that expired after signing should pass.
     * <p>
     * File: hellopades-lt-sha256-rsa1024-not-expired.pdf
     */
    @Test
    @Ignore("Unknown reason")
    public void validSignaturesRemainValidAfterSigningCertificateExpires() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa1024-not-expired.pdf"));
    }

    /**
     * TestCaseID: PDF-ValidationPass-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     * <p>
     * Title: The PDF-file has been signed with certificate that will expire in 7 days after signing (PAdES Baseline LT)
     * <p>
     * Expected Result: Document signed with certificate that expired after signing should pass.
     * <p>
     * File: hellopades-lt-sha256-rsa2048-7d.pdf
     */
    @Test
    @Ignore("Unknown reason")
    public void certificateExpired7DaysAfterDocumentSigningShouldPass() {
        assertAllSignaturesAreValid(postForReport("hellopades-lt-sha256-rsa2048-7d.pdf"));
    }

    /**
     * TestCaseID: PDF-ValidationPass-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     * <p>
     * Title: Pdf with single valid signature
     * <p>
     * Expected Result: Document should pass.
     * <p>
     * File: PdfValidSingleSignature.pdf
     */
    @Test
    public void validSignature() {
        setTestFilesDirectory("document_format_test_files/");
        assertAllSignaturesAreValid(postForReport("PdfValidSingleSignature.pdf"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
