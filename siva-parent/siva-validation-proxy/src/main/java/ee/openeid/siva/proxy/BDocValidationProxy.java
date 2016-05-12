package ee.openeid.siva.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.proxy.converter.XMLToJSONConverter;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.proxy.exception.ValidationProxyException;
import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.validation.service.bdoc.BDOCValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service
public class BDocValidationProxy extends AbstractValidationProxy {

    private XMLToJSONConverter converter;
    private BDOCValidationService bdocValidationService;

    @Override //TODO: use the parent class method when pdf validator also uses QualifiedReport (VAL-180)
    public String validate(final ProxyDocument proxyDocument) {
        QualifiedReport report = validateInService(super.createValidationDocument(proxyDocument)).getQualifiedReport();
        if (proxyDocument.getRequestProtocol() == RequestProtocol.JSON) {
            return toJSON(report);
        }
        return toXML(report);
    }

    private String toXML(QualifiedReport report) {
        try {
            Marshaller jaxbMarshaller = JAXBContext.newInstance(QualifiedReport.class).createMarshaller();
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(report, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new ValidationProxyException(e);
        }
    }

    private String toJSON(QualifiedReport report) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(report);
        } catch (JsonProcessingException e) {
            throw new ValidationProxyException(e);
        }
    }

    QualifiedValidationResult validateInService(ValidationDocument validationDocument) {
        return bdocValidationService.validateDocument(validationDocument);
    }

    String toJSON(String report) {
        return converter.toJSON(report);
    }

    @Autowired
    public void setBDOCValidationService(BDOCValidationService bdocValidationService) {
        this.bdocValidationService = bdocValidationService;
    }

    @Autowired
    public void setXMLToJSONConverter(XMLToJSONConverter converter) {
        this.converter = converter;
    }

}
