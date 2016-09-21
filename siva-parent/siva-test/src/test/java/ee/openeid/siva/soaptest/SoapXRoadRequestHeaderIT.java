package ee.openeid.siva.soaptest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

@Category(IntegrationTest.class)
public class SoapXRoadRequestHeaderIT extends SiVaSoapTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    @Test
    public void soapRequestHeadersAreReturnedInResponse() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header>\n" +
                "       <xrd:client id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client>\n" +
                "       <xrd:service id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service>\n" +
                "       <xrd:id>4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id>\n" +
                "       <xrd:userId>EE12345678901</xrd:userId>\n" +
                "       <xrd:issue>12345</xrd:issue>\n" +
                "       <xrd:protocolVersion>4.0</xrd:protocolVersion>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <Filename>Valid_IDCard_MobID_signatures.bdoc</Filename>\n" +
                "            <DocumentType>BDOC</DocumentType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedSoapResponseHeader =
                "<soap:Header><xrd:client xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client><xrd:service xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service><xrd:id xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id><xrd:userId xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">EE12345678901</xrd:userId><xrd:issue xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">12345</xrd:issue><xrd:protocolVersion xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4.0</xrd:protocolVersion></soap:Header>";

        String httpResponseBody = post(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }

    @Test
    public void soapRequestHeadersAreReturnedInSameOrderInResponse() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header>\n" +
                "       <xrd:service id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service>\n" +
                "       <xrd:client id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client>\n" +
                "       <xrd:id>4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id>\n" +
                "       <xrd:userId>EE12345678901</xrd:userId>\n" +
                "       <xrd:issue>12345</xrd:issue>\n" +
                "       <xrd:protocolVersion>4.0</xrd:protocolVersion>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <Filename>Valid_IDCard_MobID_signatures.bdoc</Filename>\n" +
                "            <DocumentType>BDOC</DocumentType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedSoapResponseHeader =
                "<soap:Header><xrd:service xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service><xrd:client xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client><xrd:id xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id><xrd:userId xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">EE12345678901</xrd:userId><xrd:issue xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">12345</xrd:issue><xrd:protocolVersion xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4.0</xrd:protocolVersion></soap:Header>";

        String httpResponseBody = post(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }

    @Test
    public void soapRequestHeadersAreReturnedInFaultResponse() {
        String encodedString = "YmxhaA==";

        String requestBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header>\n" +
                "       <xrd:client id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client>\n" +
                "       <xrd:service id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service>\n" +
                "       <xrd:id>4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id>\n" +
                "       <xrd:userId>EE12345678901</xrd:userId>\n" +
                "       <xrd:issue>12345</xrd:issue>\n" +
                "       <xrd:protocolVersion>4.0</xrd:protocolVersion>\n" +
                "   </soapenv:Header>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <Filename>Valid_IDCard_MobID_signatures.bdoc</Filename>\n" +
                "            <DocumentType>BDOC</DocumentType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedSoapResponseHeader =
                "<soap:Header><xrd:client xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SUBSYSTEM\">\n" +
                        "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                        "           <id:memberClass>GOV</id:memberClass>\n" +
                        "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                        "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                        "       </xrd:client><xrd:service xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" xmlns:id=\"http://x-road.eu/xsd/identifiers\" id:objectType=\"SERVICE\">\n" +
                        "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                        "           <id:memberClass>GOV</id:memberClass>\n" +
                        "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                        "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                        "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                        "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                        "       </xrd:service><xrd:id xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id><xrd:userId xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">EE12345678901</xrd:userId><xrd:issue xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">12345</xrd:issue><xrd:protocolVersion xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4.0</xrd:protocolVersion></soap:Header>";

        String httpResponseBody = post(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
