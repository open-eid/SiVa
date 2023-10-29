/*
 * Copyright 2018 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class ValidSignatureFilenameConstraintValidator implements ConstraintValidator<ValidSignatureFilename, String> {

    @Override
    public void initialize(ValidSignatureFilename validSignatureFilename) {

    }

    @Override
    public boolean isValid(String filename, ConstraintValidatorContext constraintValidatorContext) {
        String extension = extractFilenameExtension(filename);
        return extension != null && extension.equalsIgnoreCase("xml");
    }

    private static String extractFilenameExtension(String filename) {
        try {
            return FilenameUtils.getExtension(filename);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to extract signature filename extension: {}", e.getMessage());
        }
        return null;
    }

}
