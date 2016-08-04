package ee.openeid.siva.proxy.document;

import eu.europa.esig.dss.MimeType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DocumentTypeTest {

    @Test
    public void WhenDocumentTypeIsPDFThenGetMimeTypeReturnsPDF() {
        MimeType mimeType = DocumentType.PDF.getMimeType();
        assertEquals(mimeType, MimeType.PDF);
    }

    @Test
    public void WhenDocumentTypeIsXROADThenGetMimeTypeReturnsASICE() {
        MimeType mimeType = DocumentType.XROAD.getMimeType();
        assertEquals(mimeType, MimeType.ASICE);
    }

    @Test
    public void WhenDocumentTypeIsBDOCThenGetMimeTypeReturnsASICE() {
        MimeType mimeType = DocumentType.BDOC.getMimeType();
        assertEquals(mimeType, MimeType.ASICE);
    }

    @Test
    public void WhenDocumentTypeIsDDOCThenGetMimeTypeReturnsXML() {
        MimeType mimeType = DocumentType.DDOC.getMimeType();
        assertEquals(mimeType, MimeType.XML);
    }

}
