package ee.openeid.siva.proxy.exception;

public class ReportToJSONMarshallingException extends ReportMarshallingException {

    private static final String MESSAGE = "unable to create json from validation report";

    public ReportToJSONMarshallingException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
