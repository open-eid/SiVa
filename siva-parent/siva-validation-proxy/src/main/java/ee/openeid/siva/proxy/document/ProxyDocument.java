package ee.openeid.siva.proxy.document;

import lombok.Data;

@Data
public class ProxyDocument {

    private byte[] bytes;

    private String name;

    protected DocumentType documentType;

    private String absolutePath = "WSDocument";

    private ReportType reportType;

    private RequestProtocol requestProtocol;

}
