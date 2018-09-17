package ee.openeid.siva.webapp.request.validation.annotations;

import ee.openeid.siva.webapp.request.validation.validators.ValidSignatureFilenameConstraintValidator;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Size(min = 1, max = 260)
@NotBlank
@NotNullValidFilenamePattern
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSignatureFilenameConstraintValidator.class)
public @interface ValidSignatureFilename {

    String message() default "{validation.error.message.signatureExtension}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
