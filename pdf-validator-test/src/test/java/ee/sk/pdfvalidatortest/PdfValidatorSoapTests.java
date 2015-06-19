package ee.sk.pdfvalidatortest;

import com.jayway.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;

public abstract class PdfValidatorSoapTests {
    private static final Properties TESTS_PROPERTIES = readProperties("src/main/config/tests.properties");
    private static final String PROJECT_SUBMODULE_NAME =  "pdf-validator-test";

    public static final String VALIDATION_SERVICE_URL = TESTS_PROPERTIES.getProperty("service_url");

    public static final byte[] PDF_MISSING_SIGNING_CERT_ATTR = readFile("missing_signing_certificate_attribute.pdf");

    protected String validationRequestFor(byte[] pdf) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <ns2:validateDocument xmlns:ns2=\"http://ws.dss.markt.ec.europa.eu/\">\n" +
                "            <document>\n" +
                "                <bytes>\n" +
                "                    " + Base64.encodeBase64String(pdf) +
                "                </bytes>\n" +
                "                <name>/fixedpath/file.pdf</name>\n" +
                "                <absolutePath>/fixedpath/file.pdf</absolutePath>\n" +
                "                <mimeType>PDF</mimeType>\n" +
                "                <nextDocument></nextDocument>\n" +
                "            </document>\n" +
                "        <diagnosticDataToBeReturned>true</diagnosticDataToBeReturned>\n" +
                "        </ns2:validateDocument>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    protected Response post(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(VALIDATION_SERVICE_URL);
    }

    private static Properties readProperties(String pathname) {
        String fullPath = getProjectBaseDirectory() + pathname;
        Properties properties = new Properties();

        try {
            properties.load(new FileReader(new File(fullPath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    protected static byte[] readFile(String fileName) {
        String testFilesBase = getProjectBaseDirectory();
        return readFileFromPath(testFilesBase + "src/test/resources/" + fileName);
    }

    private static String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

    protected static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
