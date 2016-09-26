/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
