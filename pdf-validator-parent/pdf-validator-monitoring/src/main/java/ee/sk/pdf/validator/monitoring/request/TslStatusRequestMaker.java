package ee.sk.pdf.validator.monitoring.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import ee.sk.pdf.validator.monitoring.response.TslCertificateInfo;
import ee.sk.pdf.validator.monitoring.response.TslStatusResponse;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class TslStatusRequestMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(TslStatusRequestMaker.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a z");
    private ServiceStatus serviceStatus;
    private TslStatusResponse statusResponse = new TslStatusResponse();

    @Autowired
    private MonitoringRequestConfiguration monitoringRequestConfiguration;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoggingService loggingService;

    public TslStatusResponse getPdfValidatorTslLoadingStatus() {
        makeRequest();
        statusResponse.setServiceStatus(serviceStatus);

        return statusResponse;
    }

    private void makeRequest() {
        ServiceStatus serviceStatus;
        String tslStatusPath = monitoringRequestConfiguration.getTslStatusPath();
        String fullPath = monitoringRequestConfiguration.getHost() + tslStatusPath;

        try {
            Response response = clientService.getResponse(tslStatusPath, MediaType.APPLICATION_JSON_VALUE);
            serviceStatus = response != null ? validateTslResponse(response) : ServiceStatus.CRITICAL;


        } catch (IOException e) {
            serviceStatus = ServiceStatus.CRITICAL;;
            loggingService.logError(LOGGER, "monitoring.tslRequestFailed", fullPath, e.getMessage());
        }

        setServiceStatusInfoMessages(serviceStatus);
        if (serviceStatus == ServiceStatus.OK) {
            serviceStatus = validateTslCertificates();
        }

        this.serviceStatus = serviceStatus;
    }

    private ServiceStatus validateTslCertificates() {
        String message = "Expiration WARNING(s) for: ";
        ServiceStatus serviceStatus = ServiceStatus.OK;

        for(Map.Entry<String, TslCertificateInfo> entry: statusResponse.getTslCertificateInfo().entrySet()) {
            TslCertificateInfo tslCertificateInfo = entry.getValue();

            Date signingCertificateEndDate = tslCertificateInfo.getUsedSigningCertificatesValidityEndDates();
            if (isExpired(signingCertificateEndDate)) {
                message += formatMessage(
                        " Signing certificate has expired for ",
                        entry.getKey(),
                        signingCertificateEndDate
                );

            }

            Date tslNextUpdate = tslCertificateInfo.getTslNextUpdate();
            if (isExpired(tslNextUpdate)) {
                message += formatMessage(" TSL has expired for ", entry.getKey(), tslNextUpdate);
            }

            if (isExpired(signingCertificateEndDate) || isExpired(tslNextUpdate)) {
                serviceStatus = ServiceStatus.WARNING;
            }
        }

        statusResponse.setUpdateMessage(message.replaceAll("\\s+", " ").trim());
        return serviceStatus;
    }

    private static boolean isExpired(Date date) {
        return date != null && date.before(new Date());
    }

    private String formatMessage(String helperInfo, String url, Date expirationDate) {
        return helperInfo + url + " at: " + dateFormat.format(expirationDate) + ". ";
    }

    private void setServiceStatusInfoMessages(ServiceStatus serviceStatus) {
        if (serviceStatus == ServiceStatus.CRITICAL) {
            statusResponse = new TslStatusResponse();
            statusResponse.setUpdateMessage("Checking for TSL Update status failed. Status endpoint failed to respond");
        }

        if (serviceStatus == ServiceStatus.WARNING) {
            statusResponse.setUpdateMessage("PDF Validator TSL update has not yet completed");
        }
    }

    private ServiceStatus validateTslResponse(Response response) throws IOException {
        String body = response.readEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        statusResponse = mapper.readValue(body, TslStatusResponse.class);

        return isTslUpdateNotCompleted() ? ServiceStatus.WARNING : ServiceStatus.OK;
    }

    private boolean isTslUpdateNotCompleted() {
        return statusResponse.getUpdateEndTime() == null || statusResponse.getUpdateStartTime() == null;
    }
}
