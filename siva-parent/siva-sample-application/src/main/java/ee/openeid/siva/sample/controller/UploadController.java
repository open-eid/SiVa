package ee.openeid.siva.sample.controller;

import ee.openeid.siva.sample.ci.info.BuildInfo;
import ee.openeid.siva.sample.siva.SivaValidationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

import static ee.openeid.siva.sample.siva.ValidationReportUtils.getOverallValidationResult;
import static ee.openeid.siva.sample.siva.ValidationReportUtils.getValidateFilename;

@Controller
class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    private static final String REDIRECT_PATH = "redirect:/";
    private static final String START_PAGE_VIEW_NAME = "index";

    private SivaValidationService validationService;
    private FileUploadService fileUploadService;
    private BuildInfo buildInfo;

    @RequestMapping("/")
    public String startPage(final Model model) {
        model.addAttribute(buildInfo);
        return START_PAGE_VIEW_NAME;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String getUploadedFile(
            @RequestParam("file") final MultipartFile file,
            final RedirectAttributes redirectAttributes
    ) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File upload failed");
            return REDIRECT_PATH;
        }

        try {
            final String fullFilename = fileUploadService.getUploadedFile(file);
            final String validationResult = validationService.validateDocument(new File(fullFilename));

            setModelFlashAttributes(redirectAttributes, validationResult);
        } catch (final IOException e) {
            LOGGER.warn("File upload problem", e);
            redirectAttributes.addFlashAttribute("File upload failed with message: " + e.getMessage());
        } finally {
            fileUploadService.deleteUploadedFile(file.getOriginalFilename());
        }

        return REDIRECT_PATH;
    }

    private static void setModelFlashAttributes(final RedirectAttributes redirectAttributes, final String validationResult) {
        redirectAttributes.addFlashAttribute("validationResult", new JSONObject(validationResult).toString(4));
        redirectAttributes.addFlashAttribute("documentName", getValidateFilename(validationResult));
        redirectAttributes.addFlashAttribute("overallValidationResult", getOverallValidationResult(validationResult));
    }

    @Autowired
    public void setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    @Autowired
    public void setValidationService(final SivaValidationService validationService) {
        this.validationService = validationService;
    }

    @Autowired
    public void setFileUploadService(final FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }
}
