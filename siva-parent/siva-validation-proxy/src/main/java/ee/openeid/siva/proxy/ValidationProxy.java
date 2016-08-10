package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.exception.ValidatonServiceNotFoundException;
import ee.openeid.siva.proxy.http.RESTProxyService;
import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ValidationProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationProxy.class);
    private static final String SERVICE_BEAN_NAME_POSTFIX = "ValidationService";

    private RESTProxyService restProxyService;
    private ApplicationContext applicationContext;

    public QualifiedReport validate(ProxyDocument proxyDocument) {
        if (proxyDocument.getDocumentType() == DocumentType.XROAD) {
            return restProxyService.validate(createValidationDocument(proxyDocument));
        }

        return getServiceForType(proxyDocument.getDocumentType())
                .validateDocument(createValidationDocument(proxyDocument));
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
        validationDocument.setSignaturePolicy(proxyDocument.getSignaturePolicy());
        return validationDocument;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setRestProxyService(RESTProxyService restProxyService) {
        this.restProxyService = restProxyService;
    }
}
