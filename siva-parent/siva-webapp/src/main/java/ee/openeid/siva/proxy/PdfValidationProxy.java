package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.JSONDocument;
import ee.openeid.pdf.webservice.json.ValidationService;
import eu.europa.esig.dss.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfValidationProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfValidationProxy.class);

    @Autowired
    private ValidationService validationService;

    public String validate() {
        final JSONDocument document = createDocument();
        return validationService.validateDocument(document);
    }

    private JSONDocument createDocument() {
        final JSONDocument document = new JSONDocument();

        try {
            final Path filepath = Paths.get(getClass().getClassLoader().getResource("test-files/sample.pdf").toURI()) ;
            LOGGER.info("File: {} exists: {}", filepath, Files.exists(filepath));

            document.setBytes(Files.readAllBytes(filepath));
            document.setMimeType(MimeType.PDF);
            document.setName("sample.pdf");
        } catch (IOException | URISyntaxException e) {
            LOGGER.warn("Failed to load PDF file", e);
        }

        return document;
    }
}
