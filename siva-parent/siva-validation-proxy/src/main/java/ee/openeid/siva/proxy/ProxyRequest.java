package ee.openeid.siva.proxy;

import ee.openeid.siva.proxy.document.ReportType;
import lombok.Data;

@Data
public abstract class ProxyRequest {

    private String signaturePolicy;

    private ReportType reportType;
}
