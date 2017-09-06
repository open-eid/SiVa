/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XRoadValidationFailIT extends SiVaRestTests {

    @Override
    protected String getTestFilesDirectory() {
        return "xroad/";
    }

    /**
     * TestCaseID: Xroad-ValidationFail-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv3-polv5
     *
     * Title: Simple xroad document with container fault
     *
     * Expected Result: Document should fail validation
     *
     * File: invalid-digest.asice
     */
    @Test
    public void validatingInvalidSimpleXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("invalid-digest.asice"));
        post(validationRequestWithDocumentTypeValidKeys(encodedString, "invalid-digest.asice", "xroad", VALID_SIGNATURE_POLICY_3))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("signatures[0].errors.content", Matchers.hasItem("MissingHeaderField: Required field 'protocolVersion' is missing"))
                .body("validSignaturesCount", Matchers.is(0))
                .body("signaturesCount", Matchers.is(1));
    }


}
