package ee.openeid.siva.proxy.document;

import eu.europa.esig.dss.MimeType;
import lombok.Data;

@Data
public class ProxyDocument {

    private byte[] bytes;

    private String name;

    protected MimeType mimeType;

    private String absolutePath = "WSDocument";

    private ReportType reportType;

    private RequestProtocol requestProtocol;

}
