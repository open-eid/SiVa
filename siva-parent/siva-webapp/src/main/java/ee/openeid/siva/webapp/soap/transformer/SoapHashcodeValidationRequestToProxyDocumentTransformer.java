/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.webapp.soap.HashDataFile;
import ee.openeid.siva.webapp.soap.SoapHashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SoapHashcodeValidationRequestToProxyDocumentTransformer {

    public ProxyDocument transform(SoapHashcodeValidationRequest validationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();

        proxyDocument.setName(validationRequest.getFilename());

        proxyDocument.setBytes(Base64.decodeBase64(validationRequest.getSignatureFile()));

        if (validationRequest.getReportType() != null) {
            proxyDocument.setReportType(ReportType.reportTypeFromString(validationRequest.getReportType().name()));
        } else {
            proxyDocument.setReportType(ReportType.SIMPLE);
        }

        proxyDocument.setSignaturePolicy(validationRequest.getSignaturePolicy());

        List<Datafile> datafiles = mapRequestDatafilesToProxyDocument(validationRequest.getDataFiles().getDataFile());
        proxyDocument.setDatafiles(datafiles);

        return proxyDocument;
    }

    private List<Datafile> mapRequestDatafilesToProxyDocument(List<HashDataFile> requestDatafiles) {
        if (requestDatafiles == null || requestDatafiles.isEmpty()) {
            return Collections.emptyList();
        }
        return requestDatafiles.stream()
                .map(this::mapRequestDatafileToProxyDatafile)
                .collect(Collectors.toList());
    }

    private Datafile mapRequestDatafileToProxyDatafile(HashDataFile requestDatafile) {
        Datafile proxyDatafile = new Datafile();
        proxyDatafile.setFilename(requestDatafile.getFilename());
        proxyDatafile.setHash(requestDatafile.getHash());
        proxyDatafile.setHashAlgo(requestDatafile.getHashAlgo().value());
        return proxyDatafile;
    }
}
