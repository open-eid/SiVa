package ee.openeid.siva.sample.siva;

import lombok.Data;
import lombok.Getter;

@Data
class ServiceError {
    @Getter
    private String errorMessage;

    @Getter
    private int errorCode;

    ServiceError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
