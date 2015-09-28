package ee.sk.pdfvalidatortest;

import com.jayway.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;

public abstract class PdfValidatorSoapTests {
    private static final Properties TESTS_PROPERTIES = readProperties("src/main/config/tests.properties");
    private static final String PROJECT_SUBMODULE_NAME =  "pdf-validator-test";

    public static final String VALIDATION_SERVICE_URL = TESTS_PROPERTIES.getProperty("service_url");

    protected static String validationRequestFor(byte[] pdf) {
        return "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <ns2:validateDocument xmlns:ns2=\"http://ws.dss.esig.europa.eu/\">\n" +
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

    protected Document simpleReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlSimpleReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

    protected int validSignatures(Document simpleReport) {
        String stringResult = XmlUtil.findElementByXPath(
                simpleReport,
                "//d:SimpleReport/d:ValidSignaturesCount",
                Collections.singletonMap("d", "http://dss.esig.europa.eu/validation/diagnostic")).getTextContent();

        return Integer.parseInt(stringResult);
    }

    protected Document detailedReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlDetailedReport").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

    protected String certificateContentsById(String certificateId, Document diagnosticData) {
        return XmlUtil.findElementByXPath(
                diagnosticData,
                "//d:DiagnosticData/d:UsedCertificates/d:Certificate[@Id='" + certificateId + "']/d:X509Data",
                Collections.singletonMap("d", "http://dss.esig.europa.eu/validation/diagnostic")).getTextContent();
    }

    protected Document diagnosticData(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReportString = XmlUtil.findElementByXPath(document, "//xmlDiagnosticData").getTextContent();
        return XmlUtil.parseXml(detailedReportString);
    }

    protected String findErrorById(String errorId, Document detailedReport) {
        return XmlUtil.findElementByXPath(
                detailedReport,
                "//d:Error[@NameId='" + errorId + "']",
                Collections.singletonMap("d", "http://dss.esig.europa.eu/validation/diagnostic")).getTextContent();
    }

    protected String findWarningById(String errorId, Document detailedReport) {
        return XmlUtil.findElementByXPath(
                detailedReport,
                "//d:Warning[@NameId='" + errorId + "']",
                Collections.singletonMap("d", "http://dss.esig.europa.eu/validation/diagnostic")).getTextContent();
    }

    protected Response post(String request) {
        return given().
                contentType("text/xml;charset=UTF-8").
                body(request).
                when().
                post(VALIDATION_SERVICE_URL);
    }

    protected static byte[] readFileFromTestResources(String relativePath, String fileName) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + relativePath + fileName);
    }

    protected abstract byte[] readFile(String fileName);

    private static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);

        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
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

}