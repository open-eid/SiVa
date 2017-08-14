package ee.openeid.siva.integrationtest;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Ignore
public class EuPlugValidationPassIT extends SiVaRestTests {

    @Value("${plugtest.location}")
    private String location;

    @Override
    protected String getTestFilesDirectory() {
        return location;
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuania adoc-v2.0 signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void lithuaniaAsiceAdoc20ValidSignature() {
        post(validationRequestForEu("Signature-A-LT_MIT-1.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(0));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation multiple of Lithuania adoc-v2.0 signatures
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-2.asice
     */
    @Test
    public void lithuaniaAsiceAdoc20TwoValidSignatures() {
        post(validationRequestForEu("Signature-A-LT_MIT-2.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("signatures[1].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[1].signatureFormat", Matchers.is("XAdES-BASELINE-B"))
                .body("signatures[1].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[1].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(2))
                .body("signaturesCount", Matchers.is(2))
                .body("validationWarnings", Matchers.hasSize(0));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Lithuania adoc-v2.0 signature with warning
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-5.asice"
     */
    @Test //TODO: need to check specifics about this warning, whether it is ok or not
    public void lithuaniaAsiceAdoc20ValidSignatureWithWarning() {
        post(validationRequestForEu("Signature-A-LT_MIT-5.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-B"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].warnings[0].description", Matchers.is("The 'issuer-serial' attribute is absent or does not match!"))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(0));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-4
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title: Validation of Latvian edoc-v2.0 signature
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LV_EUSO-1.asice
     */
    @Test
    public void latvianAsiceEdoc20ValidSignature() {
        post(validationRequestForEu("Signature-A-LV_EUSO-1.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].signatureFormat", Matchers.is("XAdES-BASELINE-T"))
                .body("signatures[0].signatureLevel", Matchers.is("QESIG"))
                .body("signatures[0].warnings", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1))
                .body("validationWarnings", Matchers.hasSize(0));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-5
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LV_EUSO-2.asice
     */
    @Test //TODO: this file is actually identical to the Signature-A-LV_EUSO-1.asice
    public void A_LV_EUSO_2Valid() {
        post(validationRequestForEu("Signature-A-LV_EUSO-2.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-6
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void A_PL_KIR_1Valid() {
        post(validationRequestForEu("Signature-A-PL_KIR-1.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-7
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void A_PL_KIR_2Valid() {
        post(validationRequestForEu("Signature-A-PL_KIR-2.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-8
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void A_SK_DIT_3Valid() {
        post(validationRequestForEu("Signature-A-SK_DIT-3.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-9
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void A_SK_DIT_7Valid() {
        post(validationRequestForEu("Signature-A-SK_DIT-7.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-10
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    @Ignore("No TLS")
    public void C_AT_SIT_1Valid() {
        post(validationRequestForEu("Signature-C-AT_SIT-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-11
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void C_DE_SCI_1Valid() {
        post(validationRequestForEu("Signature-C-DE_SCI-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-12
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    @Ignore
    public void C_ES_MIN_1Valid() {
        post(validationRequestForEu("Signature-C-ES_MIN-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-13
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    @Ignore
    public void C_ES_MIN_2Valid() {
        post(validationRequestForEu("Signature-C-ES_MIN-2.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-14
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void C_IT_BIT_5Valid() {
        post(validationRequestForEu("Signature-C-IT_BIT-5.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-15
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void C_PL_ADS_4Valid() {
        post(validationRequestForEu("Signature-C-PL_ADS-4.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-16
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void C_PL_ADS_7Valid() {
        post(validationRequestForEu("Signature-C-PL_ADS-7.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-17
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_BE_CONN_1Valid() {
        post(validationRequestForEu("Signature-P-BE_CONN-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-18
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_BE_CONN_7Valid() {
        post(validationRequestForEu("Signature-P-BE_CONN-7.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-19
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_DE_SCI_2Valid() {
        post(validationRequestForEu("Signature-P-DE_SCI-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-20
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_IT_MID_1Valid() {
        post(validationRequestForEu("Signature-P-IT_MID-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-21
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_LT_MIT_1Valid() {
        post(validationRequestForEu("Signature-P-LT_MIT-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-22
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_LT_MIT_2Valid() {
        post(validationRequestForEu("Signature-P-LT_MIT-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-23
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_LV_EUSO_1Valid() {
        post(validationRequestForEu("Signature-P-LV_EUSO-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-24
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_LV_EUSO_2Valid() {
        post(validationRequestForEu("Signature-P-LV_EUSO-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-25
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_PL_ADS_6Valid() {
        post(validationRequestForEu("Signature-P-PL_ADS-6.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-26
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void P_PL_ADS_8Valid() {
        post(validationRequestForEu("Signature-P-PL_ADS-8.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-27
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    @Ignore("No TLS")
    public void X_AT_SIT_1Valid() {
        post(validationRequestForEu("Signature-X-AT_SIT-1.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-28
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    @Ignore("No TLS")
    public void X_AT_SIT_21Valid() {
        post(validationRequestForEu("Signature-X-AT_SIT-21.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-29
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void X_BE_CONN_1Valid() {
        post(validationRequestForEu("Signature-X-BE_CONN-1.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    /**
     * TestCaseID: EuPlug-ValidationPass-30
     *
     * TestType: Automated
     *
     * Requirement:
     *
     * Title:
     *
     * Expected Result: The document should pass the validation
     *
     * File: Signature-A-LT_MIT-1.asice
     */
    @Test
    public void X_BE_CONN_21Valid() {
        post(validationRequestForEu("Signature-X-BE_CONN-21.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    private String validationRequestForEu(String file){
        return validationRequestForEuWithPolicy(file, VALID_SIGNATURE_POLICY_1);
    }

    private String validationRequestForEuWithPolicy(String file, String policy){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("document", Base64.encodeBase64String(Files.readAllBytes(Paths.get(getTestFilesDirectory() + file))));
            jsonObject.put("filename", file);
            jsonObject.put("signaturePolicy", policy);
        }catch (IOException e){
            throw new RuntimeException("Error on reading file", e);
        }
        return jsonObject.toString();
    }
}
