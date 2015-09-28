package ee.sk.pdf.validator.monitoring.request;

import com.google.common.base.Throwables;
import ee.sk.pdf.validator.monitoring.template.PDFLoader;
import ee.sk.pdf.validator.monitoring.template.TemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

@Service
public class ClientService {
    @Autowired
    private TemplateLoader templateLoader;

    @Autowired
    private PDFLoader pdfLoader;

    @Autowired
    private Client client;

    @Autowired
    private MonitoringRequestConfiguration monitoringRequestConfiguration;

    public Response getResponse(String path, String mediaType) throws ConnectException {
        WebTarget target = client
                .target(monitoringRequestConfiguration.getHost())
                .path(path);

        try {
            return mediaType.equals(MediaType.TEXT_XML) ? sendRequest(target) : sendJsonRequest(target);
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

    private static Response sendJsonRequest(WebTarget target) {
        return target.request().get();
    }

    private Response sendRequest(WebTarget target) {
        String requestBodyContents = templateLoader.parsedTemplate(pdfLoader.getBase64EncodedPDF());
        return target
                .request(MediaType.TEXT_XML)
                .post(Entity.entity(requestBodyContents, MediaType.TEXT_XML));
    }

}
