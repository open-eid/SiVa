package ee.openeid.siva.webapp.soap;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.webapp.soap.transformer.QualifiedReportSoapResponseTransformer;
import ee.openeid.siva.webapp.soap.transformer.SoapValidationRequestToProxyDocumentTransformer;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutFaultInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

@InInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapRequestValidationInterceptor"})
@OutInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@OutFaultInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapFaultResponseInterceptor", "ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@SchemaValidation(type = SchemaValidation.SchemaValidationType.IN)
public class ValidationWebServiceImpl implements ValidationWebService {

    private ValidationProxy validationProxy;
    private SoapValidationRequestToProxyDocumentTransformer requestTransformer;
    private QualifiedReportSoapResponseTransformer responseTransformer;

    @Override
    public QualifiedReport validateDocument(SoapValidationRequest validationRequest) {
        ee.openeid.siva.validation.document.report.QualifiedReport qualifiedReport = validationProxy.validate(requestTransformer.transform(validationRequest));
        return responseTransformer.toSoapResponse(qualifiedReport);
    }


    @Autowired
    public void setValidationProxy(ValidationProxy validationProxy) {
        this.validationProxy = validationProxy;
    }

    @Autowired
    public void setRequestTransformer(SoapValidationRequestToProxyDocumentTransformer requestTransformer) {
        this.requestTransformer = requestTransformer;
    }

    @Autowired
    public void setResponseransformer(QualifiedReportSoapResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }

}
