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
package ee.openeid.siva.integrationtest;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class DdocGetDataFilesIT  extends SiVaRestTests{

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
     * TestCaseID:  Ddoc-Get-Data-Files-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: Valid DDOC with data file used
     *
     * Expected Result: The data file should be returned
     *
     * File: 18912.ddoc
     */
    @Test
    public void testGetDataFileFromValidDdoc(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("18912.ddoc"))
                .then()
                .body("dataFiles[0].fileName", Matchers.is("readme"))
                .body("dataFiles[0].mimeType", Matchers.is("text/plain"))
                .body("dataFiles[0].base64", Matchers.startsWith("RGlnaURvYyBpcyBhIGdlbmVyaWMgbGlicmFyeSBp"))
                .body("dataFiles[0].size", Matchers.is(491));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: Invalid DDOC with data file used
     *
     * Expected Result: The data file should be returned
     *
     * File: OCSP nonce vale.ddoc
     */
    @Test
    public void testGetDataFileFromInvalidDdoc(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("OCSP nonce vale.ddoc"))
                .then()
                .body("dataFiles[0].fileName", Matchers.is("testfail.txt"))
                .body("dataFiles[0].mimeType", Matchers.is("text/plain"))
                .body("dataFiles[0].base64", Matchers.is("T2xlbiB0ZXN0IGZhaWwu\n"))
                .body("dataFiles[0].size", Matchers.is(15));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title:DDOC with xml v1.1 is  used
     *
     * Expected Result: The data file is returned
     *
     * File: DIGIDOC-XML1.1.ddoc
     * */
    @Test
    public void testGetDataFileFromDdocXml1_1(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("DIGIDOC-XML1.1.ddoc"))
                .then()
                .log().ifError()
                .body("dataFiles[0].fileName", Matchers.is("puhkus_urmo_062006.doc"))
                .body("dataFiles[0].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[0].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAAJAAAA"))
                .body("dataFiles[0].size", Matchers.is(549376));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-4
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: DDOC with xml v1.2 is  used
     *
     * Expected Result: The data file is returned
     *
     * File: DIGIDOC-XML1.2.ddoc
     * */
    @Test
    public void testGetDataFileFromDdocXml1_2(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("DIGIDOC-XML1.2.ddoc"))
                .then()
                .log().ifError()
                .body("dataFiles[0].fileName", Matchers.is("RO219559508.pdf"))
                .body("dataFiles[0].mimeType", Matchers.is("text/text"))
                .body("dataFiles[0].base64", Matchers.startsWith("JVBERi0xLjMKJeLjz9MKMSAwIG9iajw8L1Byb2R1Y2VyKGh0bWxkb2MgMS44LjIzIENvcHlyaWdodCAxOTk3LTIwMDI"))
                .body("dataFiles[0].size", Matchers.is(5252));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-5
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: DDOC with xml v1.3 is  used
     *
     * Expected Result: The data file is returned
     *
     * File: DIGIDOC-XML1.3.ddoc
     * */
    @Test
    public void testGetDataFileFromDdocXml1_3(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("DIGIDOC-XML1.3.ddoc"))
                .then()
                .log().ifError()
                .body("dataFiles[0].fileName", Matchers.is("Glitter-rock-4_gallery.jpg"))
                .body("dataFiles[0].mimeType", Matchers.is("application/octet-stream"))
                .body("dataFiles[0].base64", Matchers.startsWith("/9j/4AAQSkZJRgABAQAAAQABAAD/4RXeRXhpZgAASUkqAAgAAAACADEBAgAHAAAA"))
                .body("dataFiles[0].size", Matchers.is(41114));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-6
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: Hashcoded DDOC  is  used
     *
     * Expected Result: Null is returned
     *
     * File: DIGIDOC-XML1.3_hashcode.ddoc
     * */
    @Test
    public void testGetDataFileFromDdocHashcoded(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("DIGIDOC-XML1.3_hashcode.ddoc"))
                .then()
                .log().ifError()
                .body("dataFiles[0].fileName", Matchers.is("Glitter-rock-4_gallery.jpg"))
                .body("dataFiles[0].mimeType", Matchers.is("application/octet-stream"))
                .body("dataFiles[0].base64", Matchers.nullValue())
                .body("dataFiles[0].size", Matchers.is(41114));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-7
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: DDOC  with 12 different  files of different types  is  used
     *
     * Expected Result: Data file returned for each file correctly
     *
     * File: igasugust1.3.ddoc
     * */
    @Test @Ignore // Run this test manually as it fails in Travis because of big response data
    public void testGetMultipileDataFilesFromDdoc(){
        setTestFilesDirectory("ddoc/live/timemark/");
        postForDataFiles(dataFilesRequest("igasugust1.3.ddoc"))
                .then()
                .log().ifError()
                .body("dataFiles[0].fileName", Matchers.is("DigiDocService_spec_1_110_est.pdf"))
                .body("dataFiles[0].mimeType", Matchers.is("application/pdf"))
                .body("dataFiles[0].base64", Matchers.startsWith("JVBERi0xLjMKJcfsj6IKOCAwIG9iago8PC9MZW5ndGggOSAwIFIvRmlsdGVyIC9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nLVWTXMTMQy97"))
                .body("dataFiles[0].size", Matchers.is(435164))
                .body("dataFiles[1].fileName", Matchers.is("Testilood20070320.doc"))
                .body("dataFiles[1].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[1].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAAEAAAArgEAAAAAA"))
                .body("dataFiles[1].size", Matchers.is(222720))
                .body("dataFiles[2].fileName", Matchers.is("fail.rtf"))
                .body("dataFiles[2].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[2].base64", Matchers.startsWith("e1xydGYxXGFuc2lcZGVmZjBcYWRlZmxhbmcxMDI1CntcZm9udHRibHtcZjBcZnJv"))
                .body("dataFiles[2].size", Matchers.is(2145))
                .body("dataFiles[3].fileName", Matchers.is("fail.odt"))
                .body("dataFiles[3].mimeType", Matchers.is("application/unknown"))
                .body("dataFiles[3].base64", Matchers.startsWith("UEsDBBQAAAAAAJhRwTpexjIMJwAAACcAAAAIAAAAbWltZXR5cGVhcHBsaWNhdGlvbi92bmQub2FzaXMub3BlbmRvY3VtZW50LnRleH"))
                .body("dataFiles[3].size", Matchers.is(7427))
                .body("dataFiles[4].fileName", Matchers.is("4.txt"))
                .body("dataFiles[4].mimeType", Matchers.is("text/plain"))
                .body("dataFiles[4].base64", Matchers.startsWith("/GtzZmFpbA==\n"))
                .body("dataFiles[4].size", Matchers.is(7))
                .body("dataFiles[5].fileName", Matchers.is("kolm.doc"))
                .body("dataFiles[5].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[5].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAABAAAAKgAAAAAAAAAAEAAALAAAAAEA"))
                .body("dataFiles[5].size", Matchers.is(24064))
                .body("dataFiles[6].fileName", Matchers.is("5.xls"))
                .body("dataFiles[6].mimeType", Matchers.is("application/vnd.ms-excel"))
                .body("dataFiles[6].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAABAAAAGwAAAAAAAAAAEAAA/v///wAAAAD+"))
                .body("dataFiles[6].size", Matchers.is(14848))
                .body("dataFiles[7].fileName", Matchers.is("kaks.doc"))
                .body("dataFiles[7].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[7].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAABAAAAKgAAAAAAAAAAEAAALAAAAAEAAAD+////AAAAACkAAAD"))
                .body("dataFiles[7].size", Matchers.is(24064))
                .body("dataFiles[8].fileName", Matchers.is("kõõs.txt"))
                .body("dataFiles[8].mimeType", Matchers.is("text/plain"))
                .body("dataFiles[8].base64", Matchers.is("bfZoaGho\n"))
                .body("dataFiles[8].size", Matchers.is(6))
                .body("dataFiles[9].fileName", Matchers.is("yks.doc"))
                .body("dataFiles[9].mimeType", Matchers.is("application/msword"))
                .body("dataFiles[9].base64", Matchers.startsWith("0M8R4KGxGuEAAAAAAAAAAAAAAAAAAAAAPgADAP7/CQAGAAAAAAAAAAAAAAABAAAAKgAAAAAAAAAAEAAALAAAAAEAAAD+////AAAAACkAAAD////////////////////////////////////////////////////////////////////////////////////////////////////"))
                .body("dataFiles[9].size", Matchers.is(24064))
                .body("dataFiles[10].fileName", Matchers.is("testid.txt"))
                .body("dataFiles[10].mimeType", Matchers.is("text/plain"))
                .body("dataFiles[10].base64", Matchers.startsWith("UElOMSBibG9raXM6DQoNCjI1MTMNCjI1MjMNCjI1MjcNCjI1MzENCjI1NTkNCj"))
                .body("dataFiles[10].size", Matchers.is(414))
                .body("dataFiles[11].fileName", Matchers.is("NsPdf.PDF"))
                .body("dataFiles[11].mimeType", Matchers.is("application/pdf"))
                .body("dataFiles[11].base64", Matchers.startsWith("JVBERi0xLjMKJeTjz9IKNSAwIG9iago8PC9MZW5ndGggNiAwIFIKL0ZpbHRlci9G\nbGF0ZURlY29"))
                .body("dataFiles[11].size", Matchers.is(2783));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-8
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: BDOC with data file used
     *
     * Expected Result: The error message  should be returned
     *
     * File: BDOC-TS.bdoc
     **/
    @Test
    public void testGetDataFileFromBdocShouldFail(){
        setTestFilesDirectory("bdoc/live/timestamp/");
        postForDataFiles(dataFilesRequest("BDOC-TS.bdoc"))
                .then()
                .log().ifError()
                .body("requestErrors[0].message", Matchers.is(INVALID_DOCUMENT_TYPE_DDOC))
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE));
    }
    /**
     * TestCaseID:  Ddoc-Get-Data-Files-9
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/v2/use_cases/#ddoc-data-file-extraction-process
     *
     * Title: PDF file used
     *
     * Expected Result: The error message  should be returned
     *
     * File: hellopades-lt-b.pdf
     * */
    @Test
    public void testGetDataFileFromPdfShouldFail(){
        setTestFilesDirectory("pdf/baseline_profile_test_files/");
        postForDataFiles(dataFilesRequest("hellopades-lt-b.pdf"))
                .then()
                .log().ifError()
                .body("requestErrors[0].message", Matchers.is(INVALID_DOCUMENT_TYPE_DDOC))
                .body("requestErrors[0].key", Matchers.is(DOCUMENT_TYPE));
    }
    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
