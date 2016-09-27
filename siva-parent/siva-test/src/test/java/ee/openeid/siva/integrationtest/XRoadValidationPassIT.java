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
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
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
     * Title: Bdoc with conformant EE signature
     *
     * Expected Result: Document should pass when signature policy is set to "ee"
     *
     * File: Valid_ID_sig.bdoc
     */
    @Test
    public void validatingAnXroadDocumentShouldReturnAReport() {
        String encodedString = Base64.encodeBase64String(readFileFromTestResources("xroad-simple.asice"));
        QualifiedReport report = mapToReport(
                post(validationRequestWithValidKeys(encodedString, "xroad-simple.asice", "xroad", ""))
                        .body()
                        .asString()
        );
        assertAllSignaturesAreValid(report);
    }

}
