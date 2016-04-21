package ee.openeid.siva.webapp.request;

public interface ValidationRequest {

    String getBase64Document();
    String getFilename();
    String getDocumentType();
    String getReportType();

}
