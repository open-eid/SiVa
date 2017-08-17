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

package ee.openeid.siva.sample.siva;

import rx.Observable;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service(value = SivaServiceType.SOAP_SERVICE)
public class SivaSOAPValidationServiceClient implements ValidationService {
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public Observable<String> validateDocument(String policy, UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }

        FileType serviceType = ValidationRequestUtils.getValidationServiceType(file);
        String requestBody = createXMLValidationRequest(file.getEncodedFile(), serviceType, file.getFilename(), policy);

        String fullUrl = properties.getServiceHost() + properties.getSoapServicePath();
        return Observable.just(XMLTransformer.formatXML(restTemplate.postForObject(fullUrl, requestBody, String.class)));
    }

    private static String createXMLValidationRequest(String base64Document, FileType fileType, String filename, String policy) {
        String documentType = "";
        if (fileType == FileType.XROAD)
            documentType = "<DocumentType>" + fileType.name() + "</DocumentType>" + LINE_SEPARATOR;
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:ValidateDocument>" + LINE_SEPARATOR +
                "         <soap:ValidationRequest>" + LINE_SEPARATOR +
                "            <Document>" + base64Document + "</Document>" + LINE_SEPARATOR +
                "            <Filename>" + filename + "</Filename>" + LINE_SEPARATOR +
                documentType +
                "            <SignaturePolicy>" + policy + "</SignaturePolicy>" + LINE_SEPARATOR +
                "         </soap:ValidationRequest>" + LINE_SEPARATOR +
                "      </soap:ValidateDocument>" + LINE_SEPARATOR +
                "   </soapenv:Body>" + LINE_SEPARATOR +
                "</soapenv:Envelope>";
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
