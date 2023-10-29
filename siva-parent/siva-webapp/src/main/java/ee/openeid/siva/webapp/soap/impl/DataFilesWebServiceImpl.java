/*
 * Copyright 2017 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.DataFilesProxy;
import ee.openeid.siva.webapp.soap.DataFilesReport;
import ee.openeid.siva.webapp.soap.DataFilesWebService;
import ee.openeid.siva.webapp.soap.SoapDataFilesRequest;
import ee.openeid.siva.webapp.soap.transformer.DataFilesReportSoapResponseTransformer;
import ee.openeid.siva.webapp.soap.transformer.SoapDataFilesRequestToProxyDocumentTransformer;
import org.apache.cxf.annotations.SchemaValidation;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutFaultInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

@InInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapRequestDataFilesInterceptor"})
@OutInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@OutFaultInterceptors(interceptors = {"ee.openeid.siva.webapp.soap.interceptor.SoapFaultResponseInterceptor", "ee.openeid.siva.webapp.soap.interceptor.SoapResponseHeaderInterceptor"})
@SchemaValidation(type = SchemaValidation.SchemaValidationType.IN)
public class DataFilesWebServiceImpl implements DataFilesWebService {

    private DataFilesProxy dataFilesProxy;
    private SoapDataFilesRequestToProxyDocumentTransformer requestTransformer;
    private DataFilesReportSoapResponseTransformer responseTransformer;

    @Override
    public DataFilesReport getDocumentDataFiles(SoapDataFilesRequest dataFilesRequest) {
        ee.openeid.siva.validation.document.report.DataFilesReport  dataFilesReport = dataFilesProxy.getDataFiles(requestTransformer.transform(dataFilesRequest));
        return responseTransformer.toSoapResponse(dataFilesReport);
    }

    @Autowired
    public void setRequestTransformer(SoapDataFilesRequestToProxyDocumentTransformer requestTransformer) {
        this.requestTransformer = requestTransformer;
    }

    @Autowired
    public void setResponseransformer(DataFilesReportSoapResponseTransformer responseTransformer) {
        this.responseTransformer = responseTransformer;
    }

    @Autowired
    public void setDataFilesProxy(DataFilesProxy dataFilesProxy) {
        this.dataFilesProxy = dataFilesProxy;
    }

}
