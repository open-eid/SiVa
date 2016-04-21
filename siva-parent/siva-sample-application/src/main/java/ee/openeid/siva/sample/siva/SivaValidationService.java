package ee.openeid.siva.sample.siva;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@Service
public class SivaValidationService {
    @Value("${siva.service.url}")
    private String sivaBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public String validateDocument(File file, ReportType sivaReportType) throws IOException {
        String encodeFile = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));

        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setDocument(encodeFile);
        validationRequest.setFilename(file.getName());
        validationRequest.setReportType(sivaReportType);
        validationRequest.setDocumentType(FileType.PDF);

        return restTemplate.postForObject(sivaBaseUrl, validationRequest, String.class);
    }
}
