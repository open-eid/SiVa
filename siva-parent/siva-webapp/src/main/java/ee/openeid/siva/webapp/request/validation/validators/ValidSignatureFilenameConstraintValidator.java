package ee.openeid.siva.webapp.request.validation.validators;

import ee.openeid.siva.webapp.request.validation.annotations.ValidSignatureFilename;
import org.apache.commons.io.FilenameUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidSignatureFilenameConstraintValidator implements ConstraintValidator<ValidSignatureFilename, String> {

    @Override
    public void initialize(ValidSignatureFilename validSignatureFilename) {

    }

    @Override
    public boolean isValid(String filename, ConstraintValidatorContext constraintValidatorContext) {
        String extension = FilenameUtils.getExtension(filename);
        return extension != null && extension.equalsIgnoreCase("xml");
    }

}
