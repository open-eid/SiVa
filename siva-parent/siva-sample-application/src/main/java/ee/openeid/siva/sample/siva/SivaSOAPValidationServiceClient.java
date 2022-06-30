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

package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service(value = SivaServiceType.SOAP_SERVICE)
public class SivaSOAPValidationServiceClient implements ValidationService {
    static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String EMPTY_STRING = "";
    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public String validateDocument(String policy, String report, UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }

        String requestBody = createXMLValidationRequest(file.getEncodedFile(), file.getFilename(), report, policy);

        String fullUrl = properties.getServiceHost() + properties.getSoapServicePath();
        return XMLTransformer.formatXML(restTemplate.postForObject(fullUrl, requestBody, String.class));
    }

    static String createXMLValidationRequest(String base64Document, String filename, String report, String policy) {
        String reportType = getSoapReportTypeRow(report);
        String policyType = getSoapPolicyTypeRow(policy);

        String extraLines = reportType + policyType;
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:ValidateDocument>" + LINE_SEPARATOR +
                "         <soap:ValidationRequest>" + LINE_SEPARATOR +
                "            <Document>" + base64Document + "</Document>" + LINE_SEPARATOR +
                "            <Filename>" + filename + "</Filename>" + LINE_SEPARATOR +
                extraLines +
                "         </soap:ValidationRequest>" + LINE_SEPARATOR +
                "      </soap:ValidateDocument>" + LINE_SEPARATOR +
                "   </soapenv:Body>" + LINE_SEPARATOR +
                "</soapenv:Envelope>";
    }

    private static String getSoapPolicyTypeRow(String policy) {
        if (StringUtils.isNotBlank(policy))
            return "            <SignaturePolicy>" + policy + "</SignaturePolicy>" + LINE_SEPARATOR;
        return EMPTY_STRING;
    }

    private static String getSoapReportTypeRow(String report) {
        if (StringUtils.isNotBlank(report))
            return "            <ReportType>" + report + "</ReportType>" + LINE_SEPARATOR;
        return EMPTY_STRING;
    }


    @Autowired
    public void setProperties(SivaRESTWebServiceConfigurationProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
