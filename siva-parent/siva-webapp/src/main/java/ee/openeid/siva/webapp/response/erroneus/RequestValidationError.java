package ee.openeid.siva.webapp.response.erroneus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@ToString
public class RequestValidationError {

    @Getter
    private final List<RequestFieldValidationError> requestErrors = new ArrayList<>();

    public void addFieldError(RequestFieldValidationError fieldError) {
        requestErrors.add(fieldError);
    }

    public void addFieldError(String field, String message) {
        requestErrors.add(new RequestFieldValidationError(field, message));
    }

}
