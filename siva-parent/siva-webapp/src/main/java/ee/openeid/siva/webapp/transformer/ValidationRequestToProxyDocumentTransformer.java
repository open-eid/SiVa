/*
 * Copyright 2017 - 2025 Riigi Infosüsteemi Amet
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

package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.request.ValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class ValidationRequestToProxyDocumentTransformer {

    public ProxyDocument transform(ValidationRequest validationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();

        proxyDocument.setName(validationRequest.getFilename());
        proxyDocument.setBytes(Base64.decodeBase64(validationRequest.getDocument()));
        proxyDocument.setSignaturePolicy(validationRequest.getSignaturePolicy());

        setReportType(validationRequest, proxyDocument);
        return proxyDocument;
    }

    private void setReportType(ValidationRequest validationRequest, ProxyDocument proxyDocument) {
        if (validationRequest.getReportType() != null) {
            proxyDocument.setReportType(ReportType.reportTypeFromString(validationRequest.getReportType()));
        } else {
            proxyDocument.setReportType(ReportType.SIMPLE);
        }
    }

}
