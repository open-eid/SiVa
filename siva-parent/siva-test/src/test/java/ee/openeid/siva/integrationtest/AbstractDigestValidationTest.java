package ee.openeid.siva.integrationtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import org.digidoc4j.Configuration;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerOpener;
import org.digidoc4j.DigestAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import ee.openeid.siva.webapp.request.DetachedContent;
import ee.openeid.siva.webapp.request.SignatureContent;
import ee.openeid.siva.webapp.request.ValidateHashCodePayload;

/**
 * Created by Andrei on 15.03.2018.
 */
public abstract class AbstractDigestValidationTest extends SiVaRestTests {

  protected String testFilesDirectory;
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected Response post(String request) {
    return RestAssured.given().config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
        .body(request).contentType(ContentType.JSON).when().post("/validateDigest");
  }

  @Autowired
  protected String getTestFilesDirectory() {
    return this.testFilesDirectory;
  }

  @Override
  protected String validationRequestFor(String file, String signaturePolicy, String reportType) {
    try (InputStream stream = new ByteArrayInputStream(this.readFileFromTestResources(file))) {
      Container container = ContainerOpener.open(stream, Configuration.of(Configuration.Mode.PROD));
      ValidateHashCodePayload payload = new ValidateHashCodePayload();
      payload.setContainerFileName(file);
      payload.setReportType(reportType);
      payload.setSignaturePolicy(signaturePolicy);
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
      return this.objectMapper.writeValueAsString(payload);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
