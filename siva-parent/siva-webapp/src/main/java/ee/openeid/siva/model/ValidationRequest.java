package ee.openeid.siva.model;

public interface ValidationRequest {

    public String getBase64Document();
    public String getFilename();
    public String getType();
    public String getReportType();

}
