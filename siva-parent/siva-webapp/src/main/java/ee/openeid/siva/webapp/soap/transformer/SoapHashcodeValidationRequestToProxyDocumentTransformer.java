package ee.openeid.siva.webapp.soap.transformer;

import ee.openeid.siva.proxy.document.Datafile;
import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.proxy.document.ReportType;
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
