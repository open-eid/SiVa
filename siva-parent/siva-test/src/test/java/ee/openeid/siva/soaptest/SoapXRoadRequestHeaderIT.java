/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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

    /**
     *
     * TestCaseID: XroadSoap-RequestVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Soap headers are returned in response
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     */
    @Test
    public void soapRequestHeadersAreReturnedInValidationResponse() {
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
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedSoapResponseHeader =
                "<soap:Header><xrd:client xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client><xrd:service xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" id:objectType=\"SERVICE\">\n" +
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

    /**
     *
     * TestCaseID: XroadSoap-RequestVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Soap headers are returned in correct order
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     */
    @Test
    public void soapRequestHeadersAreReturnedInSameOrderInValidationResponse() {
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
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String expectedSoapResponseHeader =
                "<soap:Header><xrd:service xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" id:objectType=\"SERVICE\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER2</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM2</id:subsystemCode>\n" +
                "           <id:serviceCode>exampleService</id:serviceCode>\n" +
                "           <id:serviceVersion>v1</id:serviceVersion>\n" +
                "       </xrd:service><xrd:client xmlns:id=\"http://x-road.eu/xsd/identifiers\" xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\" id:objectType=\"SUBSYSTEM\">\n" +
                "           <id:xRoadInstance>EE</id:xRoadInstance>\n" +
                "           <id:memberClass>GOV</id:memberClass>\n" +
                "           <id:memberCode>MEMBER1</id:memberCode>\n" +
                "           <id:subsystemCode>SUBSYSTEM1</id:subsystemCode>\n" +
                "       </xrd:client><xrd:id xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4894e35d-bf0f-44a6-867a-8e51f1daa7e0</xrd:id><xrd:userId xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">EE12345678901</xrd:userId><xrd:issue xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">12345</xrd:issue><xrd:protocolVersion xmlns:xrd=\"http://x-road.eu/xsd/xroad.xsd\">4.0</xrd:protocolVersion></soap:Header>";

        String httpResponseBody = post(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }

    /**
     *
     * TestCaseID: XroadSoap-RequestVerification-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#validation-request-interface
     *
     * Title: Soap headers are returned in error response
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: not relevant
     *
     */
    @Test
    public void soapRequestHeadersAreReturnedInFaultValidationResponse() {
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
    /**
     *
     * TestCaseID: XroadSoap-GetDataFiles-RequestVerification-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Soap headers are returned in response for Get Data Files
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: susisevad1_3.ddoc
     *
     */
    @Test
    public void soapGetDataFilesRequestHeadersAreReturnedInResponse() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("susisevad1_3.ddoc"));

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
                "      <soap:GetDocumentDataFiles>\n" +
                "         <soap:DataFilesRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <DocumentType>DDOC</DocumentType>\n" +
                "         </soap:DataFilesRequest>\n" +
                "      </soap:GetDocumentDataFiles>\n" +
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

        String httpResponseBody = postDataFiles(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }
    /**
     *
     * TestCaseID: XroadSoap-GetDataFiles-RequestVerification-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Soap headers are returned in correct order for Get Data Files
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: susisevad1_3.ddoc
     *
     */
    @Test
    public void soapGetDataFilesRequestHeadersAreReturnedInSameOrderInResponse() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("susisevad1_3.ddoc"));

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
                "      <soap:GetDocumentDataFiles>\n" +
                "         <soap:DataFilesRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <DocumentType>DDOC</DocumentType>\n" +
                "         </soap:DataFilesRequest>\n" +
                "      </soap:GetDocumentDataFiles>\n" +
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

        String httpResponseBody = postDataFiles(requestBody).getBody().asString();
        System.out.println(">>>>>>>>>>>>>>>>"+httpResponseBody+"<<<<<<<<<<<<<<<<<<<<");
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }
    /**
     *
     * TestCaseID: XroadSoap-GetDataFiles-RequestVerification-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-request-interface
     *
     * Title: Soap headers are returned in error response for Get Data Files
     *
     * Expected Result: Same headers are in response as in request
     *
     * File: not relevant
     *
     */
    @Test
    public void soapGetDataFilesRequestHeadersAreReturnedInFaultResponse() {
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
                "      <soap:GetDocumentDataFiles>\n" +
                "         <soap:DataFilesRequest>\n" +
                "            <Document>" + encodedString + "</Document>\n" +
                "            <DocumentType>DDOC</DocumentType>\n" +
                "         </soap:DataFilesRequest>\n" +
                "      </soap:GetDocumentDataFiles>\n" +
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

        String httpResponseBody = postDataFiles(requestBody).getBody().asString();
        assertThat(httpResponseBody, containsString(expectedSoapResponseHeader));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }

}
