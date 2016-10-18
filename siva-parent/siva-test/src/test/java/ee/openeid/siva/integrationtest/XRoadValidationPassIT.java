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
public class XRoadValidationPassIT extends SiVaRestTests {

    @Override
    protected String getTestFilesDirectory() {
        return "xroad/";
    }

    /**
     * TestCaseID: Xroad-ValidationPass-1
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Simple xroad document
     *
     * Expected Result: Document should pass
     *
     * File: xroad-simple.asice
     */
    @Test
    public void validatingSimpleXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "xroad", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_LT"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xroad-ValidationPass-2
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Batchsignature xroad document
     *
     * Expected Result: Document should pass
     *
     * File: xroad-batchsignature.asice
     */
    @Test
    public void validatingBatchXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-batchsignature.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-batchsignature.asice", "xroad", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

    /**
     * TestCaseID: Xroad-ValidationPass-3
     *
     * TestType: Automated
     *
     * Requirement: http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#common-validation-constraints-polv1-polv2
     *
     * Title: Attachment xroad document
     *
     * Expected Result: Document should pass
     *
     * File: xroad-attachment.asice
     */
    @Test
    public void validatingAttachXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-attachment.asice"));
        post(validationRequestWithValidKeys(encodedString, "xroad-attachment.asice", "xroad", VALID_SIGNATURE_POLICY_1))
                .then()
                .body("signatures[0].signatureFormat", Matchers.is("XAdES_BASELINE_B_BES"))
                .body("signatures[0].indication", Matchers.is("TOTAL-PASSED"))
                .body("signatures[0].subIndication", Matchers.is(""))
                .body("signatures[0].errors.content", Matchers.hasSize(0))
                .body("validSignaturesCount", Matchers.is(1))
                .body("signaturesCount", Matchers.is(1));
    }

}
