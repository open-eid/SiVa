package eu.europa.esig.dss.web.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;

import java.util.Date;
import java.util.List;

public class TslDiagnosticInfo {
    private String loadingDiagnosticInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private List<Date> usedSigningCertificatesValidityEndDates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date tslNextUpdate;

    public TslDiagnosticInfo(TrustedListsCertificateSource.DiagnosticInfo diagnosticInfo) {
        this.loadingDiagnosticInfo = diagnosticInfo.getLoadingDiagnosticInfo();
        this.usedSigningCertificatesValidityEndDates = diagnosticInfo.getUsedSigningCertificatesValidityEndDates();
        this.tslNextUpdate = diagnosticInfo.getTslNextUpdate();
    }

    public String getLoadingDiagnosticInfo() {
        return loadingDiagnosticInfo;
    }

    public List<Date> getUsedSigningCertificatesValidityEndDates() {
        return usedSigningCertificatesValidityEndDates;
    }

    public Date getTslNextUpdate() {
        return tslNextUpdate;
    }
}
