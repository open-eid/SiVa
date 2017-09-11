/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.webapp.soap.impl;

import ee.openeid.siva.proxy.ValidationProxy;
import ee.openeid.siva.webapp.soap.QualifiedReport;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import ee.openeid.siva.webapp.soap.ValidateDocumentResponse;
import ee.openeid.siva.webapp.soap.ValidationWebService;
import ee.openeid.siva.webapp.soap.transformer.QualifiedReportSoapResponseTransformer;
import ee.openeid.siva.webapp.soap.transformer.SoapValidationRequestToProxyDocumentTransformer;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutFaultInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.Holder;

@InInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapRequestValidationInterceptor"})
@OutInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@OutFaultInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapFaultResponseInterceptor", "ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@SchemaValidation(type = SchemaValidation.SchemaValidationType.IN)
public class ValidationWebServiceImpl implements ValidationWebService {

    private ValidationProxy validationProxy;
    private SoapValidationRequestToProxyDocumentTransformer requestTransformer;
    private QualifiedReportSoapResponseTransformer responseTransformer;

    @Override
    public void validateDocument(SoapValidationRequest validationRequest, Holder<QualifiedReport> validationReport, Holder<ee.openeid.siva.webapp.soap.DetailedReport> validationProcess) {
        ee.openeid.siva.validation.document.report.Report qualifiedReport = validationProxy.validate(requestTransformer.transform(validationRequest));
        ValidateDocumentResponse validateDocumentResponse = responseTransformer.toSoapResponse(qualifiedReport);
        validationReport.value = validateDocumentResponse.getValidationReport();
        validationProcess.value = validateDocumentResponse.getValidationProcess();
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
    public void setResponseTransformer(QualifiedReportSoapResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }


}
