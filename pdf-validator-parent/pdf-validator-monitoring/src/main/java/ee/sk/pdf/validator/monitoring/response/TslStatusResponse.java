package ee.sk.pdf.validator.monitoring.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;

import java.util.Date;
import java.util.Map;

public class TslStatusResponse {
    private ServiceStatus serviceStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateEndTime;
    private String updateMessage;

    private Map<String, TslCertificateInfo> tslCertificateInfo;

    public final Date getUpdateStartTime() {
        return updateStartTime;
    }

    @SuppressWarnings("unused")
    public final void setUpdateStartTime(Date updateStartTime) {
        this.updateStartTime = updateStartTime;
    }

    public final Date getUpdateEndTime() {
        return updateEndTime;
    }

    @SuppressWarnings("unused")
    public final void setUpdateEndTime(Date updateEndTime) {
        this.updateEndTime = updateEndTime;
    }

    @SuppressWarnings("unused")
    public final String getUpdateMessage() {
        return updateMessage;
    }

    public final void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    @SuppressWarnings("unused")
    public final ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public final void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @JsonProperty("tslCertificateInfo")
    public final Map<String, TslCertificateInfo> getTslCertificateInfo() {
        return tslCertificateInfo;
    }

    @JsonProperty("diagnosticInfoPerTslUrls")
    public final void setTslCertificateInfo(Map<String, TslCertificateInfo> tslCertificateInfo) {
        this.tslCertificateInfo = tslCertificateInfo;
    }
}
