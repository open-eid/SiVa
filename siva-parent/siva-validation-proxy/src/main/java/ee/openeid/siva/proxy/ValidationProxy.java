package ee.openeid.siva.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.proxy.exception.ValidationProxyException;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service
public class ValidationProxy {

    private ApplicationContext applicationContext;

    public String validate(final ProxyDocument proxyDocument) {
        ValidationService validationService = getServiceForType(proxyDocument.getDocumentType());
        QualifiedReport report = validationService.validateDocument(createValidationDocument(proxyDocument));

        if (proxyDocument.getRequestProtocol() == RequestProtocol.JSON) {
            return toJSON(report);
        }
        return toXML(report);
    }

    private ValidationService getServiceForType(DocumentType doucmentType) {
        String validatorName = doucmentType.name().toLowerCase() + "-validator";
        try {
            ValidationService validationService = (ValidationService) applicationContext.getBean(validatorName);
            return validationService;
        } catch (NoSuchBeanDefinitionException e) {
            throw new ValidatonServiceNotFoundException(validatorName + " not found");
        }
    }


    private ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setMimeType(proxyDocument.getDocumentType().getMimeType());
        return validationDocument;
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

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
