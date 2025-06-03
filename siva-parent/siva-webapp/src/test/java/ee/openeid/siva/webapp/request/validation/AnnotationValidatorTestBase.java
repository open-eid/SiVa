/*
 * Copyright 2019 - 2025 Riigi Infosüsteemi Amet
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class AnnotationValidatorTestBase {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    void assertNoViolations(TestClassWithAnnotatedFields testObject, String testedValue) {
        Set<ConstraintViolation<TestClassWithAnnotatedFields>> violations = VALIDATOR.validate(testObject);
        printViolations(violations, testedValue);
        if (!violations.isEmpty()) {
            fail("Expected no validation violations, but " + violations.size() + " present!");
        }
    }

    void assertViolations(TestClassWithAnnotatedFields testObject, String testedValue, String... errorMessages) {
        Set<ConstraintViolation<TestClassWithAnnotatedFields>> violations = VALIDATOR.validate(testObject);
        printViolations(violations, testedValue);
        assertFalse(violations.isEmpty());
        assertSame(errorMessages.length, violations.size());

        Iterator<ConstraintViolation<TestClassWithAnnotatedFields>> iterator = violations.iterator();
        while (iterator.hasNext()) {
            ConstraintViolation<TestClassWithAnnotatedFields> constraint = iterator.next();
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

    private void printViolations(Set<ConstraintViolation<TestClassWithAnnotatedFields>> violations, String testedValue) {
        if (!violations.isEmpty()) {
            violations.forEach(v -> System.out.println("'" + testedValue + "' invalid: " + v.getPropertyPath() + " - " + v.getMessage()));
        }
    }

    interface TestClassWithAnnotatedFields {}
}
