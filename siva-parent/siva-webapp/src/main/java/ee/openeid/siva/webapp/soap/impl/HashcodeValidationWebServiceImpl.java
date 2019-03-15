/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.proxy.HashcodeValidationProxy;
import ee.openeid.siva.validation.document.report.SimpleReport;
import ee.openeid.siva.webapp.soap.HashcodeValidationWebService;
import ee.openeid.siva.webapp.soap.SoapHashcodeValidationRequest;
import ee.openeid.siva.webapp.soap.response.ValidationReport;
import ee.openeid.siva.webapp.soap.transformer.SoapHashcodeValidationRequestToProxyDocumentTransformer;
import ee.openeid.siva.webapp.soap.transformer.ValidationReportSoapResponseTransformer;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.interceptor.OutFaultInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.Holder;

@OutFaultInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapFaultResponseInterceptor", "ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@SchemaValidation(type = SchemaValidation.SchemaValidationType.IN)
public class HashcodeValidationWebServiceImpl implements HashcodeValidationWebService {

    private HashcodeValidationProxy hashcodeValidationProxy;
    private SoapHashcodeValidationRequestToProxyDocumentTransformer hashRequestTransformer;
    private ValidationReportSoapResponseTransformer responseTransformer;

    @Override
    public void hashcodeValidationDocument(SoapHashcodeValidationRequest validationRequest, Holder<ValidationReport> validationReport, Holder<String> validationReportSignature) {
        SimpleReport simpleReport = hashcodeValidationProxy.validate(hashRequestTransformer.transform(validationRequest));
        validationReport.value = responseTransformer.toSoapResponse(simpleReport);
    }

    @Autowired
    public void setHashcodeValidationProxy(HashcodeValidationProxy hashcodeValidationProxy) {
        this.hashcodeValidationProxy = hashcodeValidationProxy;
    }

    @Autowired
    public void setHashRequestTransformer(SoapHashcodeValidationRequestToProxyDocumentTransformer hashRequestTransformer) {
        this.hashRequestTransformer = hashRequestTransformer;
    }

    @Autowired
    public void setResponseTransformer(ValidationReportSoapResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }
}
