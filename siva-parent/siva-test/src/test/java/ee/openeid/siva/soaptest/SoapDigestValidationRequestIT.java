package ee.openeid.siva.soaptest;

import java.util.Base64;

import org.digidoc4j.Configuration;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerOpener;
import org.digidoc4j.DigestAlgorithm;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.request.DetachedContent;
import ee.openeid.siva.webapp.request.SignatureContent;
import ee.openeid.siva.webapp.request.ValidateHashCodePayload;

/**
 * Created by Andrei on 14.03.2018.
 */
public class SoapDigestValidationRequestIT extends SiVaSoapTests {

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testHashcodeValidationASiCE() throws Exception {
        Response response = this.post(this.writeValueAsString(this.buildPayload("test.asice")));
        response.then()
            .statusCode(HttpStatus.OK.value())
            .body("Envelope.Body.ValidateDocumentResponse.ValidationReport.ValidationConclusion.ValidSignaturesCount", Matchers.is("1"));
    }

    @Test
    public void testHashcodeValidationBDOCFails() throws Exception {
        Response response = this.post(this.writeValueAsString(this.buildPayload("BdocMultipleSignaturesMixedWithValidAndInvalid.bdoc")));
        response.then()
            .statusCode(HttpStatus.OK.value())
            .body("Envelope.Body.ValidateDocumentResponse.ValidationReport.ValidationConclusion.ValidSignaturesCount", Matchers.is("1"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return "digest_validation_test_files";
    }

    private ValidateHashCodePayload buildPayload(String containerFileName) {
        Container container = ContainerOpener.open(String.format("src/test/resources/%s/%s", this.getTestFilesDirectory(),
            containerFileName), Configuration.of(Configuration.Mode.TEST));
        ValidateHashCodePayload payload = new ValidateHashCodePayload();
        payload.setContainerFileName(containerFileName);
        payload.setReportType(ReportType.SIMPLE.name());
        payload.setSignaturePolicy(VALID_SIGNATURE_POLICY_3);
        container.getDataFiles().forEach(f -> {
            try {
                DetachedContent content = new DetachedContent();
                content.setFileName(f.getName());
                content.setBase64Digest(Base64.getEncoder().encodeToString(f.calculateDigest(DigestAlgorithm.SHA256)));
                content.setDigestAlgorithm("SHA256");
                payload.getDetachedContents().add(content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        container.getSignatures().forEach(s -> {
            try {
                SignatureContent content = new SignatureContent();
                content.setFileName(s.getId());
                content.setBase64Value(Base64.getEncoder().encodeToString(s.getAdESSignature()));
                payload.getSignatureContents().add(content);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return payload;
    }

    private String writeValueAsString(ValidateHashCodePayload payload) {
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n");
        sb.append("<soapenv:Header/>\n");
        sb.append("<soapenv:Body>\n");
        sb.append("<soap:ValidateDigestDocument>\n");
        sb.append("<soap:DigestValidationRequest>\n");
        sb.append(String.format("<Filename>%s</Filename>\n", payload.getContainerFileName()));
        sb.append(String.format("<ReportType>%s</ReportType>\n", payload.getReportType()));
        sb.append(String.format("<SignaturePolicy>%s</SignaturePolicy>\n", payload.getSignaturePolicy()));
        sb.append("<Content>\n");
        payload.getDetachedContents().forEach(c -> {
            sb.append("<DigestDocument>\n");
            sb.append(String.format("<Filename>%s</Filename>\n", c.getFileName()));
            sb.append(String.format("<DigestAlgorithm>%s</DigestAlgorithm>\n", c.getDigestAlgorithm()));
            sb.append(String.format("<Digest>%s</Digest>\n", c.getBase64Digest()));
            sb.append("</DigestDocument>\n");
        });
        sb.append("</Content>\n");
        sb.append("<Signatures>\n");
        payload.getSignatureContents().forEach(c -> {
            sb.append("<Signature>\n");
            sb.append(String.format("<Filename>%s</Filename>\n", c.getFileName()));
            sb.append(String.format("<Content>%s</Content>\n", c.getBase64Value()));
            sb.append("</Signature>\n");
        });
        sb.append("</Signatures>\n");
        sb.append("</soap:DigestValidationRequest>\n");
        sb.append("</soap:ValidateDigestDocument>\n");
        sb.append("</soapenv:Body>\n");
        sb.append("</soapenv:Envelope>");
        return sb.toString();
    }

}
