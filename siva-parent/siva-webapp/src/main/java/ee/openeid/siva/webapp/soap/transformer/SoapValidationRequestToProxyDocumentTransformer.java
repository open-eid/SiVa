/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ProxyHashCodeDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.proxy.document.typeresolver.DocumentTypeResolver;
import ee.openeid.siva.webapp.soap.SoapDigestValidationRequest;
import ee.openeid.siva.webapp.soap.SoapValidationRequest;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.InMemoryDocument;

@Component
public class SoapValidationRequestToProxyDocumentTransformer {

    public ProxyDocument transform(SoapValidationRequest validationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(validationRequest.getFilename());
        if (validationRequest.getDocumentType() != null)
            proxyDocument.setDocumentType(DocumentTypeResolver.documentTypeFromString(validationRequest.getDocumentType().name()));
        if (validationRequest.getReportType() != null)
            proxyDocument.setReportType(ReportType.reportTypeFromString(validationRequest.getReportType()));
        proxyDocument.setBytes(Base64.getDecoder().decode(validationRequest.getDocument()));
        proxyDocument.setSignaturePolicy(validationRequest.getSignaturePolicy());
        return proxyDocument;
    }

    public ProxyDocument transform(SoapDigestValidationRequest validationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();
        proxyDocument.setName(validationRequest.getFilename());
        proxyDocument.setSignaturePolicy(validationRequest.getSignaturePolicy());
        proxyDocument.setReportType(ReportType.valueOf(validationRequest.getReportType()));
        return proxyDocument;
    }

    public List<ProxyHashCodeDocument> transformToDetachedDocuments(SoapDigestValidationRequest digestValidationRequest) {
        if (digestValidationRequest.getContent() != null && CollectionUtils.isNotEmpty(digestValidationRequest.getContent().getDigestDocument())) {
            return digestValidationRequest.getContent().getDigestDocument().stream().map(c -> {
                ProxyHashCodeDocument document = new ProxyHashCodeDocument();
                document.setFileName(c.getFilename());
                document.setBase64Digest(c.getDigest());
                document.setDigestAlgorithm(c.getDigestAlgorithm());
                return document;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Map<String, DSSDocument> transformToSignatureDocuments(SoapDigestValidationRequest digestValidationRequest) {
        if (digestValidationRequest.getSignatures() != null && CollectionUtils.isNotEmpty(digestValidationRequest.getSignatures().getSignature())) {
            return digestValidationRequest.getSignatures().getSignature().stream().collect(Collectors.toMap(c -> c.getFilename(), c -> new InMemoryDocument(Base64.getDecoder().decode(c.getContent()))));
        }
        return Collections.emptyMap();
    }

    public Map<String, DSSDocument> transformToTimeStampTokenDocuments(SoapDigestValidationRequest digestValidationRequest) {
        if (digestValidationRequest.getTimeStampTokens() != null && CollectionUtils.isNotEmpty(digestValidationRequest.getTimeStampTokens().getTimeStampToken())) {
            return digestValidationRequest.getTimeStampTokens().getTimeStampToken().stream().collect(Collectors.toMap(c -> c.getFilename(), c -> new InMemoryDocument(Base64.getDecoder().decode(c.getContent()))));
        }
        return Collections.emptyMap();
    }

}
