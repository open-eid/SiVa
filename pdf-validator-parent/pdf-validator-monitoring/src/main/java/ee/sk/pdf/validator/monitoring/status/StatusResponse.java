package ee.sk.pdf.validator.monitoring.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import ee.sk.pdf.validator.monitoring.response.TslStatusResponse;

import java.util.Date;

public class StatusResponse {
    private ServiceStatus serviceStatus;
    private String statusMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date lastChecked;

    private TslStatusResponse tslStatusResponse;

    @SuppressWarnings("unused")
    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @SuppressWarnings("unused")
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @SuppressWarnings("unused")
    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    @SuppressWarnings("unused")
    public TslStatusResponse getTslStatusResponse() {
        return tslStatusResponse;
    }

    public void setTslStatusResponse(TslStatusResponse tslStatusResponse) {
        this.tslStatusResponse = tslStatusResponse;
    }
}
