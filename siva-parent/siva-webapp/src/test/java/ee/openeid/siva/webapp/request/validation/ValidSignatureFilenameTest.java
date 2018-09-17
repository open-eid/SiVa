package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ValidSignatureFilenameTest {

    private static final String INVALID_FILENAME = "Invalid filename";
    private static final String INVALID_SIZE = "size must be between 1 and 260";
    private static final String INVALID_FILENAME_EXTENSION = "Invalid filename extension. Only xml files accepted.";
    private static final String MAY_NOT_BE_EMTPY = "may not be empty";
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validFilename() {
        validSignatureFilename("a.xml");
        validSignatureFilename(StringUtils.repeat('a', 260 - 4) + ".xml");
        validSignatureFilename("qwertyuiopüõasdfghjklöäzxcvbnm><1234567890+#$!}[]()-_.,;€ˇ~^'äöõü§@.xml");
        validSignatureFilename("something.xml");
    }

    @Test
    public void invalidFilename() {
        invalidSignatureFilename("", INVALID_FILENAME, INVALID_SIZE, INVALID_FILENAME_EXTENSION, MAY_NOT_BE_EMTPY);
        invalidSignatureFilename(" ", INVALID_FILENAME_EXTENSION, MAY_NOT_BE_EMTPY);
        invalidSignatureFilename(null, INVALID_FILENAME, INVALID_FILENAME_EXTENSION, MAY_NOT_BE_EMTPY);
        invalidSignatureFilename(StringUtils.repeat('a', 261), INVALID_FILENAME_EXTENSION, INVALID_SIZE);

        invalidSignatureFilename("%", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("&", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("\\", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("/", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("\"", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename(":", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("?", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("*", INVALID_FILENAME, INVALID_FILENAME_EXTENSION);

        invalidSignatureFilename("something", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.random", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.bdoc", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.ddoc", INVALID_FILENAME_EXTENSION);
        invalidSignatureFilename("something.asice", INVALID_FILENAME_EXTENSION);

        invalidSignatureFilename("&.xml", INVALID_FILENAME);

    }

    private void validSignatureFilename(String signatureFilename) {
        Set<ConstraintViolation<MockTestTarget>> violations = VALIDATOR.validate(new MockTestTarget(signatureFilename));
        printViolations(violations, signatureFilename);
        if (!violations.isEmpty()) {
            fail("Expected no validation violations, but " + violations.size() + " present!");
        }
    }

    private void invalidSignatureFilename(String signatureFilename, String... errorMessages) {
        Set<ConstraintViolation<MockTestTarget>> violations = VALIDATOR.validate(new MockTestTarget(signatureFilename));
        printViolations(violations, signatureFilename);
        assertFalse(violations.isEmpty());
        assertSame(errorMessages.length, violations.size());

        Iterator<ConstraintViolation<MockTestTarget>> iterator = violations.iterator();
        while (iterator.hasNext()) {
            ConstraintViolation<MockTestTarget> constraint = iterator.next();
            String errorMessage = constraint.getMessage();
            containsErrorMessage(errorMessage, errorMessages);
        }
    }

    private boolean containsErrorMessage(String errorMessage, String... errorMessages) {
        for (String expectedErrorMessage : errorMessages) {
            if (errorMessage.equals(expectedErrorMessage)) {
                return true;
            }
        }
        fail("Unexpected error message of '" + errorMessage + "' was thrown!");
        return false;
    }

    private void printViolations(Set<ConstraintViolation<MockTestTarget>> violations, String signatureFilename) {
        if (!violations.isEmpty()) {
            violations.forEach(v -> System.out.println("'" + signatureFilename + "' invalid: " + v.getPropertyPath() + " - " + v.getMessage()));
        }
    }

    @AllArgsConstructor
    private static class MockTestTarget {

        @ValidSignatureFilename
        String signatureFilename;
    }
}
