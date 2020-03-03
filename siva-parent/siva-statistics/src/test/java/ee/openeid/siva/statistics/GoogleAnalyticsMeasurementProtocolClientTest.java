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

package ee.openeid.siva.statistics;

import ee.openeid.siva.statistics.googleanalytics.GoogleAnalyticsMeasurementProtocolClient;
import ee.openeid.siva.statistics.googleanalytics.configuration.properties.GoogleAnalyticsMeasurementProtocolProperties;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@PrepareForTest({ UUID.class, GoogleAnalyticsMeasurementProtocolClient.class })
@RunWith(PowerMockRunner.class)
public class GoogleAnalyticsMeasurementProtocolClientTest {

    private static final String CONTAINER_TYPE = "some container type";

    private static final String MOCKED_UUID_STRING = "mocked-uuid";

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
        BDDMockito.given(UUID.randomUUID()).willReturn(uuid);
        BDDMockito.given(uuid.toString()).willReturn(MOCKED_UUID_STRING);
    }

    @Test
    public void checkIfRequestIsNotSentWhenDisabled() throws Exception {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        String indication = "TOTAL_PASSED";
        String subindication = "";
        String countryCode = "EE";
        String signatureFormat = "FORMAT";

        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, "N/A");
        addSimpleSignatureReport(report, indication, subindication, countryCode, signatureFormat);

        properties.setEnabled(false);

        RestTemplate restTemplateMock = mock(RestTemplate.class);
        gaClient.setRestTemplate(restTemplateMock);
        Mockito.verify(restTemplateMock, Mockito.never()).postForObject(Mockito.any(String.class), Mockito.any(HttpEntity.class), Mockito.any());

        gaClient.sendStatisticalData(report);
    }

    @Test
    public void checkIfRequestIsSentWhenEnabled() throws Exception {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 1;
        int totalSignatureCount = 1;
        String indication = "TOTAL_PASSED";
        String subindication = "";
        String countryCode = "EE";
        String signatureFormat = "FORMAT";

        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, "N/A");
        addSimpleSignatureReport(report, indication, subindication, countryCode, signatureFormat);

        properties.setEnabled(true);

        mockServer.expect(requestTo(properties.getUrl()))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(getExpectedHttpRequestBody(report, "N/A")))
                .andRespond(withSuccess());


        gaClient.sendStatisticalData(report);
    }

    @Test
    public void checkIfModifiedPropertiesApplyToRequest() throws Exception {
        long validationDurationinMillis = 1000L;
        int validSignaturesCount = 2;
        int totalSignatureCount = 1;
        String firstSignatureIndication = "TOTAL_PASSED";
        String firstSignatureSubindication = "";
        String firstSignatureCountryCode = "EE";
        String firstSignatureFormat = "FORMAT";
        String secondSignatureIndication = "TOTAL_FAILED";
        String secondSignatureSubindication = "CERTIFICATE_CHAIN_NOT_FOUND";
        String secondSignatureCountryCode = "US";
        String secondSignatureFormat = "";
        String xAuthenticatedUser = "some_user";
        SimpleValidationReport report = createDummySimpleValidationReport(TimeUnit.MILLISECONDS.toNanos(validationDurationinMillis), validSignaturesCount, totalSignatureCount, xAuthenticatedUser);
        addSimpleSignatureReport(report, firstSignatureIndication, firstSignatureSubindication, firstSignatureCountryCode, firstSignatureFormat);
        addSimpleSignatureReport(report, secondSignatureIndication, secondSignatureSubindication, secondSignatureCountryCode, secondSignatureFormat);

        properties.setEnabled(true);
        properties.setUrl("http://www.someurl.com/some_path");
        properties.setDataSourceName("some_data_source");
        properties.setTrackingId("some_tracking_id");

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

    private void addSimpleSignatureReport(SimpleValidationReport report, String indication, String subindication, String country, String signatureFormat) {
        if (report.getSimpleSignatureReports() == null) {
            report.setSimpleSignatureReports(new ArrayList<>());
        }
        SimpleSignatureReport sigReport = new SimpleSignatureReport();
        sigReport.setIndication(indication);
        sigReport.setSubIndication(subindication);
        sigReport.setCountryCode(country);
        sigReport.setSignatureFormat(signatureFormat);
        report.getSimpleSignatureReports().add(sigReport);
    }

    private String getExpectedHttpRequestBody(SimpleValidationReport report, String userIdentifier) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(UriUtils.encodeFragment("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=duration&ev=" + report.getDuration(), "UTF-8") + "\n");
        sb.append(UriUtils.encodeFragment("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=signaturesCount&ev=" + report.getSignatureCount(), "UTF-8") + "\n");
        sb.append(UriUtils.encodeFragment("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + CONTAINER_TYPE + "&ea=Container validation&el=validSignaturesCount&ev=" + report.getValidSignatureCount(), "UTF-8") + "\n");
        for (SimpleSignatureReport sigReport : report.getSimpleSignatureReports()) {
            String indicationSubIndicationPair = expectedIndicationSubIndicationPair(sigReport.getIndication(), sigReport.getSubIndication());
            String containerTypeSigFormatPair = CONTAINER_TYPE + "/" + sigReport.getSignatureFormat();
            sb.append(UriUtils.encodeFragment("v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + MOCKED_UUID_STRING + "&cd1=" + userIdentifier + "&ec=" + containerTypeSigFormatPair + "&ea=Signature validation&el=" + indicationSubIndicationPair + "&geoid=" + sigReport.getCountryCode(), "UTF-8") + "\n");
        }
        return sb.toString();
    }

    private String expectedIndicationSubIndicationPair(String indication, String subIndication) {
        return indication + (StringUtils.isEmpty(subIndication) ? "" : "/" + subIndication);
    }

}
