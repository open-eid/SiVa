package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.siva.SivaValidationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static ee.openeid.siva.sample.siva.ValidationReportUtils.getOverallValidationResult;
import static ee.openeid.siva.sample.siva.ValidationReportUtils.getValidateFilename;

@Controller
class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
    private static final String REDIRECT_PATH = "redirect:/";
    private SivaValidationService validationService;

    @RequestMapping("/")
    public String startPage() {
        return "index";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String getUploadedFile(
            @RequestParam("file") final MultipartFile file,
            final RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File upload failed");
            return REDIRECT_PATH;
        }

        try {
            final String fullFilename = UploadUtils.getUploadedFile(file);
            final String validationResult = validationService.validateDocument(new File(fullFilename));

            setModelFlashAttributes(redirectAttributes, validationResult);
            UploadUtils.deleteUploadedFile(Paths.get(fullFilename));
        } catch (final IOException e) {
            LOGGER.warn("File upload problem", e);
            redirectAttributes.addFlashAttribute("File upload failed with message: " + e.getMessage());
        }

        return REDIRECT_PATH;
    }

    private static void setModelFlashAttributes(final RedirectAttributes redirectAttributes, final String validationResult) {
        redirectAttributes.addFlashAttribute("validationResult", new JSONObject(validationResult).toString(4));
        redirectAttributes.addFlashAttribute("documentName", getValidateFilename(validationResult));
        redirectAttributes.addFlashAttribute("overallValidationResult", getOverallValidationResult(validationResult));
    }

    @Autowired
    public void setValidationService(final SivaValidationService validationService) {
        this.validationService = validationService;
    }
}
