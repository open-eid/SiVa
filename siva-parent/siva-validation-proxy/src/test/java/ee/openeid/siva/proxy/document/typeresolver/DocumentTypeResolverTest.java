/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.proxy.document.typeresolver;

import ee.openeid.siva.proxy.document.DocumentType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocumentTypeResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConstructorIsPrivate() throws Exception {
        final Constructor<DocumentTypeResolver> constructor = DocumentTypeResolver.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

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
