package ee.sk.pdf.validator.monitoring.template;

import com.google.common.io.BaseEncoding;
import ee.sk.pdf.validator.monitoring.logging.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PDFLoader {
    private final static Logger LOGGER = LoggerFactory.getLogger(PDFLoader.class);
    private static final String STRING_EMPTY = "";

    private String pdfFileLocation;

    @Autowired
    private LoggingService loggingService;

    public CharSequence getBase64EncodedPDF() {
        try {
            return BaseEncoding.base64().encode(loadPDFFile());
        } catch (URISyntaxException | IOException e) {
            loggingService.logError(LOGGER, "monitoring.pdfLoadingFailed", e.getMessage());
        }

        return STRING_EMPTY;
    }

    private byte[] loadPDFFile() throws URISyntaxException, IOException {
        Path pdfFilePath = Paths.get(this.getClass().getResource(pdfFileLocation).toURI());
        return Files.readAllBytes(pdfFilePath);
    }

    public void setPdfFileLocation(String pdfFileLocation) {
        this.pdfFileLocation = pdfFileLocation;
    }
}
