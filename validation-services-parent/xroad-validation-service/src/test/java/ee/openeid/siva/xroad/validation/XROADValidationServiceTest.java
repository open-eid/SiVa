package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ee.openeid.siva.xroad.validation.XROADTestUtils.*;
import static ee.openeid.siva.xroad.validation.XROADTestUtils.buildValidationDocument;

public class XROADValidationServiceTest {

    private static final String BDOC_TS_VALID = "BDOC-TS.bdoc";
    private static final String DDOC_VALID = "ddoc_valid_2_signatures.ddoc";
    private static final String PDF_VALID = "hellopades-pades-lt-sha256-sign.pdf";

    private static String DOCUMENT_MALFORMED_MESSAGE = "document malformed or not matching documentType";

    private XROADValidationService validationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        validationService = initializeXROADValidationService();
    }

    @Test
    public void validatingInvalidBase64EncodedDocumentResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = new ValidationDocument();
        doc.setDataBase64Encoded("ASDASDAFGOAGMRASGMASPÖGLMOP");
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidBdocTSWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(BDOC_TS_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidDDocWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(DDOC_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        validationService.validateDocument(doc);
    }

    @Test
    public void validatingValidPdfWithXroadValidatorResultsInMalformedDocumentException() throws Exception {
        ValidationDocument doc = buildValidationDocument(PDF_VALID);
        expectedException.expect(MalformedDocumentException.class);
        expectedException.expectMessage(DOCUMENT_MALFORMED_MESSAGE);
        validationService.validateDocument(doc);
    }
}
