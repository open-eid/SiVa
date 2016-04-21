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

    @Value("${siva.service.url}")
    private String sivaBaseUrl;

    private RestTemplate restTemplate;

    public String validateDocument(final File file, final ReportType sivaReportType) throws IOException {
        String encodeFile = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));

        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setDocument(encodeFile);
        validationRequest.setReportType(sivaReportType);

        String filename = file.getName();
        validationRequest.setFilename(filename);
        validationRequest.setDocumentType(parseFileExtension(filename.substring(filename.lastIndexOf(".") + 1)));

        return restTemplate.postForObject(sivaBaseUrl, validationRequest, String.class);
    }

    private FileType parseFileExtension(final String fileExtension) {
        return Arrays.asList(FileType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);

    }

    @Autowired
    public void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
