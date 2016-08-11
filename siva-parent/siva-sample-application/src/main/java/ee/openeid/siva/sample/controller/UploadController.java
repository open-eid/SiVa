package ee.openeid.siva.sample.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.openeid.siva.sample.cache.UploadFileCacheService;
import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.ci.info.BuildInfo;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rx.Observable;

import java.io.IOException;

import static ee.openeid.siva.sample.siva.ValidationReportUtils.*;

@Controller
class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private static final String REDIRECT_PATH = "redirect:/validation-response";
    private static final String START_PAGE_VIEW_NAME = "index";

    private ValidationTaskRunner validationTaskRunner;
    private UploadFileCacheService fileUploadService;
    private GoogleAnalyticsProperties googleAnalyticsProperties;
    private Observable<BuildInfo> buildInfo;

    @RequestMapping("/")
    public String startPage(final Model model) {
        model.addAttribute(buildInfo.toBlocking().single());
        model.addAttribute("googleTrackingId", googleAnalyticsProperties.getTrackingId());
        return START_PAGE_VIEW_NAME;
    }

    @ResponseBody
    @RequestMapping("/validation-response")
    public ValidationResponse validationResponse(final Model model) {
        return  (ValidationResponse) model.asMap().get("validationResponse");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String getUploadedFile(
            @RequestParam(value = "file") final MultipartFile file,
            final RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File upload failed");
            return REDIRECT_PATH;
        }

        final long timestamp = System.currentTimeMillis() / 1000L;
        try {
            final UploadedFile uploadedFile = fileUploadService.addUploadedFile(timestamp, file);
            validationTaskRunner.run(uploadedFile);

            setModelFlashAttributes(redirectAttributes, getJsonValidationResult(), getSoapValidationResult());
        } catch (IOException e) {
            LOGGER.warn("File upload problem", e);
            redirectAttributes.addFlashAttribute("File upload failed with message: " + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.warn("SiVa SOAP or REST service call failed with error: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            fileUploadService.deleteUploadedFile(timestamp);
            validationTaskRunner.clearValidationResults();
        }

        return REDIRECT_PATH;
    }

    private String getJsonValidationResult() {
        return validationTaskRunner.getValidationResult(ValidationResultType.JSON);
    }

    private String getSoapValidationResult() {
        return validationTaskRunner.getValidationResult(ValidationResultType.SOAP);
    }

    private static void setModelFlashAttributes(
            RedirectAttributes redirectAttributes,
            String validationResult,
            String soapValidationResult
    ) throws JsonProcessingException {
        final ValidationResponse response = new ValidationResponse();
        response.setFilename(getValidateFilename(validationResult));
        response.setOverAllValidationResult(getOverallValidationResult(validationResult));

        final String output = isJSONNull(validationResult) ? handleMissingJSON() : validationResult;
        response.setJsonValidationResult(new JSONObject(output).toString(4));
        response.setSoapValidationResult(soapValidationResult);

        redirectAttributes.addFlashAttribute(response);
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
    public void setBuildInfo(final Observable<BuildInfo> buildInfo) {
        this.buildInfo = buildInfo;
    }

    @Autowired
    public void setFileUploadService(final UploadFileCacheService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
