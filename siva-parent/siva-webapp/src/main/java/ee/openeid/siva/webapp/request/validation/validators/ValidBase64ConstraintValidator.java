package ee.openeid.siva.webapp.request.validation.validators;

import ee.openeid.siva.webapp.request.validation.annotations.ValidBase64String;
import org.apache.commons.codec.binary.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidBase64ConstraintValidator implements ConstraintValidator<ValidBase64String, String> {

   public void initialize(ValidBase64String constraint) {
   }

   public boolean isValid(String obj, ConstraintValidatorContext context) {
       return Base64.isBase64(obj);
   }
}
