/*
 * Copyright 2017 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp;

import ee.openeid.siva.proxy.DataFilesProxy;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.webapp.request.JSONDataFilesRequest;
import ee.openeid.siva.webapp.transformer.DataFilesRequestToProxyDocumentTransformer;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static ee.openeid.siva.webapp.util.ResponseHeaderUtils.setContentDispositionHeader;

@RestController
public class DataFilesController {

    private DataFilesProxy dataFilesProxy;
    private DataFilesRequestToProxyDocumentTransformer transformer;

    @PostMapping(value = "/getDataFiles", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataFilesReport getDataFiles(@Valid @RequestBody JSONDataFilesRequest dataFilesRequest, HttpServletResponse response) {
        setContentDispositionHeader(response);
        return dataFilesProxy.getDataFiles(transformer.transform(dataFilesRequest));
    }

    @Autowired
    public void setDataFilesProxy(DataFilesProxy dataFilesProxy) {
        this.dataFilesProxy = dataFilesProxy;
    }

    @Autowired
    public void setDataFilesTransformer(DataFilesRequestToProxyDocumentTransformer transformer) {
        this.transformer = transformer;
    }
}
