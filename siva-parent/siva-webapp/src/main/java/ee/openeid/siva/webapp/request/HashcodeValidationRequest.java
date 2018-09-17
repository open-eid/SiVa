package ee.openeid.siva.webapp.request;

import java.util.List;

public interface HashcodeValidationRequest {

    String getSignatureFile();

    String getFilename();

    String getSignaturePolicy();

    String getReportType();

    List<Datafile> getDatafiles();

}
