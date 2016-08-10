package ee.openeid.siva.proxy.http;

import ee.openeid.siva.proxy.configuration.ProxyConfigurationProperties;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RESTProxyService {
    private RestTemplate restTemplate;
    private ProxyConfigurationProperties properties;

    public QualifiedReport validate(ValidationDocument validationDocument) {
        String xroadValidationUrl = properties.getXroadUrl() + "/xroad-validation";
        return restTemplate.postForObject(xroadValidationUrl, validationDocument, QualifiedReport.class);
    }

    @Autowired
    public void setProperties(ProxyConfigurationProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
