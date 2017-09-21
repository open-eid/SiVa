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

package ee.openeid.siva.manualtest;

import ee.openeid.siva.integrationtest.SiVaRestTests;
import ee.openeid.siva.validation.document.report.SimpleReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class StatisticsToGAManualIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    /*
    * Note: All tests in this class expect manual configuration of Google Analytics before executing the tests.
    * Note: The test are made to prepare test data and ease the test execution, all results must be checked manually in GA!
    * Note: Check all the fields presence and correctness even though tests prepare data for specific cases.
    */

     /**
     * TestCaseID: Xauth-Statistics-GA-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Pdf valid container is validated with x-authenticated-user set in header
     *
     * Expected Result: x-authenticated-user value is shown in GA
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void xAuthUsrWithValidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", ""), "XAuthTest")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * TestCaseID: Xauth-Statistics-GA-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Pdf valid container is validated with x-authenticated-user set in header
     *
     * Expected Result: x-authenticated-user value is shown as N/A in GA
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void xAuthUsrEmptyWithValidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", ""), "")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * TestCaseID: Xauth-Statistics-GA-3
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Pdf valid container is validated with x-authenticated-user set in header
     *
     * Expected Result: x-authenticated-user value is shown as N/A in GA
     *
     * File: pades_lt_two_valid_sig.pdf
     */
    @Test
    public void xAuthMissingWithValidSignatures() {
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("pades_lt_two_valid_sig.pdf"));
        post(validationRequestWithValidKeys(encodedString, "pades_lt_two_valid_sig.pdf", ""))
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * TestCaseID: Bdoc-Statistics-GA-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Bdoc with certificates from different countries.
     *
     * Expected Result: LT, EE and LV country codes are present in GA, Signature container type is ASIC-E TM
     *
     * File: Baltic MoU digital signing_EST_LT_LV.bdoc
     */
    @Test
    public void bdocWithSignaturesFromDifferentCountries() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Baltic MoU digital signing_EST_LT_LV.bdoc"));
        SimpleReport report = mapToReport(
                postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "Baltic MoU digital signing_EST_LT_LV.bdoc", ""), "CountriesTest")
                        .body()
                        .asString()
        ).getValidationReport();
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Bdoc-Statistics-GA-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Asice-E TS is validated
     *
     * Expected Result: GA shows Signature container type as ASIC-E TS
     *
     * File: BDOC-TS.bdoc
     */
    @Test
    public void bdocTSSignatures() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("BDOC-TS.bdoc"));
        SimpleReport report = mapToReport(
                postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "BDOC-TS.bdoc", ""), "AsicETSTest")
                        .body()
                        .asString()
        ).getValidationReport();
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Xroad-Statistics-GA-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Xroad valid container is validated
     *
     * Expected Result: Country code XX is sent to GA (GA shows ZZ as the XX is unknown code), Signature container type is ASiC-E (BatchSignature)
     *
     * File: xroad-batchsignature.asice
     */
    @Test
    public void xroadWithValidSignatures() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-batchsignature.asice"));
        SimpleReport report = mapToReport(
                postWithXAuthUsrHeader(validationRequestWithDocumentTypeValidKeys(encodedString, "xroad-batchsignature.asice", "xroad", ""), "XXCountryCodeTest")
                        .body()
                        .asString()
        ).getValidationReport();
        assertAllSignaturesAreValid(report);
    }

    /**
     * TestCaseID: Ddoc-Statistics-GA-1
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Ddoc not supported file is inserted
     *
     * Expected Result: Not shown in GA statistics as the container is not validated
     *
     * File: xroad-simple.asice
     */
    @Test
    public void ddocWithErrorResponse() {
        setTestFilesDirectory("xroad/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "xroad-simple.ddoc", ""), "IsNotShownTest")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("requestErrors[0].key", Matchers.is(DOCUMENT))
                .body("requestErrors[0].message", Matchers.containsString(DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE));
    }

    /**
     * TestCaseID: Ddoc-Statistics-Log-2
     *
     * TestType: Manual
     *
     * Requirement: http://open-eid.github.io/SiVa/pdf-files/SiVa_statistics.pdf
     *
     * Title: Ddoc invalid container is validated
     *
     * Expected Result: GA shows Signature container type as XAdES, TOTAL-FAILED indication
     *
     * File: ilma_kehtivuskinnituseta.ddoc
     */
    @Test
    public void ddocWithInvalidSignatures() {
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ilma_kehtivuskinnituseta.ddoc"));
        SimpleReport report = mapToReport(
                postWithXAuthUsrHeader(validationRequestWithValidKeys(encodedString, "ilma_kehtivuskinnituseta.ddoc", ""), "XAdESTest")
                        .body()
                        .asString()
        ).getValidationReport();
        assertAllSignaturesAreInvalid(report);
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}

