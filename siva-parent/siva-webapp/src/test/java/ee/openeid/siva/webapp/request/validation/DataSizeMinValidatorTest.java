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

package ee.openeid.siva.webapp.request.validation;

import ee.openeid.siva.webapp.request.validation.annotations.DataSizeMin;
import ee.openeid.siva.webapp.request.validation.validators.DataSizeMinValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import jakarta.validation.ConstraintValidatorContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class DataSizeMinValidatorTest {

    @InjectMocks
    private DataSizeMinValidator dataSizeMinValidator;

    @Mock
    private DataSizeMin dataSizeMinAnnotation;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @ParameterizedTest
    @EnumSource(DataUnit.class)
    void isValid_WhenDataSizeIsNull_ReturnsTrue(DataUnit unit) {
        initializeAnnotatedLimitAndValidator(0L, unit);

        boolean result = dataSizeMinValidator.isValid(null, constraintValidatorContext);

        assertThat(result, equalTo(true));
        verifyAllInteractions();
    }

    @ParameterizedTest
    @EnumSource(DataUnit.class)
    void isValid_WhenDataSizeIsEqualToAnnotatedLimit_ReturnsTrue(DataUnit unit) {
        initializeAnnotatedLimitAndValidator(7L, unit);
        DataSize dataSize = DataSize.of(7L, unit);

        boolean result = dataSizeMinValidator.isValid(dataSize, constraintValidatorContext);

        assertThat(result, equalTo(true));
        verifyAllInteractions();
    }

    @ParameterizedTest
    @EnumSource(DataUnit.class)
    void isValid_WhenDataSizeIsGreaterThanAnnotatedLimit_ReturnsTrue(DataUnit unit) {
        initializeAnnotatedLimitAndValidator(7L, unit);
        DataSize dataSize = DataSize.ofBytes(
                DataSize.of(7L, unit).toBytes() + 1L
        );

        boolean result = dataSizeMinValidator.isValid(dataSize, constraintValidatorContext);

        assertThat(result, equalTo(true));
        verifyAllInteractions();
    }

    @ParameterizedTest
    @EnumSource(DataUnit.class)
    void isValid_WhenDataSizeIsLessThanAnnotatedLimit_ReturnsFalse(DataUnit unit) {
        initializeAnnotatedLimitAndValidator(7L, unit);
        DataSize dataSize = DataSize.ofBytes(
                DataSize.of(7L, unit).toBytes() - 1L
        );

        boolean result = dataSizeMinValidator.isValid(dataSize, constraintValidatorContext);

        assertThat(result, equalTo(false));
        verifyAllInteractions();
    }

    private void initializeAnnotatedLimitAndValidator(long value, DataUnit unit) {
        doReturn(value).when(dataSizeMinAnnotation).value();
        doReturn(unit).when(dataSizeMinAnnotation).unit();

        dataSizeMinValidator.initialize(dataSizeMinAnnotation);
    }

    private void verifyAllInteractions() {
        verify(dataSizeMinAnnotation).value();
        verify(dataSizeMinAnnotation).unit();

        verifyNoMoreInteractions(dataSizeMinAnnotation);
        verifyNoInteractions(constraintValidatorContext);
    }
}
