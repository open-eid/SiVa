package ee.sk.pdf.validator.monitoring.request;

import ee.sk.pdf.validator.monitoring.logging.LoggingService;
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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;
import java.util.Date;

@Service
public class WsdlRequestMaker {
    private final static Logger LOGGER = LoggerFactory.getLogger(WsdlRequestMaker.class);

    private ServiceStatus serviceStatus;
    private String statusMessage;

    @Autowired
    private ClientService clientService;

    @Autowired
    private MonitoringRequestConfiguration monitoringRequestConfiguration;

    @Autowired
    private ResponseValidator responseValidator;

    @Autowired
    private LoggingService loggingService;

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
        loggingService.logInfo(LOGGER, "monitoring.startRequest", fullPath);

        ServiceStatus statusResult = ServiceStatus.OK;
        try {
            Response response = clientService.getResponse(webServicePath, MediaType.TEXT_XML);
            statusResult = response != null ? validateResponse(statusResult, response) : ServiceStatus.CRITICAL;
        } catch (ConnectException e) {
            loggingService.logInfo(LOGGER, "monitoring.requestFailed", fullPath, e.getMessage());
            statusResult = ServiceStatus.CRITICAL;
        }

        serviceStatus = statusResult;
        statusMessage = loggingService.getLastLoggedMessage();
        if (statusResult == ServiceStatus.OK) {
            statusMessage = "PDF validator service running correctly";
        }
    }

    private ServiceStatus validateResponse(ServiceStatus statusResult, Response response) {
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            statusResult = ServiceStatus.CRITICAL;
        }

        String body = response.readEntity(String.class);
        if (ResponseValidator.isServiceFault(body)) {
            loggingService.logInfo(LOGGER, "monitoring.validationFailed");
            statusResult = ServiceStatus.WARNING;
        }

        if (ResponseValidator.shouldCheckForValidSignature(statusResult, body)) {
            statusResult =  responseValidator.validateResponse(body);
        }

        if (statusResult == ServiceStatus.WARNING) {
            loggingService.logInfo(LOGGER, "monitoring.pdfSimpleReportError", responseValidator.getGetErrorMessage());
        }

        return statusResult;
    }

}
