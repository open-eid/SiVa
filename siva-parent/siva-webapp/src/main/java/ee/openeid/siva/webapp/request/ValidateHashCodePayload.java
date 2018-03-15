package ee.openeid.siva.webapp.request;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import ee.openeid.siva.proxy.document.ProxyHashCodeDocument;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.InMemoryDocument;
import lombok.Data;

@Data
public class ValidateHashCodePayload {

    private List<DetachedContent> detachedContents = new ArrayList<>();
    private List<SignatureContent> signatureContents = new ArrayList<>();
    private List<TimeStampTokenContent> timeStampTokenContents = new ArrayList<>();
    private String reportType;
    private String containerFileName;
    private String signaturePolicy;

    public List<ProxyHashCodeDocument> toDocumentList() {
        if (CollectionUtils.isNotEmpty(this.detachedContents)) {
            return this.detachedContents.stream().map(c -> {
                ProxyHashCodeDocument document = new ProxyHashCodeDocument();
                document.setFileName(c.getFileName());
                document.setBase64Digest(c.getBase64Digest());
                document.setDigestAlgorithm(c.getDigestAlgorithm());
                return document;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Map<String, DSSDocument> toSignatureDocumentList() {
        if (CollectionUtils.isNotEmpty(this.signatureContents)) {
            return this.signatureContents.stream().collect(Collectors.toMap(c -> c.getFileName(), c -> new InMemoryDocument(Base64.getDecoder().decode(c.getBase64Value()))));
        }
        return Collections.emptyMap();
    }

    public Map<String, DSSDocument> toTimeStampTokenList() {
        if (CollectionUtils.isNotEmpty(this.timeStampTokenContents)) {
            return this.timeStampTokenContents.stream().collect(Collectors.toMap(c -> c.getFileName(), c -> new InMemoryDocument(Base64.getDecoder().decode(c.getBase64Value()))));
        }
        return Collections.emptyMap();
    }

}
