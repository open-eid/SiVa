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

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Service(value = SivaServiceType.SOAP_SERVICE)
public class SivaSOAPValidationServiceClient implements ValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SivaSOAPValidationServiceClient.class);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public Observable<String> validateDocument(String policy, UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }

        FileType serviceType = ValidationRequestUtils.getValidationServiceType(file);
        String requestBody = createXMLValidationRequest(file.getEncodedFile(), serviceType.name(), file.getFilename(), policy);

        String fullUrl = properties.getServiceHost() + properties.getSoapServicePath();
        return Observable.just(formatXML(restTemplate.postForObject(fullUrl, requestBody, String.class)));
    }

    private static String formatXML(String xml) {
        Source xmlInput = new StreamSource(new StringReader(xml));
        StreamResult xmlOutput = new StreamResult(new StringWriter());

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            LOGGER.warn("XML Parsing error: {}", e.getMessage(), e);
        }

        return xmlOutput.getWriter().toString().replace("?>", "?>" + LINE_SEPARATOR);
    }

    private static String createXMLValidationRequest(String base64Document, String documentType, String filename, String policy) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:ValidateDocument>" + LINE_SEPARATOR +
                "         <soap:ValidationRequest>" + LINE_SEPARATOR +
                "            <Document>" + base64Document + "</Document>" + LINE_SEPARATOR +
                "            <Filename>" + filename + "</Filename>" + LINE_SEPARATOR +
                "            <DocumentType>" + documentType + "</DocumentType>" + LINE_SEPARATOR +
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
