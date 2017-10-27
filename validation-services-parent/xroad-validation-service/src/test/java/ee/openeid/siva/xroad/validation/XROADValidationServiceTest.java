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

package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.validation.service.xroad.XROADValidationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ee.openeid.siva.xroad.validation.XROADTestUtils.*;

public class XROADValidationServiceTest {

    private static final String BDOC_TS_VALID = "BDOC-TS.bdoc";
    private static final String DDOC_VALID = "ddoc_valid_2_signatures.ddoc";
    private static final String PDF_VALID = "hellopades-pades-lt-sha256-sign.pdf";

    private static String DOCUMENT_MALFORMED_MESSAGE = "Document malformed or not matching documentType";

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
        doc.setBytes("ASDASDAFGOAGMRASGMASPÖGLMOP".getBytes());
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
