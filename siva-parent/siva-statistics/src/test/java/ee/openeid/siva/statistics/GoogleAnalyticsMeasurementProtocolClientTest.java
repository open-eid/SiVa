/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.statistics;

import ee.openeid.siva.statistics.googleanalytics.GoogleAnalyticsMeasurementProtocolClient;
import ee.openeid.siva.statistics.googleanalytics.configuration.properties.GoogleAnalyticsMeasurementProtocolProperties;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@PrepareForTest({ UUID.class, GoogleAnalyticsMeasurementProtocolClient.class })
@RunWith(PowerMockRunner.class)
public class GoogleAnalyticsMeasurementProtocolClientTest {

    private static final String CONTAINER_TYPE = "some container type";

    private static final String MOCKED_UUID_STRING = "mocked-uuid";

    private static final String X_AUTHENTICATED_USER = "x-authenticated-user";

    private GoogleAnalyticsMeasurementProtocolClient gaClient = new GoogleAnalyticsMeasurementProtocolClient();

    private GoogleAnalyticsMeasurementProtocolProperties properties = new GoogleAnalyticsMeasurementProtocolProperties();

    private MockRestServiceServer mockServer;

    private UUID uuid;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        gaClient.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(restTemplate);

        gaClient.setProperties(properties);

        uuid = mock(UUID.class);
        mockStatic(UUID.class);
        given(UUID.randomUUID()).willReturn(uuid);
        given(uuid.toString()).willReturn(MOCKED_UUID_STRING);
    }

    @Test
    public void checkIfRequestIsNotSentWhenDisabled() throws URIException {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        String indication = "TOTAL_PASSED";
        String subindication = "";
        String countryCode = "EE";

        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, "N/A");
        addSimpleSignatureReport(report, indication, subindication, countryCode);

        properties.setEnabled(false);

        RestTemplate restTemplateMock = mock(RestTemplate.class);
        gaClient.setRestTemplate(restTemplateMock);
        Mockito.verify(restTemplateMock, never()).postForObject(any(String.class), any(HttpEntity.class), any());

        gaClient.sendStatisticalData(report);
    }

    @Test
    public void checkIfRequestIsSentWhenEnabled() throws URIException {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        String indication = "TOTAL_PASSED";
        String subindication = "";
        String countryCode = "EE";

        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, "N/A");
        addSimpleSignatureReport(report, indication, subindication, countryCode);

        properties.setEnabled(true);

        //HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        //gaClient.setHttpRequest(mockedRequest);
        //when(mockedRequest.getHeader(X_AUTHENTICATED_USER)).thenReturn("");

        mockServer.expect(requestTo(properties.getUrl()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(getExpectedHttpRequestBody(report, "N/A")))
                .andRespond(withSuccess());


        gaClient.sendStatisticalData(report);
    }

    @Test
    public void checkIfModifiedPropertiesApplyToRequest() throws URIException {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 2;
        int totalSignatureCount = 1;
        String firstSignatureIndication = "TOTAL_PASSED";
        String firstSignatureSubindication = "";
        String firstSignatureCountryCode = "EE";
        String secondSignatureIndication = "TOTAL_FAILED";
        String secondSignatureSubindication = "CERTIFICATE_CHAIN_NOT_FOUND";
        String secondSignatureCountryCode = "US";
        String xAuthenticatedUser = "some_user";
        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, xAuthenticatedUser);
        addSimpleSignatureReport(report, firstSignatureIndication, firstSignatureSubindication, firstSignatureCountryCode);
        addSimpleSignatureReport(report, secondSignatureIndication, secondSignatureSubindication, secondSignatureCountryCode);

        properties.setEnabled(true);
        properties.setUrl("http://www.someurl.com/some_path");
        properties.setDataSourceName("some_data_source");
        properties.setTrackingId("some_tracking_id");


        //HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        //gaClient.setHttpRequest(mockedRequest);
        //when(mockedRequest.getHeader(X_AUTHENTICATED_USER)).thenReturn(xAuthenticatedUser);

        mockServer.expect(requestTo(properties.getUrl()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(getExpectedHttpRequestBody(report, xAuthenticatedUser)))
                .andRespond(withSuccess());

        gaClient.sendStatisticalData(report);
    }

    private SimpleValidationReport createDummySimpleValidationReport(long duration, int validSignaturesCount, int totalSignaturesCount, String xAuthenticatedUser) {
        SimpleValidationReport report = new SimpleValidationReport();
        report.setDuration(duration);
        report.setSignatureCount(totalSignaturesCount);
        report.setContainerType(CONTAINER_TYPE);
        report.setValidSignatureCount(validSignaturesCount);
        report.setUserIdentifier(xAuthenticatedUser);
        return report;
    }

    private void addSimpleSignatureReport(SimpleValidationReport report, String indication, String subindication, String country) {
        if (report.getSimpleSignatureReports() == null) {
            report.setSimpleSignatureReports(new ArrayList<>());
        }
        SimpleSignatureReport sigReport = new SimpleSignatureReport();
        sigReport.setIndication(indication);
        sigReport.setSubIndication(subindication);
        sigReport.setCountryCode(country);
        report.getSimpleSignatureReports().add(sigReport);
    }

    private String getExpectedHttpRequestBody(SimpleValidationReport report, String userIdentifier) throws URIException {
        StringBuilder sb = new StringBuilder();
        sb.append(URIUtil.encodeQuery("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=duration&ev=" + report.getDuration()) + "\n");
        sb.append(URIUtil.encodeQuery("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=signaturesCount&ev=" + report.getSignatureCount()) + "\n");
        sb.append(URIUtil.encodeQuery("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=validSignaturesCount&ev=" + report.getValidSignatureCount()) + "\n");
        for (SimpleSignatureReport sigReport : report.getSimpleSignatureReports()) {
            sb.append(URIUtil.encodeQuery("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Signature validation&el=" + sigReport.getIndication() + "&geoid=" + sigReport.getCountryCode()) + "\n");
            sb.append(URIUtil.encodeQuery("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Signature validation&el=" + sigReport.getSubIndication() + "&geoid=" + sigReport.getCountryCode()) + "\n");
        }
        return sb.toString();
    }

}
