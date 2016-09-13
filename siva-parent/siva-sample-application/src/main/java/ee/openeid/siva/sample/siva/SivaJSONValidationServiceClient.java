package ee.openeid.siva.sample.siva;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;

@Service(value = SivaServiceType.JSON_SERVICE)
public class SivaJSONValidationServiceClient implements ValidationService {
    private static final int GENERIC_ERROR_CODE = 101;

    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;
    private SivaValidationServiceErrorHandler errorHandler;

    @Override
    public Observable<String> validateDocument(final UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("Invalid file object given");
        }

        final String base64EncodedFile = file.getEncodedFile();

        final ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setDocument(base64EncodedFile);

        final String filename = file.getFilename();
        validationRequest.setFilename(filename);
        setValidationDocumentType(validationRequest, file);

        try {
            restTemplate.setErrorHandler(errorHandler);
            String fullUrl = properties.getServiceHost() + properties.getJsonServicePath();
            return Observable.just(restTemplate.postForObject(fullUrl, validationRequest, String.class));
        } catch (ResourceAccessException ce) {
            String errorMessage = "Connection to web service failed. Make sure You have configured SiVa web service correctly";
            return Observable.just(new ObjectMapper().writer().writeValueAsString(new ServiceError(GENERIC_ERROR_CODE, errorMessage)));
        }
    }

    private static void setValidationDocumentType(ValidationRequest validationRequest, UploadedFile uploadedFile) throws IOException {
        final FileType uploadedFileExtension = ValidationRequestUtils.getValidationServiceType(uploadedFile);
        validationRequest.setDocumentType(uploadedFileExtension);
    }

    @Autowired
    public void setProperties(SivaRESTWebServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setErrorHandler(final SivaValidationServiceErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
