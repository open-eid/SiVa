package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PdfValidationProxy implements ValidationProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfValidationProxy.class);

    private XMLToJSONConverter converter;

    private ValidationService validationService;

    public String validate(final ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = createValidationDocument(proxyDocument);

        Map<String, String> reportMap =  validationService.validateDocument(validationDocument);
        String report = reportMap.get(proxyDocument.getReportType().name());
        if (proxyDocument.getRequestProtocol() == RequestProtocol.JSON) {
            report = converter.toJSON(report);
        }
        return report;
    }

    private ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setMimeType(proxyDocument.getDocumentType().getMimeType());
        return validationDocument;
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }

    @Autowired
    public void setXMLToJSONConverter(XMLToJSONConverter converter) {
        this.converter = converter;
    }

}
