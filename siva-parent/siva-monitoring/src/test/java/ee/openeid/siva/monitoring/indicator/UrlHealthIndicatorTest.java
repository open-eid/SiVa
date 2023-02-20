/*
 * Copyright 2017 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package ee.openeid.siva.monitoring.indicator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static ee.openeid.siva.monitoring.indicator.UrlHealthIndicator.RESPONSE_PARAM_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UrlHealthIndicatorTest {

    public static final String TEST_LINK_NAME = "someLinkToExternalSystem";
    public static final String TEST_LINK_URL = "http://localhost:8080";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UrlHealthIndicator.ExternalLink externalLink;

    @InjectMocks
    private UrlHealthIndicator urlHealthIndicator;

    @BeforeEach
    public void setUp() {
        Mockito.when(externalLink.getName()).thenReturn(TEST_LINK_NAME);
        Mockito.when(externalLink.getUrl()).thenReturn(TEST_LINK_URL);
    }

    @Test
    public void whenRestServiceReturnsUp(){

        Mockito.when(restTemplate.getForObject(Mockito.eq(externalLink.getUrl()), Mockito.eq(UrlHealthIndicator.HealthStatus.class))).thenReturn(new UrlHealthIndicator.HealthStatus(Status.UP.getCode()));

        Health health = urlHealthIndicator.health();
        assertEquals(Status.UP, health.getStatus());
        assertEquals(TEST_LINK_NAME, health.getDetails().get(RESPONSE_PARAM_NAME));
    }

    @Test
    public void whenRestServiceReturnsDown() {

        Mockito.when(restTemplate.getForObject(Mockito.eq(externalLink.getUrl()), Mockito.eq(UrlHealthIndicator.HealthStatus.class))).thenReturn(new UrlHealthIndicator.HealthStatus(Status.DOWN.getCode()));

        Health health = urlHealthIndicator.health();
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(TEST_LINK_NAME, health.getDetails().get(RESPONSE_PARAM_NAME));
    }

    @Test
    public void whenRestServiceConnectionIsDown() {

        Mockito.when(restTemplate.getForObject(Mockito.eq(externalLink.getUrl()), Mockito.eq(UrlHealthIndicator.HealthStatus.class))).thenThrow(new RestClientException("any rest exception"));

        Health health = urlHealthIndicator.health();
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(TEST_LINK_NAME, health.getDetails().get(RESPONSE_PARAM_NAME));
    }
}
