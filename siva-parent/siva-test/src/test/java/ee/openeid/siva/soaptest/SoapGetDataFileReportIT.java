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

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("IntegrationTest")
public class SoapGetDataFileReportIT extends SiVaSoapTests  {

    @BeforeEach
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }


    private static final String DEFAULT_TEST_FILES_DIRECTORY = "ddoc/get_data_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     *
     * TestCaseID: SoapGetDataFile-Report-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-response-interface
     *
     * Title: Verification of values in Validation Report, checks for  File name, Base64, Mimetype and Size
     *
     * Expected Result: All required elements are present and meet the expected values
     *
     * File: valid_XML1_3.ddoc
     *
     **/
    @Test
    public void soapGetDataFilesCorrectValuesArePresent(){
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("valid_XML1_3.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "test.DDOC")).andReturn().body().asString());
        assertEquals("test.txt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename(), "File name should match expected");
        assertEquals("VGVzdCBhbmQgc29tZSBvdGhlciB0ZXN0", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64(), "Base64 should match expected");
        assertEquals("application/octet-stream", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType(), "Mimetype should match expected");
        assertEquals(24, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize(), "Size should match expected");
    }
    /**
     *
     * TestCaseID: SoapGetDataFile-Report-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-response-interface
     *
     * Title: Verification of values in Validation Report xml v1.1, checks for  File name, Base64, Mimetype and Size
     *
     * Expected Result: All required elements are present and meet the expected values
     *
     * File: DIGIDOC-XML1.1.ddoc
     *
     **/
    @Test
    public void soapGetDataFilesFromDdocV1_1CorrectValuesArePresent(){
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("DIGIDOC-XML1.1.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "test.DDOC")).andReturn().body().asString());
        String begginingBase64 = getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64().substring(0, 55);
        assertEquals("puhkus_urmo_062006.doc", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", begginingBase64, "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType(), "Mimetype should match expected");
        assertEquals(549376, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize(), "Size should match expected");
    }
    /**
     *
     * TestCaseID: SoapGetDataFile-Report-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-response-interface
     *
     * Title: Verification of values in Validation Report xml v1.2, checks for  File name, Base64, Mimetype and Size
     *
     * Expected Result: All required elements are present and meet the expected values
     *
     * File: DIGIDOC-XML1.2.ddoc
     *
     **/
    @Test
    public void soapGetDataFilesFromDdocV1_2CorrectValuesArePresent(){
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("DIGIDOC-XML1.2.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "test.DDOC")).andReturn().body().asString());
        String begginingBase64 = getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64().substring(0, 55);
        assertEquals("RO219559508.pdf", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename(), "File name should match expected");
        assertEquals("JVBERi0xLjMKJeLjz9MKMSAwIG9iajw8L1Byb2R1Y2VyKGh0bWxkb2M", begginingBase64, "Base64 should match expected");
        assertEquals("text/text", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType(), "Mimetype should match expected");
        assertEquals(3938, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize(), "Size should match expected");
    }
    /**
     *
     * TestCaseID: SoapGetDataFile-Report-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva3/interfaces/#data-files-response-interface
     *
     * Title: Verification of values in Validation Report for container with a number of files, checks for  File name, Base64, Mimetype and Size for each file
     *
     * Expected Result: All required elements are present and meet the expected values
     *
     * File: igasugust1.3.ddoc
     *
     **/
    @Test
    public void soapGetDataFilesFromDdocManyFilesCorrectValuesArePresent(){
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "igasugust1.3.ddoc")).andReturn().body().asString());
        assertEquals("DigiDocService_spec_1_110_est.pdf", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename(), "File name should match expected");
        assertEquals("JVBERi0xLjMKJcfsj6IKOCAwIG9iago8PC9MZW5ndGggOSAwIFIvRml", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/pdf", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType(), "Mimetype should match expected");
        assertEquals(435164, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize(), "Size should match expected");
        assertEquals("Testilood20070320.doc", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getMimeType(), "Mimetype should match expected");
        assertEquals(222720, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getSize(), "Size should match expected");
        assertEquals("fail.rtf", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getFilename(), "File name should match expected");
        assertEquals("e1xydGYxXGFuc2lcZGVmZjBcYWRlZmxhbmcxMDI1CntcZm9udHRibHt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getMimeType(), "Mimetype should match expected");
        assertEquals(2145, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getSize(), "Size should match expected");
        assertEquals("fail.odt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getFilename(), "File name should match expected");
        assertEquals("UEsDBBQAAAAAAJhRwTpexjIMJwAAACcAAAAIAAAAbWltZXR5cGVhcHB", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/unknown", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getMimeType(), "Mimetype should match expected");
        assertEquals(7427, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getSize(), "Size should match expected");
        assertEquals("4.txt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getFilename(), "File name should match expected");
        assertEquals("/GtzZmFpbA==", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getBase64(), "Base64 should match expected");
        assertEquals("text/plain", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getMimeType(), "Mimetype should match expected");
        assertEquals(7, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getSize(), "Size should match expected");
        assertEquals("kolm.doc", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getMimeType(), "Mimetype should match expected");
        assertEquals(24064, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getSize(), "Size should match expected");
        assertEquals("5.xls", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/vnd.ms-excel", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getMimeType(), "Mimetype should match expected");
        assertEquals(14848, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getSize(), "Size should match expected");
        assertEquals("kaks.doc", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getMimeType(), "Mimetype should match expected");
        assertEquals(24064, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getSize(), "Size should match expected");
        assertEquals("kõõs.txt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getFilename(), "File name should match expected");
        assertEquals("bfZoaGho", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getBase64(), "Base64 should match expected");
        assertEquals("text/plain", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getMimeType(), "Mimetype should match expected");
        assertEquals(6, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getSize(), "Size should match expected");
        assertEquals("yks.doc", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getFilename(), "File name should match expected");
        assertEquals("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/msword", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getMimeType(), "Mimetype should match expected");
        assertEquals(24064, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getSize(), "Size should match expected");
        assertEquals("testid.txt", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getFilename(), "File name should match expected");
        assertEquals("UElOMSBibG9raXM6DQoNCjI1MTMNCjI1MjMNCjI1MjcNCjI1MzENCjI", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("text/plain", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getMimeType(), "Mimetype should match expected");
        assertEquals(414, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getSize(), "Size should match expected");
        assertEquals("NsPdf.PDF", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getFilename(), "File name should match expected");
        assertEquals("JVBERi0xLjMKJeTjz9IKNSAwIG9iago8PC9MZW5ndGggNiAwIFIKL0Z", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getBase64().substring(0, 55), "Base64 should match expected");
        assertEquals("application/pdf", getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getMimeType(), "Mimetype should match expected");
        assertEquals(2783, getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getSize(), "Size should match expected");
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
