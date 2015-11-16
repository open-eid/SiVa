package ee.sk.pdf.validator.monitoring.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TslCertificateInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(TslCertificateInfo.class);

    // Example: 2018-12-19 08:42:39 AM GMT
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss a z";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    private String loadingDiagnosticInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date usedSigningCertificatesValidityEndDates;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date tslNextUpdate = null;

    @SuppressWarnings("unused")
    public final String getLoadingDiagnosticInfo() {
        return loadingDiagnosticInfo;
    }

    @SuppressWarnings("unused")
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
            LOGGER.warn(
                "Failed parsing date for UsedSigningCertificatesValidityEndDates with error message: {}",
                e.getMessage()
            );
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
            LOGGER.warn("Failed parsing date for TslNextUpdate with error message: ", e.getMessage());
        }
    }
}
