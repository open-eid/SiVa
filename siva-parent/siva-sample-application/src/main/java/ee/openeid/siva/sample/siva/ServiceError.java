package ee.openeid.siva.sample.siva;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class ServiceError {
    @Getter
    private int errorCode;

    @Getter
    private String errorMessage;
}
