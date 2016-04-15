package ee.openeid.siva.webapp.mimetype;

import eu.europa.esig.dss.MimeType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class MimeTypeResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void pdfTypeReturnsPdfMimeType() {
        MimeType mimeType = MimeTypeResolver.mimeTypeFromString("pdf");
        assertEquals(mimeType, MimeType.PDF);
    }

    @Test
    public void ignoreCaseForType() {
        MimeType mimeType = MimeTypeResolver.mimeTypeFromString("PdF");
        assertEquals(mimeType, MimeType.PDF);
        mimeType = MimeTypeResolver.mimeTypeFromString("PDF");
        assertEquals(mimeType, MimeType.PDF);
        mimeType = MimeTypeResolver.mimeTypeFromString("pdf");
        assertEquals(mimeType, MimeType.PDF);
    }

    @Test
    public void unsupportedTypeThrowsException() throws Exception {
        String unsupportedType = "some not supported type";
        expectedException.expect(MimeTypeResolver.UnsupportedTypeException.class);
        expectedException.expectMessage("type = " + unsupportedType + " is unsupported");
        MimeTypeResolver.mimeTypeFromString(unsupportedType);
    }

    @Test
    public void extraWhiteSpaceInOtherwiseSupportedTypeIsIllegal() {
        String supportedTypeWithExtraWhiteSpace = " pdf   ";
        expectedException.expect(MimeTypeResolver.UnsupportedTypeException.class);
        expectedException.expectMessage("type = " + supportedTypeWithExtraWhiteSpace + " is unsupported");
        MimeTypeResolver.mimeTypeFromString(supportedTypeWithExtraWhiteSpace);
    }
}
