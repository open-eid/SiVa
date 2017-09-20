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

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.ValidationConclusion;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class DocumentFormatIT extends SiVaRestTests {

    private static final String TEST_FILES_DIRECTORY = "document_format_test_files/";

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * TestCaseID: DocumentFormat-1
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Validation of pdf document acceptance
     *
     * Expected Result: Pdf is accepted and correct signature validation is given
     *
     * File: hellopades-pades-lt-sha256-sign.pdf
     */
    @Test
    public void PAdESDocumentShouldPass() {
        QualifiedReport report = postForReport("hellopades-pades-lt-sha256-sign.pdf");
        assertAllSignaturesAreValid(report);
        assertEquals("PAdES-BASELINE-LT", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }

    /**
     * TestCaseID: DocumentFormat-2
     *
     * TestType: Automated
     *
     * Requirement:  http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Validation of bdoc document acceptance
     *
     * Expected Result: Bdoc is accepted and correct signature validation is given
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void AdESDocumentShouldPass() {
        QualifiedReport report = postForReport("Valid_IDCard_MobID_signatures.bdoc");
        assertAllSignaturesAreValid(report);
        assertEquals("XAdES_BASELINE_LT_TM", report.getValidationConclusion().getSignatures().get(0).getSignatureFormat());
    }


    @Override
    protected String getTestFilesDirectory() {
        return TEST_FILES_DIRECTORY;
    }
}
