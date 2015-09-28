package eu.europa.esig.dss.web.monitoring;

import eu.europa.esig.dss.tsl.ReloadableTrustedListCertificateSource;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class MonitoringInfo {

    @Autowired
    private ReloadableTrustedListCertificateSource reloadableTrustedListCertificateSource;

    @RequestMapping(value = "/tsl", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TslUpdateResponse getTlsUpdateInfo() {
        TslUpdateResponse tslUpdateResponse = new TslUpdateResponse();
        tslUpdateResponse.setUpdateEndTime(reloadableTrustedListCertificateSource.getUpdateEndTime());
        tslUpdateResponse.setUpdateStartTime(reloadableTrustedListCertificateSource.getUpdateStartDate());
        tslUpdateResponse.setUpdateMessage(reloadableTrustedListCertificateSource.getTslUpdateMessage());
        tslUpdateResponse.setDiagnosticInfoPerTslUrls(buildJson(reloadableTrustedListCertificateSource.getDiagnosticInfoExtended()));

        return tslUpdateResponse;
    }

    protected Map<String, TslDiagnosticInfo> buildJson(Map<String, TrustedListsCertificateSource.DiagnosticInfo> diagnosticInfoPerTslUrls) {
        Map<String, TslDiagnosticInfo> result = new LinkedHashMap<>();
        for (Map.Entry<String, TrustedListsCertificateSource.DiagnosticInfo> entry : diagnosticInfoPerTslUrls.entrySet()) {
            result.put(entry.getKey(), new TslDiagnosticInfo(entry.getValue()));
        }
        return result;
    }

}
