/*
 * Copyright 2024 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timestamptoken;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;

public class TestUtils {

    public static final String TEST_FILES_LOCATION = "test-files/";
    public static final String NOT_GRANTED_WARNING = "Found a timestamp token not related to granted status."
            + " If not yet covered with a fresh timestamp token, this container might become invalid in the future.";
    public static final String POE_TIME_MESSAGE_TEXT = "The certificate is not related to a granted status at time-stamp lowest POE time!";

    public static ValidationDocument buildValidationDocument(String testFileLocation, String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(testFileLocation + testFile)
                .withName(testFile)
                .build();
    }
}
