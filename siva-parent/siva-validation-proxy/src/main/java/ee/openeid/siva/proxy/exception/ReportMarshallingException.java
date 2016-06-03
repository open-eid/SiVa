package ee.openeid.siva.proxy.exception;

/**
 * Base exception for marshalling report to xml or json
 */
public class ReportMarshallingException extends RuntimeException {
    public ReportMarshallingException(String message, Throwable cause) {
        super(message, cause);
    }
}
