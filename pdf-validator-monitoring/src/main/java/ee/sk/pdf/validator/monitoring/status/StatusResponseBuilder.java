package ee.sk.pdf.validator.monitoring.status;

import ee.sk.pdf.validator.monitoring.response.TslStatusResponse;

import java.util.Date;

public class StatusResponseBuilder {
    private StatusResponse statusResponse = new StatusResponse();

    public StatusResponseBuilder withMessage(String message) {
        statusResponse.setStatusMessage(message);
        return this;
    }

    public StatusResponseBuilder withLastCheckTime(Date checkTime) {
        statusResponse.setLastChecked(checkTime);
        return this;
    }

    public StatusResponseBuilder withServiceStatus(ServiceStatus serviceStatus) {
        statusResponse.setServiceStatus(serviceStatus);
        return this;
    }

    public StatusResponseBuilder withTslStatus(TslStatusResponse response) {
        statusResponse.setTslStatusResponse(response);
        return this;
    }

    public StatusResponse getStatusResponse() {
        return statusResponse;
    }
}
