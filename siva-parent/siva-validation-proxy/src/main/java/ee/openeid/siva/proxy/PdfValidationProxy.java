package ee.openeid.siva.proxy;

import ee.openeid.pdf.webservice.json.PDFDocument;
import ee.openeid.pdf.webservice.json.ValidationService;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
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

    public String validate(final ProxyDocument document) {
        PDFDocument pdfDocument = new PDFDocument();
        pdfDocument.setName(document.getName());
        pdfDocument.setBytes(document.getBytes());
        pdfDocument.setMimeType(document.getMimeType());

        Map<String, String> reportMap =  validationService.validateDocument(pdfDocument);
        String report = reportMap.get(document.getReportType().name());
        if (document.getRequestProtocol() == RequestProtocol.JSON) {
            report = converter.toJSON(report);
        }
        return report;
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
