package ee.openeid.siva.integrationtest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import ee.openeid.siva.SivaWebApplication;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.integrationtest.report.simple.SimpleReport;
import ee.openeid.siva.integrationtest.report.simple.SimpleReportWrapper;
import ee.openeid.siva.proxy.document.DocumentType;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SivaWebApplication.class, webEnvironment=RANDOM_PORT)
public abstract class SiVaRestTests {

    private static final String PROJECT_SUBMODULE_NAME =  "siva-test";
    private static final String VALIDATION_ENDPOINT = "/validate";

    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    protected Response post(String request) {
        return given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(VALIDATION_ENDPOINT);
    }

    protected SimpleReport postForSimpleReport(String file) {
        try {
            return new ObjectMapper().readValue(post(validationRequestFor(file, "simple")).andReturn().body().asString(), SimpleReportWrapper.class).getSimpleReport();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected String validationRequestFor(String file, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        jsonObject.put("documentType", parseFileExtension(file));
        jsonObject.put("reportType", reportType);
        return jsonObject.toString();
    }

    protected byte[] readFileFromTestResources(String fileName) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + fileName);
    }

    protected abstract String getTestFilesDirectory();

    protected void assertInvalidWithError(SimpleReport report, String errorCode, String expectedMessage) {
        assertAllSignaturesAreInvalid(report);
        assertEquals(expectedMessage, report.findErrorById(errorCode).get().getContent());
    }

    protected void assertHasWarning(SimpleReport report, String warningCode, String expectedMessage) {
        assertEquals(expectedMessage, report.findWarningById(warningCode).get().getContent());
    }

    protected void assertAllSignaturesAreValid(SimpleReport report) {
        assertTrue(report.getSignaturesCount() == report.getValidSignaturesCount());
    }

    protected void assertSomeSignaturesAreValid(SimpleReport report, int expectedValidSignatures) {
        assertTrue(expectedValidSignatures == report.getValidSignaturesCount());
    }

    protected void assertAllSignaturesAreInvalid(SimpleReport report) {
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    private static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DocumentType parseFileExtension(final String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        return Arrays.asList(DocumentType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
    }

    private static String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

}
