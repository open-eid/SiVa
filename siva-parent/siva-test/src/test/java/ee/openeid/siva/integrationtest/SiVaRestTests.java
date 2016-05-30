package ee.openeid.siva.integrationtest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import ee.openeid.siva.SivaWebApplication;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.Warning;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.Collection;
import java.util.Optional;

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
    private static final boolean PRINT_RESPONSE = false;

    protected static final String DOCUMENT_TYPE = "documentType";
    protected static final String REPORT_TYPE = "reportType";
    protected static final String FILENAME = "filename";
    protected static final String DOCUMENT = "document";

    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected Response post(String request) {
        return given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(VALIDATION_ENDPOINT);
    }

    /**
     * Override to enable/disable printing the response per class
     * @return
     */
    protected boolean shouldPrintResponse() {
        return PRINT_RESPONSE;
    }

    protected QualifiedReport postForReport(String file) {
        if (shouldPrintResponse()) {
            return postForReportAndPrintResponse(file);
        }
        return mapToReport(post(validationRequestFor(file, "simple")).andReturn().body().asString());
    }

    protected QualifiedReport postForReportAndPrintResponse(String file) {
        return mapToReport(post(validationRequestFor(file, "simple")).andReturn().body().prettyPrint());
    }

    protected String validationRequestFor(String file, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("document", Base64.encodeBase64String(readFileFromTestResources(file)));
        jsonObject.put("filename", file);
        jsonObject.put("documentType", parseFileExtension(file));
        jsonObject.put("reportType", reportType);
        return jsonObject.toString();
    }

    protected String validationRequestForExtended(String documentKey, String encodedDocument,
                                                  String filenameKey, String file,
                                                  String documentTypeKey, String documentType,
                                                  String reportTypeKey, String reportType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(documentKey, encodedDocument);
        jsonObject.put(filenameKey, file);
        jsonObject.put(documentTypeKey, documentType);
        jsonObject.put(reportTypeKey, reportType);
        return jsonObject.toString();
    }

    protected String validationRequestWithValidKeys(String encodedString, String filename, String documentType, String reportType) {
        return validationRequestForExtended(
                DOCUMENT, encodedString,
                FILENAME, filename,
                DOCUMENT_TYPE, documentType,
                REPORT_TYPE, reportType
        );
    }

    protected byte[] readFileFromTestResources(String fileName) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + fileName);
    }

    protected abstract String getTestFilesDirectory();

    protected void assertInvalidWithError(QualifiedReport report, String errorCode, String expectedMessage) {
        assertAllSignaturesAreInvalid(report);
        assertEquals(expectedMessage, findErrorMessageById(errorCode, report).orElse(""));
    }

    protected void assertHasWarning(QualifiedReport report, String warningCode, String expectedMessage) {
        assertEquals(expectedMessage, findWarningMessageById(warningCode, report).orElse(""));
    }

    protected void assertAllSignaturesAreValid(QualifiedReport report) {
        assertTrue(report.getSignaturesCount().equals(report.getValidSignaturesCount()));
    }

    protected void assertSomeSignaturesAreValid(QualifiedReport report, int expectedValidSignatures) {
        assertTrue(expectedValidSignatures == report.getValidSignaturesCount());
    }

    protected void assertAllSignaturesAreInvalid(QualifiedReport report) {
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    protected Optional<String> findErrorMessageById(String errorCode, QualifiedReport report) {
        return report.getSignatures()
                .stream()
                .filter(sig -> sig.getErrors() != null && !sig.getErrors().isEmpty())
                .map(SignatureValidationData::getErrors)
                .flatMap(Collection::stream)
                .filter(error -> StringUtils.equals(errorCode, error.getNameId()))
                .map(Error::getContent).findFirst();
    }

    protected Optional<String> findWarningMessageById(String warningCode, QualifiedReport report) {
        return report.getSignatures()
                .stream()
                .filter(sig -> sig.getWarnings() != null && !sig.getWarnings().isEmpty())
                .map(SignatureValidationData::getWarnings)
                .flatMap(Collection::stream)
                .filter(warning -> StringUtils.equals(warningCode, warning.getNameId()))
                .map(Warning::getDescription).findFirst();
    }

    private QualifiedReport mapToReport(String json) {
        try {
            return new ObjectMapper().readValue(json, QualifiedReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseFileExtension(final String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        DocumentType documentType = Arrays.asList(DocumentType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
        return documentType != null ? documentType.name() : fileExtension;
    }

    private static String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

}
