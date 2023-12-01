/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.webapp.request.validation.annotations.ValidDataFileFilename;
import org.apache.commons.io.FilenameUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDataFileFilenameConstraintValidator implements ConstraintValidator<ValidDataFileFilename, String> {

    @Override
    public void initialize(ValidDataFileFilename constraint) {
    }

    public boolean isValid(String filename, ConstraintValidatorContext context) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension == null)
            return false;
        DocumentType documentType = DocumentType.documentTypeFromString(extension.toUpperCase());
        return documentType != null && documentType == DocumentType.DDOC;
    }
}
