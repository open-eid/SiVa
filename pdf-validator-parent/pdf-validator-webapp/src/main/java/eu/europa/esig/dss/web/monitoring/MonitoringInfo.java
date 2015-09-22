package eu.europa.esig.dss.web.monitoring;

import eu.europa.esig.dss.tsl.ReloadableTrustedListCertificateSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        return tslUpdateResponse;
    }

}
