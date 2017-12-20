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

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.SimpleReport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class DocumentFormatIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * TestCaseID: DocumentFormat-1
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of pdf document acceptance
     *
     * Expected Result: Pdf is accepted and correct signature validation is given
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void PAdESDocumentShouldPass() {
        SimpleReport report = postForReport("hellopades-pades-lt-sha256-sign.pdf");
        assertAllSignaturesAreValid(report);
        assertEquals("PAdES_BASELINE_LT", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-2
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of bdoc document acceptance
     *
     * Expected Result: Bdoc is accepted and correct signature validation is given
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void BdocDocumentShouldPass() {
        SimpleReport report = postForReport("Valid_IDCard_MobID_signatures.bdoc");
        assertAllSignaturesAreValid(report);
        assertEquals("XAdES_BASELINE_LT_TM", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-3
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of asice document acceptance
     *
     * Expected Result: asice is accepted and correct signature validation is given
     *
     * File: Vbdoc21-TS.asice
     */
    @Test
    public void asiceDocumentShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        SimpleReport report = postForReport("bdoc21-TS.asice");
        assertAllSignaturesAreValid(report);
        assertEquals("XAdES_BASELINE_LT", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-4
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of asics document acceptance
     *
     * Expected Result: asics is accepted and correct signature validation is given
     *
     * File: ValidDDOCinsideAsics.asics
     */
    @Test
    public void asicsDocumentShouldPass() {
        setTestFilesDirectory("asics/");
        SimpleReport report = postForReport("ValidDDOCinsideAsics.asics");
        assertAllSignaturesAreValid(report);
        assertEquals("DIGIDOC_XML_1.3", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-5
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of xades acceptance
     *
     * Expected Result: xades is accepted and correct signature validation is given
     *
     * File: signatures0.xml
     */
    @Test
    public void xadesDocumentShouldPass() {
        SimpleReport report = postForReport("signatures0.xml");
        assertAllSignaturesAreInvalid(report);
        assertEquals("XAdES_BASELINE_LT", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-6
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Validation of cades acceptance
     *
     * Expected Result: cades is accepted and correct signature validation is given
     *
     * File:
     */
    @Ignore // Test file needed
    @Test
    public void cadesDocumentShouldPass() {
        SimpleReport report = postForReport("");
        assertAllSignaturesAreValid(report);
        assertEquals("CAdES_BASELINE_LT", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
