package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.http.RESTProxyService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RESTProxyServiceTest {

    private RESTProxyService restProxyService;
    private QualifiedReport mockReport;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        mockReport = new QualifiedReport();
        restProxyService = new RESTProxyService();
        restProxyService.setRestTemplate(restTemplate);
    }

    @Test
    public void restProxyServiceShouldReturnTheSameReportObjectItGetsFromRemoteRestService() {
        when(restTemplate.postForObject(anyString(), any(ValidationDocument.class), any())).thenReturn(mockReport);
        assertSame(mockReport, restProxyService.validate(new ValidationDocument()));
    }

}
