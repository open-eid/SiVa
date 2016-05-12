package ee.openeid.siva.sample;

import ee.openeid.siva.sample.siva.SivaValidationReportService;
import ee.openeid.siva.sample.siva.SivaValidationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private SivaValidationService validationService;

    @Autowired
    private SivaValidationReportService sivaValidationReportService;

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
            return "redirect:/";
        }

        try {
            final String fullFilename = getUploadedFile(file);
            final String validationResult = validationService.validateDocument(new File(fullFilename));

            redirectAttributes.addFlashAttribute("validationResult", new JSONObject(validationResult).toString(4));
            redirectAttributes.addFlashAttribute("documentName", sivaValidationReportService.getValidateFilename(validationResult));
            redirectAttributes.addFlashAttribute("overallValidationResult", sivaValidationReportService.getOverallValidationResult(validationResult));
            redirectAttributes.addFlashAttribute("error", "File upload success");
        } catch (final IOException e) {
            LOGGER.warn("File upload problem", e);
            redirectAttributes.addFlashAttribute("File upload failed with message: " + e.getMessage());
        }

        return "redirect:/";
    }

    private static String getUploadedFile(@RequestParam("file") final MultipartFile file) throws IOException {
        final Path uploadDir = Paths.get("upload-dir").toAbsolutePath();
        final String fullFilename = uploadDir + "/" + file.getOriginalFilename();
        if (!Files.exists(uploadDir)) {
            Files.createDirectory(uploadDir);
        }

        final BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File(fullFilename))
        );
        FileCopyUtils.copy(file.getInputStream(), stream);
        return fullFilename;
    }

}
