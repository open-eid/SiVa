package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class BdocValidationPassIT extends SiVaRestTests{
    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    /**
     * TestCaseID: Bdoc-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc with single valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void validSignature() {
        assertAllSignaturesAreValid(postForReport("Valid_ID_sig.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc TM with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     */
    @Test
    public void validMultipleSignatures() {
        assertAllSignaturesAreValid(postForReport("Valid_IDCard_MobID_signatures.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc with warning on signature
     *
     * Expected Result: The document should pass the validation but warning should be returned
     *
     * File: bdoc_weak_warning_sha1.bdoc
     */
    @Test @Ignore //TODO: DSS-760
    public void validSignatureWithWarning() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("bdoc_weak_warning_sha1.bdoc"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice One LT signature with certificates from different countries
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-30.asice
     */
    @Test
    public void bdocDifferentCertificateCountries() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-30.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: 24050_short_ecdsa_correct_file_mimetype.bdoc
     */
    @Test
    public void bdocEccSha256signature() {
        assertAllSignaturesAreValid(postForReport("24050_short_ecdsa_correct_file_mimetype.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice Baseline-LT file
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-49.asice
     */
    @Test
    public void bdocBaselineLtProfileValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-49.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice QES file
     *
     * Expected Result: The document should pass the validation
     *
     * File: bdoc21-TS.asice
     */
    @Test @Ignore //TODO: DSS-915
    public void bdocQESProfileValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("bdoc21-TS.asice"))
                .then()
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice Baseline-LTA file
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LTA-V-24.asice
     */
    @Test
    public void bdocBaselineLtaProfileValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LTA-V-24.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-2.asice
     */
    @Test
    public void bdocWithEccSha256ValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-2.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice file with 	ESTEID-SK 2015 certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice
     */
    @Test
    public void bdocSk2015CertificateChainValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-28.asice
     */
    @Test
    public void bdocKlass3Sk2010CertificateChainValidSignature() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        post(validationRequestFor("EE_SER-AEX-B-LT-V-28.asice"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].signatureLevel", Matchers.is("AdESqc"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc with Baseline-LT_TM and QES signature level and ESTEID-SK 2011 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC2.1.bdoc
     */
    @Test
    public void bdocEsteidSk2011CertificateChainQesBaselineLtTmValidSignature() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("BDOC2.1.bdoc"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc TS with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC-TS.bdoc
     */
    @Test
    public void bdocTsValidMultipleSignatures() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        assertAllSignaturesAreValid(postForReport("BDOC-TS.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv2
     *
     * Title: Bdoc-TM with special characters in data file
     *
     * Expected Result: The document should pass the validation
     *
     * File: Šužlikud sõid ühe õuna ära.bdoc
     */
    @Test
    public void bdocWithSpecialCharactersInDataFileShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        post(validationRequestFor("Šužlikud sõid ühe õuna ära.bdoc"))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: *.sce file with TimeMark
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC2.1_content_as_sce.sce
     */
    @Test
    public void bdocWithSceFileExtensionShouldPass() {
        setTestFilesDirectory("bdoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("BDOC2.1_content_as_sce.sce"));
        post(validationRequestWithValidKeys(encodedString, "BDOC2.1_content_as_sce.sce", "BDOC", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-16
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: *.sce file with TimeStamp
     *
     * Expected Result: The document should pass the validation
     *
     * File: ASICE_TS_LTA_content_as_sce.sce
     */
    @Test
    public void asiceWithSceFileExtensionShouldPass() {
        setTestFilesDirectory("bdoc/live/timestamp/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ASICE_TS_LTA_content_as_sce.sce"));
        post(validationRequestWithValidKeys(encodedString, "ASICE_TS_LTA_content_as_sce.sce", "BDOC", ""))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LTA"))
                .body("signatures[0].signatureLevel", Matchers.is("QES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
