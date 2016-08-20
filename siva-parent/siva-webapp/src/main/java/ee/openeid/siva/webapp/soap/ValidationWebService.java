package ee.openeid.siva.webapp.soap;

import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.webapp.soap.request.SOAPValidationRequest;
import org.apache.cxf.annotations.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService
public interface ValidationWebService {

    String SIVA_NAMESPACE = "http://soap.webapp.siva.openeid.ee/";

    @WebMethod(operationName = "ValidateDocument")
    @WebResult(name = "ValidationReport", targetNamespace = SIVA_NAMESPACE)
    @RequestWrapper(localName = "ValidateDocument", targetNamespace = SIVA_NAMESPACE, className = "ee.openeid.siva.webapp.soap.request.ValidateDocument")
    @ResponseWrapper(localName = "ValidateDocumentResponse", targetNamespace = SIVA_NAMESPACE)
    @SchemaValidation(type = SchemaValidation.SchemaValidationType.IN)
    QualifiedReport validate(@WebParam(name = "ValidationRequest", targetNamespace = SIVA_NAMESPACE) SOAPValidationRequest validationRequest);
}
