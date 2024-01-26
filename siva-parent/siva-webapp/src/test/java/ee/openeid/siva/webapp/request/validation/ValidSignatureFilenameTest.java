/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ValidSignatureFilenameTest extends AnnotationValidatorTestBase {

    private static final String INVALID_FILENAME = "Invalid filename";
    private static final String INVALID_SIZE = "size must be between 1 and 260";
    private static final String INVALID_FILENAME_EXTENSION = "Invalid filename extension. Only xml files accepted.";
    private static final String MUST_NOT_BE_BLANK = "must not be blank";

    @Test
    void validFilename() {
        validSignatureFilename("a.xml");
        validSignatureFilename(StringUtils.repeat('a', 260 - 4) + ".xml");
        validSignatureFilename("qwertyuiopüõasdfghjklöäzxcvbnm><1234567890+#$!}[]()-_.,;€ˇ~^'äöõü§@.xml");
        validSignatureFilename("something.xml");
    }

    @Test
    void invalidFilename() {
        invalidSignatureFilename("", INVALID_SIZE, INVALID_FILENAME_EXTENSION, MUST_NOT_BE_BLANK);
        invalidSignatureFilename(" ", INVALID_FILENAME_EXTENSION, MUST_NOT_BE_BLANK);
        invalidSignatureFilename(null, INVALID_FILENAME, INVALID_FILENAME_EXTENSION, MUST_NOT_BE_BLANK);
        invalidSignatureFilename(StringUtils.repeat('a', 261), INVALID_FILENAME_EXTENSION, INVALID_SIZE);

        invalidSignatureFilename("%", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("&", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("\\", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("/", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("\"", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename(":", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("?", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("*", INVALID_FILENAME_EXTENSION);

        invalidSignatureFilename("something", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.random", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.bdoc", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.ddoc", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.asice", INVALID_FILENAME_EXTENSION);
    }

    private void validSignatureFilename(String signatureFilename) {
        assertNoViolations(new MockTestTarget(signatureFilename), signatureFilename);
    }

    private void invalidSignatureFilename(String signatureFilename, String... errorMessages) {
        assertViolations(new MockTestTarget(signatureFilename), signatureFilename, errorMessages);
    }

    @AllArgsConstructor
    class MockTestTarget implements TestClassWithAnnotatedFields {

        @ValidSignatureFilename
        String signatureFilename;
    }
}
