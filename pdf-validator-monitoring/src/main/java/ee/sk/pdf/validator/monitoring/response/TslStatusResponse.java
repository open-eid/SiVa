package ee.sk.pdf.validator.monitoring.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;

import java.util.Date;

public class TslStatusResponse {
    private ServiceStatus serviceStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateEndTime;
    private String updateMessage;

    public Date getUpdateStartTime() {
        return updateStartTime;
    }

    @SuppressWarnings("unused")
    public void setUpdateStartTime(Date updateStartTime) {
        this.updateStartTime = updateStartTime;
    }

    public Date getUpdateEndTime() {
        return updateEndTime;
    }

    @SuppressWarnings("unused")
    public void setUpdateEndTime(Date updateEndTime) {
        this.updateEndTime = updateEndTime;
    }

    @SuppressWarnings("unused")
    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    @SuppressWarnings("unused")
    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}
