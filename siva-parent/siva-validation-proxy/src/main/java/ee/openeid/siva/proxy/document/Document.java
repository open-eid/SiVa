package ee.openeid.siva.proxy.document;

public interface Document {

    default ReportType getReportType() {
        throw new RuntimeException("Not supported");
    }

    default String getSignaturePolicy() {
        throw new RuntimeException("Not supported");
    }

    String getName();

    default byte[] getBytes() {
        throw new RuntimeException("Not supported");
    };

}
