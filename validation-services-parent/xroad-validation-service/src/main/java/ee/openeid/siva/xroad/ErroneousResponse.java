package ee.openeid.siva.xroad;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErroneousResponse {

    private String key;
    private String message;
}
