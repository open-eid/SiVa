package ee.openeid.siva.webapp.soap.request;

import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.webapp.request.ValidationRequest;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "ValidationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOAPValidationRequest implements ValidationRequest {

    @XmlElement(name = "Document", required = true)
    private String document;

    @XmlElement(name = "Filename", required = true)
    private String filename;

    @XmlElement(name = "DocumentType", required = true)
    private DocumentType documentType;

    @XmlElement(name = "SignaturePolicy")
    private String signaturePolicy;

    public String getDocumentType() {
        return documentType.name();
    }

}
