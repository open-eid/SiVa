package ee.openeid.siva.soap;

import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class SoapValidationRequestTests extends SiVaSoapTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "document_format_test_files/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }


    /***
     *
     * TestCaseID: Soap-ValidationRequest-1
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with bdoc document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestRandomInputAsBdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestForDocumentExtended(encodedString, "BDOC", "Valid_IDCard_MobID_signatures.bdoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(DOCUMENT_MALFORMED));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-2
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with pdf document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestRandomInputAsPdfDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestForDocumentExtended(encodedString, "PDF", "Valid_IDCard_MobID_signatures.pdf", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(DOCUMENT_MALFORMED));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-3
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Input random base64 string as document with ddoc document type
     *
     * Expected Result: Error is returned stating problem in document
     *
     * File:
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestRandomInputAsDdocDocument() {
        String encodedString = "ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA==";
        post(validationRequestForDocumentExtended(encodedString, "DDOC", "Valid_IDCard_MobID_signatures.ddoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(DOCUMENT_MALFORMED));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-4
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Expected Result: Error is returned stating mismatch with required elements
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestEmptyInputs() {
        post(validationRequestForDocumentExtended("", "", "", ""))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultcode",Matchers.is("Unmarshalling Error: cvc-enumeration-valid: Value '' is not facet-valid with respect to enumeration '[PDF, XROAD, BDOC, DDOC]'. It must be a value from the enumeration. "));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-5
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Request with not base64 string as document
     *
     * Expected Result: Error is returned stating encoding problem
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestNonBase64Input() {
        String encodedString = ",:";
        post(validationRequestForDocumentExtended(encodedString, "DDOC", "Valid_IDCard_MobID_signatures.ddoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is(DOCUMENT_MALFORMED));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-6
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Verification of wrong document type as input
     *
     * Expected Result: Correct error code is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestInvalidDocumentType() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestForDocumentExtended(encodedString, "CDOC", "Valid_IDCard_MobID_signatures.bdoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode",Matchers.is(CLIENT_FAULT))
                .body("Envelope.Body.Fault.faultstring",Matchers.is("Unmarshalling Error: cvc-enumeration-valid: Value 'CDOC' is not facet-valid with respect to enumeration '[PDF, XROAD, BDOC, DDOC]'. It must be a value from the enumeration. "));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-7
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (pdf and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestNotMatchingDocumentTypeAndActualFileBdocPdf() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestForDocumentExtended(encodedString, "PDF", "Valid_IDCard_MobID_signatures.bdoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode", Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring", Matchers.is(DOCUMENT_MALFORMED));
    }

    /***
     *
     * TestCaseID: Soap-ValidationRequest-8
     *
     * TestType: Automated
     *
     * RequirementID: http://open-eid.github.io/SiVa/siva/interface_description/
     *
     * Title: Mismatch in documentType and actual document (ddoc and bdoc)
     *
     * Expected Result: Error is returned
     *
     * File: Valid_IDCard_MobID_signatures.bdoc
     *
     ***/
    @Test @Ignore //TODO: VAL-290
    public void ValidationRequestNotMatchingDocumentTypeAndActualFileBdoc() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("Valid_IDCard_MobID_signatures.bdoc"));
        post(validationRequestForDocumentExtended(encodedString, "DDOC", "Valid_IDCard_MobID_signatures.bdoc", "EE"))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("Envelope.Body.Fault.faultcode", Matchers.is(SERVER_FAULT))
                .body("Envelope.Body.Fault.faultstring", Matchers.is(DOCUMENT_MALFORMED));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
