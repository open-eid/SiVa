package ee.sk.pdf.validator.monitoring.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties({"tslLastUpdate"})
public class StatusResponse {
    private ServiceStatus serviceStatus;
    private String statusMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date lastChecked;
    private Date tslLastUpdate;

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    public Date getTslLastUpdate() {
        return tslLastUpdate;
    }

    public void setTslLastUpdate(Date tslLastUpdate) {
        this.tslLastUpdate = tslLastUpdate;
    }
}
