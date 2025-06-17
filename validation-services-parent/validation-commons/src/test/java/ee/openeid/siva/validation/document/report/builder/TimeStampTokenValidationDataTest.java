/*
 * Copyright 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.validation.document.report.builder;

import ee.openeid.siva.validation.document.report.Error;
import ee.openeid.siva.validation.document.report.TimeStampTokenValidationData;
import ee.openeid.siva.validation.document.report.Warning;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

public class TimeStampTokenValidationDataTest {

    @Test
    void setWarnings_WhenInputIsNull_BothGetWarningsAndGetWarningReturnNull() {
        TimeStampTokenValidationData validationData = new TimeStampTokenValidationData();

        validationData.setWarnings(null);

        assertThat(validationData.getWarnings(), nullValue());
        assertThat(validationData.getWarning(), nullValue());
    }

    @Test
    void setErrors_WhenInputIsNull_BothGetErrorsAndGetErrorReturnNull() {
        TimeStampTokenValidationData validationData = new TimeStampTokenValidationData();

        validationData.setErrors(null);

        assertThat(validationData.getErrors(), nullValue());
        assertThat(validationData.getError(), nullValue());
    }

    @Test
    void setWarnings_WhenInputIsListInstance_BothGetWarningsAndGetWarningReturnTheSameInstance() {
        TimeStampTokenValidationData validationData = new TimeStampTokenValidationData();
        List<Warning> warningsList = mockTypedList();

        validationData.setWarnings(warningsList);

        assertThat(validationData.getWarnings(), sameInstance(warningsList));
        assertThat(validationData.getWarning(), sameInstance(warningsList));
        verifyNoInteractions(warningsList);
    }

    @Test
    void setErrors_WhenInputIsListInstance_BothGetErrorsAndGetErrorReturnTheSameInstance() {
        TimeStampTokenValidationData validationData = new TimeStampTokenValidationData();
        List<Error> errorsList = mockTypedList();

        validationData.setErrors(errorsList);

        assertThat(validationData.getErrors(), sameInstance(errorsList));
        assertThat(validationData.getError(), sameInstance(errorsList));
        verifyNoInteractions(errorsList);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> mockTypedList() {
        return (List<T>) mock(List.class);
    }
}
