package ee.openeid.siva.proxy.http;

import org.springframework.http.HttpStatus;

public class RESTValidationProxyException extends RuntimeException {

    private final RESTProxyError proxyError;

    public RESTValidationProxyException(RESTProxyError proxyError) {
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
