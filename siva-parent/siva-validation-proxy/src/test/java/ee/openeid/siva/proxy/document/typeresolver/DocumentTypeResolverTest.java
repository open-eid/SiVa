package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.DocumentType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class DocumentTypeResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void pdfTypeReturnsPdfMimeType() {
        DocumentType documentType = DocumentTypeResolver.documentTypeFromString("pdf");
        assertEquals(documentType, DocumentType.PDF);
    }

    @Test
    public void ignoreCaseForType() {
        DocumentType documentType = DocumentTypeResolver.documentTypeFromString("PdF");
        assertEquals(documentType, DocumentType.PDF);
        documentType = DocumentTypeResolver.documentTypeFromString("PDF");
        assertEquals(documentType, DocumentType.PDF);
        documentType = DocumentTypeResolver.documentTypeFromString("pdf");
        assertEquals(documentType, DocumentType.PDF);
    }

    @Test
    public void unsupportedTypeThrowsException() throws Exception {
        String unsupportedType = "some not supported type";
        expectedException.expect(UnsupportedTypeException.class);
        expectedException.expectMessage("type = " + unsupportedType + " is unsupported");
        DocumentTypeResolver.documentTypeFromString(unsupportedType);
    }

    @Test
    public void extraWhiteSpaceInOtherwiseSupportedTypeIsIllegal() {
        String supportedTypeWithExtraWhiteSpace = " pdf   ";
        expectedException.expect(UnsupportedTypeException.class);
        expectedException.expectMessage("type = " + supportedTypeWithExtraWhiteSpace + " is unsupported");
        DocumentTypeResolver.documentTypeFromString(supportedTypeWithExtraWhiteSpace);
    }
}
