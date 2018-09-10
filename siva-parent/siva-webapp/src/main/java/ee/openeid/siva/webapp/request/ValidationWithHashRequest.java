package ee.openeid.siva.webapp.request;

import java.util.List;

public interface ValidationWithHashRequest {

    String getSignature();

    String getFilename();

    String getSignaturePolicy();

    String getReportType();

    List<Datafile> getDatafiles();

}
