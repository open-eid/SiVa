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
public class EuPlugIT extends SiVaRestTests {

    @Value("${plugtest.location}")
    private String location;

    @Override
    protected String getTestFilesDirectory() {
        return location;
    }

    @Test
    public void A_LT_MIT_1Valid() throws IOException {
        post(validationRequestForEu("Signature-A-LT_MIT-1.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_LT_MIT_2Valid() throws IOException {
        post(validationRequestForEu("Signature-A-LT_MIT-2.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_LT_MIT_5Valid() throws IOException {
        post(validationRequestForEu("Signature-A-LT_MIT-5.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_LV_EUSO_1Valid() throws IOException {
        post(validationRequestForEu("Signature-A-LV_EUSO-1.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_LV_EUSO_2Valid() throws IOException {
        post(validationRequestForEu("Signature-A-LV_EUSO-2.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_PL_KIR_1Valid() throws IOException {
        post(validationRequestForEu("Signature-A-PL_KIR-1.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_PL_KIR_2Valid() throws IOException {
        post(validationRequestForEu("Signature-A-PL_KIR-2.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_SK_DIT_3Valid() throws IOException {
        post(validationRequestForEu("Signature-A-SK_DIT-3.asice"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void A_SK_DIT_7Valid() throws IOException {
        post(validationRequestForEu("Signature-A-SK_DIT-7.asics"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    @Ignore("No TLS")
    public void C_AT_SIT_1Valid() throws IOException {
        post(validationRequestForEu("Signature-C-AT_SIT-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test

    public void C_DE_SCI_1Valid() throws IOException {
        post(validationRequestForEu("Signature-C-DE_SCI-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    @Ignore
    public void C_ES_MIN_1Valid() throws IOException {
        post(validationRequestForEu("Signature-C-ES_MIN-1.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    @Ignore
    public void C_ES_MIN_2Valid() throws IOException {
        post(validationRequestForEu("Signature-C-ES_MIN-2.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void C_IT_BIT_5Valid() throws IOException {
        post(validationRequestForEu("Signature-C-IT_BIT-5.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void C_PL_ADS_4Valid() throws IOException {
        post(validationRequestForEu("Signature-C-PL_ADS-4.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void C_PL_ADS_7Valid() throws IOException {
        post(validationRequestForEu("Signature-C-PL_ADS-7.p7m"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_BE_CONN_1Valid() throws IOException {
        post(validationRequestForEu("Signature-P-BE_CONN-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_BE_CONN_7Valid() throws IOException {
        post(validationRequestForEu("Signature-P-BE_CONN-7.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_DE_SCI_2Valid() throws IOException {
        post(validationRequestForEu("Signature-P-DE_SCI-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_IT_MID_1Valid() throws IOException {
        post(validationRequestForEu("Signature-P-IT_MID-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_LT_MIT_1Valid() throws IOException {
        post(validationRequestForEu("Signature-P-LT_MIT-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_LT_MIT_2Valid() throws IOException {
        post(validationRequestForEu("Signature-P-LT_MIT-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_LV_EUSO_1Valid() throws IOException {
        post(validationRequestForEu("Signature-P-LV_EUSO-1.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_LV_EUSO_2Valid() throws IOException {
        post(validationRequestForEu("Signature-P-LV_EUSO-2.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_PL_ADS_6Valid() throws IOException {
        post(validationRequestForEu("Signature-P-PL_ADS-6.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void P_PL_ADS_8Valid() throws IOException {
        post(validationRequestForEu("Signature-P-PL_ADS-8.pdf"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    @Ignore("No TLS")
    public void X_AT_SIT_1Valid() throws IOException {
        post(validationRequestForEu("Signature-X-AT_SIT-1.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    @Ignore("No TLS")
    public void X_AT_SIT_21Valid() throws IOException {
        post(validationRequestForEu("Signature-X-AT_SIT-21.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void X_BE_CONN_1Valid() throws IOException {
        post(validationRequestForEu("Signature-X-BE_CONN-1.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    @Test
    public void X_BE_CONN_21Valid() throws IOException {
        post(validationRequestForEu("Signature-X-BE_CONN-21.xml"))
                .then()
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"));
    }

    private String validationRequestForEu(String file) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(Files.readAllBytes(Paths.get(getTestFilesDirectory() + file))));
        jsonObject.put("filename", file);
        jsonObject.put("signaturePolicy", VALID_SIGNATURE_POLICY_1);
        
        return jsonObject.toString();
    }

}
