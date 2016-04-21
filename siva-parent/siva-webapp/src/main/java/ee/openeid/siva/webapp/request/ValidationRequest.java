package ee.openeid.siva.webapp.request;

public interface ValidationRequest {

    String getDocument();
    String getFilename();
    String getDocumentType();
    String getReportType();

}
