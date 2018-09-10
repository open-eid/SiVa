package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ProxyDocument;
import ee.openeid.siva.validation.document.Datafile;
import ee.openeid.siva.validation.document.ValidationDocument;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashcodeValidationProxy extends ValidationProxy {

    private static final String HASHCODE_GENERIC_SERVICE = "hashcodeGeneric";

    @Override
    protected String constructValidatorName(ProxyDocument proxyDocument) {
        return HASHCODE_GENERIC_SERVICE + SERVICE_BEAN_NAME_POSTFIX;
    }

    @Override
    protected ValidationDocument createValidationDocument(ProxyDocument proxyDocument) {
        ValidationDocument validationDocument = super.createValidationDocument(proxyDocument);
        validationDocument.setDatafiles(mapProxyDatafilesToValidationDocument(proxyDocument.getDatafiles()));
        return validationDocument;
    }

    private List<Datafile> mapProxyDatafilesToValidationDocument(List<ee.openeid.siva.proxy.document.Datafile> proxyDatafiles) {
        if (proxyDatafiles.isEmpty()) {
            return Collections.emptyList();
        }
        return proxyDatafiles.stream()
                .map(this::mapRequestDatafileToProxyDatafile)
                .collect(Collectors.toList());
    }

    private ee.openeid.siva.validation.document.Datafile  mapRequestDatafileToProxyDatafile(ee.openeid.siva.proxy.document.Datafile requestDatafile) {
        ee.openeid.siva.validation.document.Datafile validationDatafile = new ee.openeid.siva.validation.document.Datafile ();
        validationDatafile.setFilename(requestDatafile.getFilename());
        validationDatafile.setHash(requestDatafile.getHash());
        validationDatafile.setHashAlgo(requestDatafile.getHashAlgo());
        return validationDatafile;
    }
}
