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
import ee.openeid.siva.sample.siva.DataFilesService;
import ee.openeid.siva.sample.siva.SivaServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataFilesTaskRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFilesTaskRunner.class);

    private DataFilesService jsonDataFilesService;
    private DataFilesService soapDataFilesService;

    private final Map<ResultType, String> dataFilesResults = new ConcurrentHashMap<>();

    public void run(UploadedFile uploadedFile) throws InterruptedException {
        Map<ResultType, DataFilesService> serviceMap = getDataFilesServiceMap();

        ExecutorService executorService = Executors.newFixedThreadPool(serviceMap.size());
        serviceMap.forEach((key, value) -> executorService.submit(() -> getDataFiles(value, key, uploadedFile)));

        executorService.shutdown();
        executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    private Map<ResultType, DataFilesService> getDataFilesServiceMap() {
        return Stream.of(addEntry(ResultType.JSON, jsonDataFilesService), addEntry(ResultType.SOAP, soapDataFilesService))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static AbstractMap.SimpleImmutableEntry<ResultType, DataFilesService> addEntry(
            ResultType resultType,
            DataFilesService dataFilesService
    ) {
        return new AbstractMap.SimpleImmutableEntry<>(resultType, dataFilesService);
    }

    private void getDataFiles(
            DataFilesService dataFilesService,
            ResultType resultType,
            UploadedFile uploadedFile
    ) {
        try {
            String dataFilesResult = dataFilesService.getDataFiles(uploadedFile)
                    .toBlocking()
                    .first();

            dataFilesResults.put(resultType, dataFilesResult);
        } catch (IOException e) {
            LOGGER.warn("Uploaded file data files extraction failed with error: {}", e.getMessage(), e);
        }
    }

    public String getDataFilesResult(ResultType resultType) {
        return dataFilesResults.get(resultType);
    }

    @Autowired
    @Qualifier(value = SivaServiceType.JSON_DATAFILES_SERVICE)
    public void setJsonDataFilesService(final DataFilesService jsonDataFilesService) {
        this.jsonDataFilesService = jsonDataFilesService;
    }

    @Autowired
    @Qualifier(value = SivaServiceType.SOAP_DATAFILES_SERVICE)
    public void setSoapDataFilesService(DataFilesService soapDataFilesService) {
        this.soapDataFilesService = soapDataFilesService;
    }

}
