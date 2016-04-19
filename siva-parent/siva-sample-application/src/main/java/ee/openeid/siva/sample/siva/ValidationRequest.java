package ee.openeid.siva.sample.siva;

import lombok.Data;

@Data
public class ValidationRequest {
    private FileType documentType;
    private ReportType reportType;
    private String filename;
    private String document;
}
