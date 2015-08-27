package ee.sk.pdf.validator.monitoring.request;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import ee.sk.pdf.validator.monitoring.response.ResponseValidator;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import ee.sk.pdf.validator.monitoring.status.StatusRepository;
import ee.sk.pdf.validator.monitoring.status.StatusResponseBuilder;
import ee.sk.pdf.validator.monitoring.template.PDFLoader;
import ee.sk.pdf.validator.monitoring.template.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
    private MonitoringRequestConfiguration monitoringRequestConfiguration;

    @Autowired
    private TemplateLoader templateLoader;

    @Autowired
    private PDFLoader pdfLoader;

    @Autowired
    private Client client;

    @Autowired
    private ResponseValidator responseValidator;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private StatusRepository statusRepository;

    @Scheduled(fixedDelayString = "${monitoring.requestInterval}")
    public void getServiceStatus() {
        makeRequest();
        LOGGER.info("Current service status is: {}", serviceStatus);

        statusRepository.setStatusResponse(new StatusResponseBuilder()
                .withMessage(statusMessage)
                .withServiceStatus(serviceStatus)
                .withLastCheckTime(new Date())
                .getStatusResponse()
        );
    }

    private void makeRequest() {
        String fullPath = monitoringRequestConfiguration.getHost() + monitoringRequestConfiguration.getPath();
        loggingService.logInfo(LOGGER, "monitoring.startRequest", fullPath);

        ServiceStatus statusResult = ServiceStatus.OK;
        try {
            Response response = getResponse();
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
        if (isServiceFault(body)) {
            loggingService.logInfo(LOGGER, "monitoring.validationFailed");
            statusResult = ServiceStatus.WARNING;
        }

        if (shouldCheckForValidSignature(statusResult, body)) {
            statusResult =  responseValidator.validateResponse(body);
        }

        return statusResult;
    }

    private static boolean isServiceFault(String body) {
        return body.contains("<soap:Fault>");
    }

    private static boolean shouldCheckForValidSignature(ServiceStatus statusResult, String body) {
        return !Strings.isNullOrEmpty(body) && statusResult == ServiceStatus.OK;
    }

    private Response getResponse() throws ConnectException {
        WebTarget target = client
                .target(monitoringRequestConfiguration.getHost())
                .path(monitoringRequestConfiguration.getPath());

        try {
            return sendRequest(target);
        } catch (ProcessingException ex) {
            getRootCause(ex);
        }

        return null;
    }

    private static void getRootCause(Exception ex) throws ConnectException {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        Throwable rootCause = Throwables.getRootCause(ex);

        String connectionExceptionName = ConnectException.class.getName();
        if (rootCause.getClass().getName().equals(connectionExceptionName)) {
            throw (ConnectException) rootCause;
        }
    }

    private Response sendRequest(WebTarget target) {
        String requestBodyContents = templateLoader.parsedTemplate(pdfLoader.getBase64EncodedPDF());
        return target
                .request(MediaType.TEXT_XML)
                .post(Entity.entity(requestBodyContents, MediaType.TEXT_XML));
    }

}
