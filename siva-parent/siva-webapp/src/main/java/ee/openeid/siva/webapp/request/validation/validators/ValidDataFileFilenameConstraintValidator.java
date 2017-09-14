package ee.openeid.siva.webapp.request.validation.validators;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.webapp.request.validation.annotations.ValidDataFileFilename;
import org.apache.commons.io.FilenameUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidDataFileFilenameConstraintValidator implements ConstraintValidator<ValidDataFileFilename, String> {

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
