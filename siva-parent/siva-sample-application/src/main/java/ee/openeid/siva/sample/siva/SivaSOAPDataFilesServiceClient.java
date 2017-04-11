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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.io.IOException;

@Service(value = SivaServiceType.SOAP_DATAFILES_SERVICE)
public class SivaSOAPDataFilesServiceClient implements DataFilesService {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public Observable<String> getDataFiles(UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }
        FileType fileType = ValidationRequestUtils.getValidationServiceType(file);
        String requestBody = createXMLDataFilesRequest(file.getEncodedFile(), fileType.name());
        return Observable.just(XMLTransformer.formatXML(restTemplate.postForObject(properties.getSoapDataFilesServicePath(), requestBody, String.class)));
    }

    private static String createXMLDataFilesRequest(String base64Document, String documentType) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">" + LINE_SEPARATOR +
                "   <soapenv:Header/>" + LINE_SEPARATOR +
                "   <soapenv:Body>" + LINE_SEPARATOR +
                "      <soap:GetDocumentDataFiles>" + LINE_SEPARATOR +
                "         <soap:DataFilesRequest>" + LINE_SEPARATOR +
                "            <Document>" + base64Document + "</Document>" + LINE_SEPARATOR +
                "            <DocumentType>" + documentType + "</DocumentType>" + LINE_SEPARATOR +
                "         </soap:DataFilesRequest>" + LINE_SEPARATOR +
                "      </soap:GetDocumentDataFiles>" + LINE_SEPARATOR +
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
