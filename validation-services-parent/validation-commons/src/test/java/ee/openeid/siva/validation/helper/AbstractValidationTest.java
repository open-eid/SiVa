/*
 * Copyright 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.helper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AbstractValidationTest {

    private static ValidatorFactory validatorFactory;

    protected Validator validator;

    @BeforeAll
    public static void setUpDefaultValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @BeforeEach
    public void setUpDefaultValidator() {
        validator = validatorFactory.getValidator();
    }

    protected void validateAndExpectNoErrors(Object objectToValidate) {
        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate);
        assertThat(violations, equalTo(Set.of()));
    }

    protected void validateAndExpectOneError(Object objectToValidate, String path, String message) {
        Set<ConstraintViolation<Object>> violations = validator.validate(objectToValidate);
        assertEquals(1, violations.size(), () -> "Expected exactly one violation, found:" +
                violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(System.lineSeparator()))
        );

        ConstraintViolation<Object> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString(), equalTo(path));
        assertThat(violation.getMessage(), equalTo(message));
    }
}
