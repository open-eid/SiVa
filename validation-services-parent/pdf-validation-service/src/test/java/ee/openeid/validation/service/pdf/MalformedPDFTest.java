package ee.openeid.validation.service.pdf;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import eu.europa.esig.dss.MimeType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MalformedPDFTest extends PDFValidationServiceTest {

    @Test
    public void validatingAPdfWithMalformedBytesResultsInMalformedDocumentException() {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName("Some name.pdf");
        validationDocument.setBytes(Base64.decode("ZCxTgQxDET7/lNizNZ4hrB1Ug8I0kKpVDkHEgWqNjcKFMD89LsIpdCkpUEsFBgAAAAAFAAUAPgIAAEM3AAAAAA=="));

        String message = "";
        try {
            validationService.validateDocument(validationDocument);
        } catch (MalformedDocumentException e) {
            message = e.getMessage();
        }
        assertEquals("document malformed or not matching documentType", message);
    }
}
