package ee.sk.pdf.validator.monitoring.request;

import ee.sk.pdf.validator.monitoring.message.MessageService;
import ee.sk.pdf.validator.monitoring.response.ResponseValidator;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import ee.sk.pdf.validator.monitoring.status.StatusRepository;
import ee.sk.pdf.validator.monitoring.status.StatusResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.Date;

@Service
public class WsdlRequestMaker {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsdlRequestMaker.class);

    private ServiceStatus serviceStatus;
    private String statusMessage;

    private RestOperations restOperations;

    @Autowired
    private ClientService clientService;

    @Autowired
    private MonitoringRequestConfiguration monitoringRequestConfiguration;

    @Autowired
    private ResponseValidator responseValidator;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TslStatusRequestMaker tslStatusRequestMaker;

    @Scheduled(fixedDelayString = "${monitoring.requestInterval}")
    public void getServiceStatus() {
        makeRequest();
        LOGGER.info("Current service status is: {}", serviceStatus);

        statusRepository.setStatusResponse(new StatusResponseBuilder()
                .withMessage(statusMessage)
                .withServiceStatus(serviceStatus)
                .withTslStatus(tslStatusRequestMaker.getPdfValidatorTslLoadingStatus())
                .withLastCheckTime(new Date())
                .getStatusResponse()
        );
    }

    private void makeRequest() {
        String webServicePath = monitoringRequestConfiguration.getPath();
        String fullPath = monitoringRequestConfiguration.getHost() + webServicePath;
        LOGGER.info(messageService.getMessage("monitoring.startRequest", fullPath));

        ServiceStatus statusResult = ServiceStatus.OK;
        try {
            Response response = clientService.getResponse(webServicePath, MediaType.TEXT_XML);
            statusResult = response != null ? validateResponse(statusResult, response) : ServiceStatus.CRITICAL;
        } catch (ConnectException e) {
            LOGGER.info(messageService.getMessage("monitoring.requestFailed", fullPath, e.getMessage()));
            LOGGER.error("Failed to make request to PDF validator validation service endpoint", e);
            statusResult = ServiceStatus.CRITICAL;
        }

        serviceStatus = statusResult;
        statusMessage = messageService.getLastLoggedMessage();
        if (statusResult == ServiceStatus.OK) {
            statusMessage = "PDF validator service running correctly";
        }
    }

    private ServiceStatus validateResponse(ServiceStatus statusResult, Response response) {
        ServiceStatus currentServiceStatus = statusResult;
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            currentServiceStatus = ServiceStatus.CRITICAL;
        }

        String body = response.readEntity(String.class);
        if (ResponseValidator.isServiceFault(body)) {
            LOGGER.info(messageService.getMessage("monitoring.validationFailed"));
            currentServiceStatus = ServiceStatus.WARNING;
        }

        if (ResponseValidator.shouldCheckForValidSignature(currentServiceStatus, body)) {
            currentServiceStatus = responseValidator.validateResponse(body);
        }

        if (currentServiceStatus == ServiceStatus.WARNING) {
            LOGGER.info(messageService.getMessage("monitoring.pdfSimpleReportError", responseValidator.getGetErrorMessage()));
        }

        return currentServiceStatus;
    }

    @Autowired
    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }
}
