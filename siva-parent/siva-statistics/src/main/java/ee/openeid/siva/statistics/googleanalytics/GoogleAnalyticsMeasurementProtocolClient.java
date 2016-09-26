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

package ee.openeid.siva.statistics.googleanalytics;

import ee.openeid.siva.statistics.googleanalytics.configuration.properties.GoogleAnalyticsMeasurementProtocolProperties;
import ee.openeid.siva.statistics.model.SimpleSignatureReport;
import ee.openeid.siva.statistics.model.SimpleValidationReport;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @see <a href="https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google Analytics Measurement Protocol</a>
 */
@Component
public class GoogleAnalyticsMeasurementProtocolClient {

    private HttpServletRequest httpRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAnalyticsMeasurementProtocolClient.class);

    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    private GoogleAnalyticsMeasurementProtocolProperties properties;

    public void sendStatisticalData(SimpleValidationReport simpleValidationReport) {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            HttpEntity<String> entity = new HttpEntity<>(composeBatchRequestBody(simpleValidationReport));
            restTemplate.postForObject(properties.getUrl(), entity, byte[].class);
        } catch (URIException e) {
            LOGGER.error("Error on URI-encoding request body: {}", e.getMessage(), e);
        } catch (HttpStatusCodeException e) {
            LOGGER.error("Batch request failed with status code: {}", e.getStatusCode(), e);
        }
    }

    private String composeBatchRequestBody(SimpleValidationReport report) throws URIException {
        List<String> events = new ArrayList<>();
        events.addAll(getContainerEvents(report));
        report.getSimpleSignatureReports().forEach(sigReport -> {
            events.addAll(getSignatureEvents(sigReport, report.getContainerType()));
        });

        StringBuilder requestBodyBuilder = new StringBuilder();
        for (String event : events) {
            String encodedEvent = URIUtil.encodeQuery(event);
            requestBodyBuilder.append(encodedEvent + "\n");
        }

        return requestBodyBuilder.toString();
    }

    private List<String> getContainerEvents(SimpleValidationReport report) {
        List<String> containerEvents = new ArrayList<>();

        containerEvents.add(createContainerEvent("duration", Long.toString(report.getDuration()), report.getContainerType()));
        containerEvents.add(createContainerEvent("signaturesCount", report.getSignatureCount().toString(), report.getContainerType()));
        containerEvents.add(createContainerEvent("validSignaturesCount", report.getValidSignatureCount().toString(), report.getContainerType()));

        return containerEvents;
    }

    private String createContainerEvent(String elementLabel, String elementValue, String eventCategory) {
        String clientId = UUID.randomUUID().toString();
        String userIdentifier = getUserIdentifier();
        String eventAction  = "Container validation";

        return "v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + clientId + "&cd1=" + userIdentifier + "&ec=" + eventCategory + "&ea=" + eventAction + "&el=" + elementLabel + "&ev=" + elementValue;
    }

    private List<String> getSignatureEvents(SimpleSignatureReport report, String eventCategory) {
        List<String> signatureEvents = new ArrayList<>();

        signatureEvents.add(createSignatureEvent(eventCategory, report.getIndication(), report.getCountryCode()));
        signatureEvents.add(createSignatureEvent(eventCategory, report.getSubIndication(), report.getCountryCode()));

        return signatureEvents;
    }

    private String createSignatureEvent(String eventCategory, String elementLabel, String geographicalId) {
        String clientId = UUID.randomUUID().toString();
        String userIdentifier = getUserIdentifier();
        String eventAction  = "Signature validation";

        return "v=1&t=event&ds=" + properties.getDataSourceName() + "&tid=" + properties.getTrackingId() + "&cid=" + clientId + "&cd1=" + userIdentifier + "&ec=" + eventCategory + "&ea=" + eventAction + "&el=" + elementLabel + "&geoid=" + geographicalId;
    }

    private String getUserIdentifier() {
        String userIdentifier = httpRequest.getHeader("x-authenticated-user");
        return StringUtils.isEmpty(userIdentifier) ? "N/A" : userIdentifier;
    }

    @Autowired
    public void setProperties(GoogleAnalyticsMeasurementProtocolProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
