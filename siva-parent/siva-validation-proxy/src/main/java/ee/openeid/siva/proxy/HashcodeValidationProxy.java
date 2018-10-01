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
