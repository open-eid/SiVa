/*
 * Copyright 2018 - 2023 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.document.ProxyHashcodeDataSet;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.webapp.soap.HashDataFile;
import ee.openeid.siva.webapp.soap.SignatureFile;
import ee.openeid.siva.webapp.soap.SoapHashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SoapHashcodeValidationRequestToProxyDocumentTransformer {

    public ProxyHashcodeDataSet transform(SoapHashcodeValidationRequest validationRequest) {
        ProxyHashcodeDataSet proxyHashcodeDataSet = new ProxyHashcodeDataSet();

        setReportType(validationRequest, proxyHashcodeDataSet);
        proxyHashcodeDataSet.setSignaturePolicy(validationRequest.getSignaturePolicy());

        proxyHashcodeDataSet.setSignatureFiles(mapSignatureFiles(validationRequest.getSignatureFiles().getSignatureFile()));
        return proxyHashcodeDataSet;
    }

    private List<ee.openeid.siva.validation.document.SignatureFile> mapSignatureFiles(List<SignatureFile> requestSignatureFiles) {
        List<ee.openeid.siva.validation.document.SignatureFile> signatureFiles = new ArrayList<>();

        requestSignatureFiles.forEach(requestSignatureFile -> {
            ee.openeid.siva.validation.document.SignatureFile signatureFile = new ee.openeid.siva.validation.document.SignatureFile();
            signatureFile.setSignature(Base64.decodeBase64(requestSignatureFile.getSignature()));
            if (requestSignatureFile.getDataFiles() != null && CollectionUtils.isNotEmpty(requestSignatureFile.getDataFiles().getDataFile())) {
                signatureFile.setDatafiles(mapRequestDatafilesToProxyDocument(requestSignatureFile.getDataFiles().getDataFile()));
            }
            signatureFiles.add(signatureFile);
        });

        return signatureFiles;
    }


    private void setReportType(SoapHashcodeValidationRequest validationRequest, ProxyHashcodeDataSet proxyHashcodeDataSet) {
        if (validationRequest.getReportType() != null) {
            proxyHashcodeDataSet.setReportType(ReportType.reportTypeFromString(validationRequest.getReportType().name()));
        } else {
            proxyHashcodeDataSet.setReportType(ReportType.SIMPLE);
        }
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
