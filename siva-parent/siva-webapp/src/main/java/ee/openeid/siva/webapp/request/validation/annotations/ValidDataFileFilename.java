package ee.openeid.siva.webapp.request.validation.annotations;

import ee.openeid.siva.webapp.request.validation.validators.ValidDataFileFilenameConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidDataFileFilenameConstraintValidator.class)
public @interface ValidDataFileFilename {

    String message() default "{validation.error.message.dataFile.filename}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
