/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.proxy.http;

import org.springframework.http.HttpStatus;

public class RESTValidationProxyException extends RuntimeException {

    private final RESTProxyError proxyError;

    public RESTValidationProxyException(RESTProxyError proxyError) {
        super(proxyError.getMessage());
        this.proxyError = proxyError;
    }

    public String getErrorKey() {
        return proxyError.getKey();
    }

    public String getErrorMessage() {
        return proxyError.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return proxyError.getHttpStatus();
    }
}
