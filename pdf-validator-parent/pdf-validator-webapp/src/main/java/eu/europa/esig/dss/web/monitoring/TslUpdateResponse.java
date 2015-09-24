package eu.europa.esig.dss.web.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Map;

public class TslUpdateResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date updateEndTime;

    private String updateMessage;

    private Map<String, TslDiagnosticInfo> diagnosticInfoPerTslUrls;

    @SuppressWarnings("unused")
    public Date getUpdateStartTime() {
        return updateStartTime;
    }

    public void setUpdateStartTime(Date updateStartTime) {
        this.updateStartTime = updateStartTime;
    }

    @SuppressWarnings("unused")
    public Date getUpdateEndTime() {
        return updateEndTime;
    }

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
    public Map<String, TslDiagnosticInfo> getDiagnosticInfoPerTslUrls() {
        return diagnosticInfoPerTslUrls;
    }

    public void setDiagnosticInfoPerTslUrls(Map<String, TslDiagnosticInfo> diagnosticInfoPerTslUrls) {
        this.diagnosticInfoPerTslUrls = diagnosticInfoPerTslUrls;
    }
}

