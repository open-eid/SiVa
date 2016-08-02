package ee.openeid.siva.sample.siva;

import ee.openeid.siva.sample.cache.UploadedFile;
import ee.openeid.siva.sample.configuration.SivaRESTWebServiceConfigurationProperties;
import ee.openeid.siva.sample.controller.ValidationRequestUtils;
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
    private SivaRESTWebServiceConfigurationProperties properties;
    private RestTemplate restTemplate;

    @Override
    public Observable<String> validateDocument(UploadedFile file) throws IOException {
        if (file == null) {
            throw new IOException("File not found");
        }

        FileType serviceType = ValidationRequestUtils.getValidationServiceType(file);
        String requestBody = createXMLValidationRequest(file.getEncodedFile(), serviceType.name(), file.getFilename());

        String fullUrl = properties.getServiceHost() + properties.getSoapServicePath();
        return Observable.just(formatXML(restTemplate.postForObject(fullUrl, requestBody, String.class)));
    }

    private String formatXML(String xml) {
        Source xmlInput = new StreamSource(new StringReader(xml));
        StreamResult xmlOutput = new StreamResult(new StringWriter());

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return xmlOutput.getWriter().toString();
    }

    private static String createXMLValidationRequest(String base64Document, String documentType, String filename) {
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.webapp.siva.openeid.ee/\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <soap:ValidateDocument>\n" +
                "         <soap:ValidationRequest>\n" +
                "            <Document>" + base64Document + "</Document>\n" +
                "            <Filename>" + filename + "</Filename>\n" +
                "            <DocumentType>" + documentType + "</DocumentType>\n" +
                "         </soap:ValidationRequest>\n" +
                "      </soap:ValidateDocument>\n" +
                "   </soapenv:Body>\n" +
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
