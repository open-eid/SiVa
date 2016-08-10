package ee.openeid.siva.proxy.http;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RESTProxyService {
    private static final String VALIDATION_URL_PATH = "/xroad-validation";
    private RestTemplate restTemplate;

    public QualifiedReport validate(ValidationDocument validationDocument) {
        validationDocument.setDataBase64Encoded(Base64.encodeBase64String(validationDocument.getBytes()));
        return restTemplate.postForObject(VALIDATION_URL_PATH, validationDocument, QualifiedReport.class);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
