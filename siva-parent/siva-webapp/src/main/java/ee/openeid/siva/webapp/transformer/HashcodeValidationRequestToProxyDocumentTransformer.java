package ee.openeid.siva.webapp.transformer;

import ee.openeid.siva.proxy.document.Datafile;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
import ee.openeid.siva.webapp.request.HashcodeValidationRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HashcodeValidationRequestToProxyDocumentTransformer {

    public ProxyDocument transform(HashcodeValidationRequest hashcodeValidationRequest) {
        ProxyDocument proxyDocument = new ProxyDocument();

        proxyDocument.setName(hashcodeValidationRequest.getFilename());

        proxyDocument.setBytes(Base64.decodeBase64(hashcodeValidationRequest.getSignatureFile()));

        if (hashcodeValidationRequest.getReportType() != null) {
            proxyDocument.setReportType(ReportType.reportTypeFromString(hashcodeValidationRequest.getReportType()));
        } else {
            proxyDocument.setReportType(ReportType.SIMPLE);
        }

        proxyDocument.setSignaturePolicy(hashcodeValidationRequest.getSignaturePolicy());

        List<Datafile> datafiles = mapRequestDatafilesToProxyDocument(hashcodeValidationRequest.getDatafiles());
        proxyDocument.setDatafiles(datafiles);

        return proxyDocument;
    }

    private List<Datafile> mapRequestDatafilesToProxyDocument(List<ee.openeid.siva.webapp.request.Datafile> requestDatafiles) {
        if (requestDatafiles == null || requestDatafiles.isEmpty()) {
            return Collections.emptyList();
        }
        return requestDatafiles.stream()
                .map(this::mapRequestDatafileToProxyDatafile)
                .collect(Collectors.toList());
    }

    private Datafile mapRequestDatafileToProxyDatafile(ee.openeid.siva.webapp.request.Datafile requestDatafile) {
        Datafile proxyDatafile = new Datafile();
        proxyDatafile.setFilename(requestDatafile.getFilename());
        proxyDatafile.setHash(requestDatafile.getHash());
        proxyDatafile.setHashAlgo(requestDatafile.getHashAlgo());
        return proxyDatafile;
    }
}
