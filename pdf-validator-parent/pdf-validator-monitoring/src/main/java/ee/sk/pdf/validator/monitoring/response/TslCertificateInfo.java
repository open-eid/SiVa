package ee.sk.pdf.validator.monitoring.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TslCertificateInfo {
    // 2018-12-19 08:42:39 AM GMT
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a z");

    private String loadingDiagnosticInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date usedSigningCertificatesValidityEndDates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    private Date tslNextUpdate = null;

    public final String getLoadingDiagnosticInfo() {
        return loadingDiagnosticInfo;
    }

    public final void setLoadingDiagnosticInfo(String loadingDiagnosticInfo) {
        this.loadingDiagnosticInfo = loadingDiagnosticInfo;
    }

    @JsonProperty("signerCertificateValidUntil")
    public final Date getUsedSigningCertificatesValidityEndDates() {
        return usedSigningCertificatesValidityEndDates;
    }

    @JsonProperty("usedSigningCertificatesValidityEndDates")
    public final void setUsedSigningCertificatesValidityEndDates(String[] usedSigningCertificatesValidityEndDates) {
        try {
            this.usedSigningCertificatesValidityEndDates = dateFormat.parse(usedSigningCertificatesValidityEndDates[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @JsonProperty("tslValidUntil")
    public final Date getTslNextUpdate() {
        return tslNextUpdate;
    }

    @JsonProperty("tslNextUpdate")
    public final void setTslNextUpdate(String tslNextUpdate) {
        try {
            if (tslNextUpdate != null) {
                this.tslNextUpdate = dateFormat.parse(tslNextUpdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
