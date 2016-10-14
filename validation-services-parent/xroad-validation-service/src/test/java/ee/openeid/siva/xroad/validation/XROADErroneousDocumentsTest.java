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

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ee.openeid.siva.xroad.validation.XROADTestUtils.buildValidationDocument;
import static ee.openeid.siva.xroad.validation.XROADTestUtils.initializeXROADValidationService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class XROADErroneousDocumentsTest {

    private static final String XROAD_INVALID_DIGEST = "invalid-digest.asice";
    private static final String XROAD_VALID_SIGNED_MESSAGE = "valid-signed-message.asice";

    private XROADValidationService validationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        validationService = initializeXROADValidationService();
    }

    @Test
    public void reportWithErrorsAndIndicationTotalFailedShouldBeReturnedWhenAsicVerificationThrowsException() throws Exception {
        QualifiedReport report = validationService.validateDocument(buildValidationDocument(XROAD_INVALID_DIGEST));
        assertSignatureIsInvalidAndHasErrors(report.getSignatures().get(0));

        QualifiedReport report2 = validationService.validateDocument(buildValidationDocument(XROAD_VALID_SIGNED_MESSAGE));
        assertSignatureIsInvalidAndHasErrors(report2.getSignatures().get(0));
    }

    private void assertSignatureIsInvalidAndHasErrors(SignatureValidationData signature) {
        assertFalse(signature.getErrors().isEmpty());
        assertEquals("TOTAL-FAILED", signature.getIndication());
    }

}
