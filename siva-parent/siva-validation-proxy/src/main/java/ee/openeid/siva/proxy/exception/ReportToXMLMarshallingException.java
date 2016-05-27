package ee.openeid.siva.proxy.exception;

public class ReportToXMLMarshallingException extends ReportMarshallingException {

    private static final String MESSAGE = "unable to create xml from validation report";

    public ReportToXMLMarshallingException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
