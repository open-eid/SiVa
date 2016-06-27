package ee.openeid.siva.sample.siva;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.sample.configuration.SivaConfigurationProperties;
import ee.openeid.siva.sample.controller.ValidationServiceUtils;
import ee.openeid.siva.sample.upload.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SivaValidationService {
    private static final int GENERIC_ERROR_CODE = 101;

    private SivaConfigurationProperties properties;
    private RestTemplate restTemplate;
    private SivaValidationServiceErrorHandler errorHandler;

    public String validateDocument(final UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("Invalid file object given");
        }

        final String base64EncodedFile = file.getEncodedFile();

        final ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setDocument(base64EncodedFile);

        final String filename = file.getFilename();
        validationRequest.setFilename(filename);
        setValidationDocumentType(validationRequest);

        try {
            restTemplate.setErrorHandler(errorHandler);
            return restTemplate.postForObject(properties.getServiceUrl(), validationRequest, String.class);
        } catch (ResourceAccessException ce) {
            String errorMessage = "Connection to web service failed. Make sure You have configured SiVa web service correctly";
            return new ObjectMapper().writer().writeValueAsString(new ServiceError(GENERIC_ERROR_CODE, errorMessage));
        }
    }

    private static void setValidationDocumentType(final ValidationRequest validationRequest) {
        final FileType uploadedFileExtension = ValidationServiceUtils.getValidationServiceType(validationRequest);
        validationRequest.setDocumentType(uploadedFileExtension);
    }

    @Autowired
    public void setProperties(SivaConfigurationProperties properties) {
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
