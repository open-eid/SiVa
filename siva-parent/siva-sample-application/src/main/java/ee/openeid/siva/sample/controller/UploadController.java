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

    private ValidationTaskRunner validationTaskRunner;
    private UploadFileCacheService fileUploadService;
    private GoogleAnalyticsProperties googleAnalyticsProperties;

    @RequestMapping("/")
    public String startPage(final Model model) {
        model.addAttribute("googleTrackingId", googleAnalyticsProperties.getTrackingId());
        return START_PAGE_VIEW_NAME;
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ValidationResponse getUploadedFile(@RequestParam(value = "file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File upload failed");
            return validationResponse(model);
        }

        long timestamp = System.currentTimeMillis() / 1000L;
        try {
            final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, file);
            validationTaskRunner.run(uploadedFile);

            setModelFlashAttributes(model);
        } catch (IOException e) {
            LOGGER.warn("File upload problem", e);
            model.addAttribute("error", "File upload failed with message: " + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.warn("SiVa SOAP or REST service call failed with error: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            fileUploadService.deleteUploadedFile(timestamp);
            validationTaskRunner.clearValidationResults();
        }

        return validationResponse(model);
    }

    private String getJsonValidationResult() {
        return validationTaskRunner.getValidationResult(ValidationResultType.JSON);
    }

    private String getSoapValidationResult() {
        return validationTaskRunner.getValidationResult(ValidationResultType.SOAP);
    }

    private static ValidationResponse validationResponse(Model model) {
        return (ValidationResponse) model.asMap().get("validationResponse");
    }

    private void setModelFlashAttributes(Model model) throws JsonProcessingException {
        String jsonValidationResult = getJsonValidationResult();
        String soapValidationResult = getSoapValidationResult();

        final ValidationResponse response = new ValidationResponse();
        response.setFilename(getValidateFilename(jsonValidationResult));
        response.setOverAllValidationResult(getOverallValidationResult(jsonValidationResult));

        final String output = isJSONNull(jsonValidationResult) ? handleMissingJSON() : jsonValidationResult;
        response.setJsonValidationResult(new JSONObject(output).toString(4));
        response.setSoapValidationResult(soapValidationResult);

        model.addAttribute(response);
    }

    @Autowired
    public void setValidationTaskRunner(ValidationTaskRunner validationTaskRunner) {
        this.validationTaskRunner = validationTaskRunner;
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
