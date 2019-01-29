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

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.openeid.siva.sample.cache.UploadFileCacheService;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.GoogleAnalyticsProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static ee.openeid.siva.sample.siva.ValidationReportUtils.*;


@Controller
class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    private static final String START_PAGE_VIEW_NAME = "index";
    private static final String HASHCODE = "Hashcode";
    private static final long SECOND_IN_MILLISECONDS = 1000L;
    private static final int JSON_INDENT_FACTOR = 4;
    private ValidationTaskRunner validationTaskRunner;
    private DataFilesTaskRunner dataFilesTaskRunner;
    private HashcodeValidationTaskRunner hashcodeValidationTaskRunner;
    private UploadFileCacheService fileUploadService;
    private GoogleAnalyticsProperties googleAnalyticsProperties;

    private static ValidationResponse validationResponse(Model model) {
        return (ValidationResponse) model.asMap().get("validationResponse");
    }

    @RequestMapping("/")
    public String startPage(final Model model) {
        model.addAttribute("googleTrackingId", googleAnalyticsProperties.getTrackingId());
        return START_PAGE_VIEW_NAME;
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ValidationResponse getUploadedFile(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam String policy,
            @RequestParam String report,
            @RequestParam String type,
            @RequestParam String encodedFilename,
            @RequestParam boolean returnDataFiles,
            Model model
    ) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File upload failed");
            return validationResponse(model);
        }

        long timestamp = System.currentTimeMillis() / SECOND_IN_MILLISECONDS;
        try {
            final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, file, encodedFilename);
            if (returnDataFiles) {
                dataFilesTaskRunner.run(uploadedFile);
            }
            if (HASHCODE.equals(type)) {
                hashcodeValidationTaskRunner.run(policy, report, uploadedFile);
            } else {
                validationTaskRunner.run(policy, report, uploadedFile);
            }
            setModelFlashAttributes(model, HASHCODE.equals(type));
        } catch (IOException e) {
            LOGGER.warn("File upload problem", e);
            model.addAttribute("error", "File upload failed with message: " + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.warn("SiVa SOAP or REST service call failed with error: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            fileUploadService.deleteUploadedFile(timestamp);
            validationTaskRunner.clearValidationResults();
            hashcodeValidationTaskRunner.clearValidationResults();
            dataFilesTaskRunner.clearDataFilesResults();
        }

        return validationResponse(model);
    }


    private String getJsonValidationResult() {
        return validationTaskRunner.getValidationResult(ResultType.JSON);
    }

    private String getSoapValidationResult() {
        return validationTaskRunner.getValidationResult(ResultType.SOAP);
    }

    private String getHashcodeJsonValidationResult() {
        return hashcodeValidationTaskRunner.getValidationResult(ResultType.JSON);
    }

    private String getHashcodeSoapValidationResult() {
        return hashcodeValidationTaskRunner.getValidationResult(ResultType.SOAP);
    }

    private String getJsonDataFilesResult() {
        return dataFilesTaskRunner.getDataFilesResult(ResultType.JSON);
    }

    private String getSoapDataFilesResult() {
        return dataFilesTaskRunner.getDataFilesResult(ResultType.SOAP);
    }

    private void setModelFlashAttributes(Model model, boolean isHashcode) throws JsonProcessingException {
        String jsonValidationResult;
        String soapValidationResult;
        if (isHashcode) {
            jsonValidationResult = getHashcodeJsonValidationResult();
            soapValidationResult = getHashcodeSoapValidationResult();
        } else {
            jsonValidationResult = getJsonValidationResult();
            soapValidationResult = getSoapValidationResult();
        }

        final ValidationResponse response = new ValidationResponse();
        if (!isHashcode)
            response.setFilename(getValidateFilename(jsonValidationResult));
        response.setOverAllValidationResult(getOverallValidationResult(jsonValidationResult));
        response.setValidationWarnings(getValidationWarnings(jsonValidationResult));
        final String output = isJSONValid(jsonValidationResult) ? jsonValidationResult : handleMissingJSON();
        response.setJsonValidationResult(new JSONObject(output).toString(JSON_INDENT_FACTOR));
        response.setSoapValidationResult(soapValidationResult);

        String jsonDataFilesResult = getJsonDataFilesResult();
        String soapDataFilesResult = getSoapDataFilesResult();

        response.setJsonDataFilesResult(jsonDataFilesResult != null ? new JSONObject(jsonDataFilesResult).toString(JSON_INDENT_FACTOR) : null);
        response.setSoapDataFilesResult(soapDataFilesResult);

        model.addAttribute(response);
    }

    @Autowired
    public void setValidationTaskRunner(ValidationTaskRunner validationTaskRunner) {
        this.validationTaskRunner = validationTaskRunner;
    }

    @Autowired
    public void setHashcodeValidationTaskRunner(HashcodeValidationTaskRunner hashcodeValidationTaskRunner) {
        this.hashcodeValidationTaskRunner = hashcodeValidationTaskRunner;
    }

    @Autowired
    public void setDataFilesTaskRunner(DataFilesTaskRunner dataFilesTaskRunner) {
        this.dataFilesTaskRunner = dataFilesTaskRunner;
    }

    @Autowired
    public void setGoogleAnalyticsProperties(GoogleAnalyticsProperties googleAnalyticsProperties) {
        this.googleAnalyticsProperties = googleAnalyticsProperties;
    }

    @Autowired
    public void setFileUploadService(final UploadFileCacheService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
