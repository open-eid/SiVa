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

    public void addFieldError(String key, String message) {
        requestErrors.add(new RequestFieldValidationError(key, message));
    }

}
