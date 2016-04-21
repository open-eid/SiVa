package ee.openeid.siva.webapp.request.validation.json;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class AcceptValuesConstraintValidator implements ConstraintValidator<AcceptValues, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(AcceptValues values) {
        acceptedValues = new ArrayList<>();
        values.value().getAcceptedValues().forEach(av -> acceptedValues.add(av.toLowerCase()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return acceptedValues.contains(value.toLowerCase());
    }
}
