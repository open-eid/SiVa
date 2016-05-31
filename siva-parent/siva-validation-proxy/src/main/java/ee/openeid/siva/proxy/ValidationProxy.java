package ee.openeid.siva.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.RequestProtocol;
import ee.openeid.siva.proxy.exception.ReportToJSONMarshallingException;
import ee.openeid.siva.proxy.exception.ReportToXMLMarshallingException;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationProxy.class);
    private static final String SERVICE_BEAN_NAME_POSTFIX = "ValidationService";

    private ApplicationContext applicationContext;

    public String validate(final ProxyDocument proxyDocument) {
        ValidationService validationService = getServiceForType(proxyDocument.getDocumentType());
        QualifiedReport report = validationService.validateDocument(createValidationDocument(proxyDocument));
        if (proxyDocument.getRequestProtocol() == RequestProtocol.JSON) {
            return toJSON(report);
        }
        return toXML(report);
    }

    private ValidationService getServiceForType(DocumentType documentType) {
        String validatorName = constructValidatorName(documentType);
        try {
            return (ValidationService) applicationContext.getBean(validatorName);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.error("{} not found", validatorName, e);
            throw new ValidatonServiceNotFoundException(validatorName + " not found");
        }
    }

    private static String constructValidatorName(DocumentType documentType) {
        return documentType.name() + SERVICE_BEAN_NAME_POSTFIX;
    }

    private ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = new ValidationDocument();
        validationDocument.setName(proxyDocument.getName());
        validationDocument.setBytes(proxyDocument.getBytes());
        validationDocument.setMimeType(proxyDocument.getDocumentType().getMimeType());
        return validationDocument;
    }

    private static String toXML(QualifiedReport report) {
        try {
            final Marshaller xmlMarshaller = JAXBContext.newInstance(QualifiedReport.class).createMarshaller();
            StringWriter sw = new StringWriter();
            xmlMarshaller.marshal(report, sw);
            return sw.toString();
        } catch (JAXBException e) {
            LOGGER.error("creating xml from qualified report failed", e);
            throw new ReportToXMLMarshallingException(e);
        }
    }

    private static String toJSON(QualifiedReport report) {
        final ObjectMapper jsonObjectMapper = new ObjectMapper();
        try {
            return jsonObjectMapper.writeValueAsString(report);
        } catch (JsonProcessingException e) {
            LOGGER.error("creating json from qualified report failed", e);
            throw new ReportToJSONMarshallingException(e);
        }
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
