package ee.openeid.siva.sample.siva;

import lombok.Data;
import lombok.Getter;

@Data
class ServiceError {
    @Getter
    private String errorMessage;

    @Getter
    private int errorCode;

    ServiceError(final int errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
