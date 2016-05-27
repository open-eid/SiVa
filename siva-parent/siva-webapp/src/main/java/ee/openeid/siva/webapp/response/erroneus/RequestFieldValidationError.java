package ee.openeid.siva.webapp.response.erroneus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class RequestFieldValidationError {
    private String key;
    private String message;
}
