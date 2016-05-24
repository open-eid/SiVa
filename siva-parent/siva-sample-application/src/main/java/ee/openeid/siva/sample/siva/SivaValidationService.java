package ee.openeid.siva.sample.siva;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class SivaValidationService {
    private static final String FILENAME_EXTENSION_SEPARATOR = ".";

    private String sivaBaseUrl;
    private RestTemplate restTemplate;

    public String validateDocument(final File file) throws IOException {
        if (file == null) {
            throw new IOException("Invalid file object given");
        }

        final String base64EncodedFile = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));

        final ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setDocument(base64EncodedFile);

        final String filename = file.getName();
        validationRequest.setFilename(filename);
        setValidationDocumentType(validationRequest, filename);

        return restTemplate.postForObject(sivaBaseUrl, validationRequest, String.class);
    }

    private static void setValidationDocumentType(final ValidationRequest validationRequest, final String filename) {
        final String uploadedFileExtension = filename.substring(filename.lastIndexOf(FILENAME_EXTENSION_SEPARATOR) + 1);
        validationRequest.setDocumentType(parseFileExtension(uploadedFileExtension));
    }

    private static FileType parseFileExtension(final String fileExtension) {
        return Arrays.asList(FileType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);

    }

    @Value("${siva.service.url}")
    public void setSivaBaseUrl(final String sivaBaseUrl) {
        this.sivaBaseUrl = sivaBaseUrl;
    }

    @Autowired
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
