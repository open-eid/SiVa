/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.openeid.siva.sample.controller.ResultType.JSON;
import static ee.openeid.siva.sample.controller.ResultType.SOAP;

@Service
class ValidationTaskRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationTaskRunner.class);
    private final Map<ResultType, String> validationResults = new ConcurrentHashMap<>();
    private ValidationService jsonValidationService;
    private ValidationService soapValidationService;

    void run(String policy, String report, UploadedFile uploadedFile) throws InterruptedException {
        Map<ResultType, ValidationService> serviceMap = getValidationServiceMap();

        ExecutorService executorService = Executors.newFixedThreadPool(serviceMap.size());
        serviceMap.entrySet().forEach(entry -> executorService.submit(() -> validateFile(entry.getValue(), entry.getKey(), report, uploadedFile, policy)));

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    private static SimpleImmutableEntry<ResultType, ValidationService> addEntry(
            ResultType resultType,
            ValidationService validationService
    ) {
        return new SimpleImmutableEntry<>(resultType, validationService);
    }

    private Map<ResultType, ValidationService> getValidationServiceMap() {
        return Stream.of(addEntry(JSON, jsonValidationService), addEntry(SOAP, soapValidationService))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void validateFile(
            ValidationService validationService,
            ResultType resultType,
            String report,
            UploadedFile uploadedFile,
            String policy
    ) {
        try {
            String validationResult = validationService.validateDocument(policy, report,uploadedFile);
            validationResults.put(resultType, validationResult);
        } catch (IOException e) {
            LOGGER.warn("Uploaded file validation failed with error: {}", e.getMessage(), e);
        }
    }

    String getValidationResult(ResultType resultType) {
        return validationResults.get(resultType);
    }

    void clearValidationResults() {
        validationResults.clear();
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
