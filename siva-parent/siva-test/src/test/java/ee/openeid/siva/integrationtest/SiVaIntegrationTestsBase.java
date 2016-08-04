package ee.openeid.siva.integrationtest;

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.SivaWebApplication;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import ee.openeid.siva.validation.document.report.Warning;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SivaWebApplication.class, webEnvironment=RANDOM_PORT)
@ActiveProfiles("test")
public abstract class SiVaIntegrationTestsBase {

    private static final String PROJECT_SUBMODULE_NAME =  "siva-test";

    @Value("${local.server.port}")
    protected int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected abstract String getTestFilesDirectory();

    protected abstract QualifiedReport postForReport(String filename);

    protected byte[] readFileFromTestResources(String fileName) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + fileName);
    }

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

    protected static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String parseFileExtension(final String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        if (isAsicExtension(fileExtension)) {
            return DocumentType.BDOC.name();
        }
        return resolveDocumentType(fileExtension);
    }

    protected String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

    private boolean isAsicExtension(String fileExtension) {
        return StringUtils.equalsIgnoreCase("asice", fileExtension);
    }

    private String resolveDocumentType(String fileExtension) {
        DocumentType documentType = Arrays.asList(DocumentType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
        return documentType != null ? documentType.name() : fileExtension;
    }



}
