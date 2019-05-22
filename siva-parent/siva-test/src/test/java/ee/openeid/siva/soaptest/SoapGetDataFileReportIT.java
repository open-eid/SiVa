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

import static org.junit.Assert.assertEquals;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

@Category(IntegrationTest.class)
public class SoapGetDataFileReportIT extends SiVaSoapTests  {

    @Before
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
     * File: ddoc_1_3.xml.ddoc
     *
     **/
    @Test
    public void soapGetDataFilesCorrectValuesArePresent(){
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("ddoc_1_3.xml.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "test.DDOC")).andReturn().body().asString());
        assertEquals("File name should match expected","test2007.txt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename());
        assertEquals("Base64 should match expected","dGVzdA==" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64());
        assertEquals("Mimetype should match expected","text/plain" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType());
        assertEquals("Size should match expected",4 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize());
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
        assertEquals("File name should match expected","puhkus_urmo_062006.doc" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,begginingBase64);
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType());
        assertEquals("Size should match expected",549376 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize());
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
        assertEquals("File name should match expected","RO219559508.pdf" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename());
        assertEquals("Base64 should match expected","JVBERi0xLjMKJeLjz9MKMSAwIG9iajw8L1Byb2R1Y2VyKGh0bWxkb2M" ,begginingBase64);
        assertEquals("Mimetype should match expected","text/text" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType());
        assertEquals("Size should match expected",3938 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize());
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
    @Ignore //TODO: Run this test manually as it fails in Travis because of big response data
    @Test
    public void soapGetDataFilesFromDdocManyFilesCorrectValuesArePresent(){
        setTestFilesDirectory("ddoc/live/timemark/");
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("igasugust1.3.ddoc"));
        Document report = extractDataFilesReportDom(postDataFiles(createXMLValidationRequestForDataFiles(encodedString, "DDOC")).andReturn().body().asString());
        assertEquals("File name should match expected","DigiDocService_spec_1_110_est.pdf" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getFilename());
        assertEquals("Base64 should match expected","JVBERi0xLjMKJcfsj6IKOCAwIG9iago8PC9MZW5ndGggOSAwIFIvRml" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/pdf" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getMimeType());
        assertEquals("Size should match expected",435164 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(0).getSize());
        assertEquals("File name should match expected","Testilood20070320.doc" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getMimeType());
        assertEquals("Size should match expected",222720 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(1).getSize());
        assertEquals("File name should match expected","fail.rtf" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getFilename());
        assertEquals("Base64 should match expected","e1xydGYxXGFuc2lcZGVmZjBcYWRlZmxhbmcxMDI1CntcZm9udHRibHt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getMimeType());
        assertEquals("Size should match expected",2145 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(2).getSize());
        assertEquals("File name should match expected","fail.odt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getFilename());
        assertEquals("Base64 should match expected","UEsDBBQAAAAAAJhRwTpexjIMJwAAACcAAAAIAAAAbWltZXR5cGVhcHB" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/unknown" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getMimeType());
        assertEquals("Size should match expected",7427 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(3).getSize());
        assertEquals("File name should match expected","4.txt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getFilename());
        assertEquals("Base64 should match expected","/GtzZmFpbA==\n" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getBase64());
        assertEquals("Mimetype should match expected","text/plain" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getMimeType());
        assertEquals("Size should match expected",7 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(4).getSize());
        assertEquals("File name should match expected","kolm.doc" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getMimeType());
        assertEquals("Size should match expected",24064 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(5).getSize());
        assertEquals("File name should match expected","5.xls" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/vnd.ms-excel" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getMimeType());
        assertEquals("Size should match expected",14848 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(6).getSize());
        assertEquals("File name should match expected","kaks.doc" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getMimeType());
        assertEquals("Size should match expected",24064 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(7).getSize());
        assertEquals("File name should match expected","kõõs.txt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getFilename());
        assertEquals("Base64 should match expected","bfZoaGho\n" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getBase64());
        assertEquals("Mimetype should match expected","text/plain" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getMimeType());
        assertEquals("Size should match expected",6 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(8).getSize());
        assertEquals("File name should match expected","yks.doc" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getFilename());
        assertEquals("Base64 should match expected","0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAA" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/msword" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getMimeType());
        assertEquals("Size should match expected",24064 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(9).getSize());
        assertEquals("File name should match expected","testid.txt" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getFilename());
        assertEquals("Base64 should match expected","UElOMSBibG9raXM6DQoNCjI1MTMNCjI1MjMNCjI1MjcNCjI1MzENCjI" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","text/plain" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getMimeType());
        assertEquals("Size should match expected",414 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(10).getSize());
        assertEquals("File name should match expected","NsPdf.PDF" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getFilename());
        assertEquals("Base64 should match expected","JVBERi0xLjMKJeTjz9IKNSAwIG9iago8PC9MZW5ndGggNiAwIFIKL0Z" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getBase64().substring(0, 55));
        assertEquals("Mimetype should match expected","application/pdf" ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getMimeType());
        assertEquals("Size should match expected",2783 ,getDataFilesReportFromDom(report).getDataFiles().getDataFile().get(11).getSize());
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
