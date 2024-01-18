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

package ee.openeid.siva.webapp.request.validation.validators;

import ee.openeid.siva.webapp.request.validation.annotations.DataSizeMin;
import org.springframework.util.unit.DataSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DataSizeMinValidator implements ConstraintValidator<DataSizeMin, DataSize> {

    private DataSize minAllowedDataSize;

    @Override
    public void initialize(DataSizeMin constraintAnnotation) {
        minAllowedDataSize = DataSize.of(constraintAnnotation.value(), constraintAnnotation.unit());
    }

    @Override
    public boolean isValid(DataSize dataSize, ConstraintValidatorContext constraintValidatorContext) {
        return (dataSize == null) || dataSize.compareTo(minAllowedDataSize) >= 0;
    }

}
