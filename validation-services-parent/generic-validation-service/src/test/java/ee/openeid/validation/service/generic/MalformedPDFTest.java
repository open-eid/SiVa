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

package ee.openeid.validation.service.generic;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MalformedPDFTest extends PDFValidationServiceTest {

    @Test
    @Ignore("DD4J to DSS ")
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
        assertEquals("Document malformed or not matching documentType", message);
    }
}
