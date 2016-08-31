package ee.openeid.siva.webapp.soap;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.webapp.soap.request.SOAPValidationRequest;
import ee.openeid.siva.webapp.transformer.ValidationRequestToProxyDocumentTransformer;
import org.apache.cxf.interceptor.InInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;

@InInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.SoapRequestValidationInterceptor"})
@WebService(serviceName = "SignatureValidationService", endpointInterface = "ee.openeid.siva.webapp.soap.ValidationWebService")
public class ValidationWebServiceImpl implements ValidationWebService {

    private ValidationProxy validationProxy;
    private ValidationRequestToProxyDocumentTransformer transformer;

    @Override
    public QualifiedReport validate(SOAPValidationRequest validationRequest) {
        return validationProxy.validate(transformer.transform(validationRequest));
    }

    @Autowired
    public void setValidationProxy(ValidationProxy validationProxy) {
        this.validationProxy = validationProxy;
    }

    @Autowired
    public void setTransformer(ValidationRequestToProxyDocumentTransformer transformer) {
        this.transformer = transformer;
    }
}
