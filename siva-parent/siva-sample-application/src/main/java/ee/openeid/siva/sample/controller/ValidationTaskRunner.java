package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.siva.SivaServiceType;
import ee.openeid.siva.sample.siva.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ValidationTaskRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationTaskRunner.class);

    private ValidationService jsonValidationService;
    private ValidationService soapValidationService;

    private Map<ValidationResultType, String> validationResults = new ConcurrentHashMap<>();

    public void run(UploadedFile uploadedFile) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> validateFile(jsonValidationService, ValidationResultType.JSON, uploadedFile));
        executorService.submit(() -> validateFile(soapValidationService, ValidationResultType.SOAP, uploadedFile));

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    private void validateFile(
            ValidationService validationService,
            ValidationResultType resultType,
            UploadedFile uploadedFile
    ) {
        try {
            String validationResult = validationService.validateDocument(uploadedFile)
                    .toBlocking()
                    .first();

            validationResults.put(resultType, validationResult);
        } catch (IOException e) {
            LOGGER.warn("JSON request uploaded file validation failed with error: {}", e.getMessage(), e);
        }
    }

    public String getValidationResult(ValidationResultType resultType) {
        return validationResults.get(resultType);
    }

    @Autowired
    @Qualifier(value = SivaServiceType.JSON_SERVICE)
    public void setJsonValidationService(final ValidationService jsonValidationService) {
        this.jsonValidationService = jsonValidationService;
    }

    @Autowired
    @Qualifier(value = SivaServiceType.SOAP_SERVICE)
    public void setSoapValidationService(ValidationService soapValidationService) {
        this.soapValidationService = soapValidationService;
    }
}
