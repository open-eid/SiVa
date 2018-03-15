package ee.openeid.siva.integrationtest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.jayway.restassured.RestAssured;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;

@Category(IntegrationTest.class)
public class BdocDigestValidationPassIT extends AbstractDigestValidationTest {

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "bdoc/live/timemark/";

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before
    public void DirectoryBackToDefault() {
        this.testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
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
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
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
     * TestCaseID: Bdoc-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice One LT signature with certificates from different countries
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-30.asice
     */
    @Test
    public void bdocDifferentCertificateCountries() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-30.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Bdoc signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: 24050_short_ecdsa_correct_file_mimetype.bdoc
     */
    @Test
    public void bdocEccSha256signature() {
        this.assertAllSignaturesAreValid(this.postForReport("24050_short_ecdsa_correct_file_mimetype.bdoc"));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice Baseline-LT file
     *
     * Expected Result: The document should pass the validation in DD4J
     *
     * File: EE_SER-AEX-B-LT-V-49.asice
     */
    @Test
    public void bdocBaselineLtProfileValidSignature() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-49.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice QES file
     *
     * Expected Result: The document should pass the validation
     *
     * File: ValidLiveSignature.asice
     */
    @Test
    public void bdocQESProfileValidSignature() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("ValidLiveSignature.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice file signed with Mobile-ID, ECC-SHA256 signature with prime256v1 key
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-2.asice
     */
    @Test
    public void bdocWithEccSha256ValidSignature() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-2.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice file with 	ESTEID-SK 2015 certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice
     */
    @Test
    public void bdocSk2015CertificateChainValidSignature() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("IB-4270_TS_ESTEID-SK 2015  SK OCSP RESPONDER 2011.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Asice file with KLASS3-SK 2010 (EECCRCA) certificate chain
     *
     * Expected Result: The document should pass the validation
     *
     * File: EE_SER-AEX-B-LT-V-28.asice
     */
    @Test
    public void bdocKlass3Sk2010CertificateChainValidSignature() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("EE_SER-AEX-B-LT-V-28.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QES"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-12
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Bdoc with Baseline-LT_TM and QES signature level and ESTEID-SK 2011 certificate chain with valid signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC2.1.bdoc
     */
    @Test
    public void bdocEsteidSk2011CertificateChainQesBaselineLtTmValidSignature() {
        this.testFilesDirectory = "bdoc/live/timemark/";
        this.post(this.validationRequestFor("BDOC2.1.bdoc"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-13
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Bdoc TS with multiple valid signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Test_id_aa.asice
     */
    @Test
    public void bdocTsValidMultipleSignatures() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("Test_id_aa.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(2));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-14
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#siva-signature-validation-policy-version-2-polv4
     *
     * Title: Bdoc-TM with special characters in data file
     *
     * Expected Result: The document should pass the validation
     *
     * File: Šužlikud sõid ühe õuna ära.bdoc
     */
    @Test
    public void bdocWithSpecialCharactersInDataFileShouldPass() {
        this.testFilesDirectory = "bdoc/live/timemark/";
        this.post(this.validationRequestFor("Šužlikud sõid ühe õuna ära.bdoc"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
            .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-15
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: *.sce file with TimeMark
     *
     * Expected Result: The document should pass the validation
     *
     * File: BDOC2.1_content_as_sce.sce
     */
    @Test
    public void bdocWithSceFileExtensionShouldPass() {
        this.testFilesDirectory = "bdoc/live/timemark/";
        this.post(this.validationRequestFor("BDOC2.1_content_as_sce.sce"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
            .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-17
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: Bdoc-TS with special characters in data file
     *
     * Expected Result: The document should pass the validation with correct signature scope
     *
     * File: Nonconventionalcharacters.asice
     */
    @Test
    public void asiceWithSpecialCharactersInDataFileShouldPass() {
        this.testFilesDirectory = "bdoc/live/timestamp/";
        this.post(this.validationRequestFor("Nonconventionalcharacters.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].name", Matchers.is("!~#¤%%&()=+-_.txt"))
            .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].scope", Matchers.is("FullSignatureScope"))
            .body("validationReport.validationConclusion.signatures[0].signatureScopes[0].content", Matchers.is("Full document"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
            .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-19
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: ECC signature vith BDOC TM
     *
     * Expected Result: The document should pass the validation
     *
     * File: testECCDemo.bdoc
     */
    @Test
    public void bdocWithEccTimeMarkShouldPass() {
        this.testFilesDirectory = "bdoc/test/timemark/";
        this.post(this.validationRequestFor("testECCDemo.bdoc"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT_TM"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
            .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Bdoc-ValidationPass-20
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva2/appendix/validation_policy/#common-validation-constraints-polv3-polv4
     *
     * Title: ECC signature vith BDOC TS
     *
     * Expected Result: The document should pass the validation
     *
     * File: Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice
     */
    @Test
    public void bdocWithEccTimeStampShouldPass() {
        this.testFilesDirectory = "bdoc/test/timestamp/";
        this.post(this.validationRequestFor("Mac_AS0099904_EsimeneAmetlikSKTestElliptilistega_TS.asice"))
            .then()
            //.body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-E"))
            .body("validationReport.validationConclusion.signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
            .body("validationReport.validationConclusion.signatures[0].signatureLevel", Matchers.is("QESIG"))
            .body("validationReport.validationConclusion.signatures[0].indication", Matchers.is("TOTAL-PASSED"))
            .body("validationReport.validationConclusion.validSignaturesCount", Matchers.is(1))
            .body("validationReport.validationConclusion.signaturesCount", Matchers.is(1));
    }

}
