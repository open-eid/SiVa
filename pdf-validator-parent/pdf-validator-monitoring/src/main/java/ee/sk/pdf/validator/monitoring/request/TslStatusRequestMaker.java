package ee.sk.pdf.validator.monitoring.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import ee.sk.pdf.validator.monitoring.response.TslStatusResponse;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.io.IOException;

@Service
public class TslStatusRequestMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(TslStatusRequestMaker.class);

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

        if (serviceStatus == ServiceStatus.CRITICAL) {
            statusResponse = new TslStatusResponse();
            statusResponse.setUpdateMessage("Checking for TSL Update status failed. Status endpoint failed to respond");
        }

        if (serviceStatus == ServiceStatus.WARNING) {
            statusResponse.setUpdateMessage("PDF Validator TSL update has not yet completed");
        }

        this.serviceStatus = serviceStatus;
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
