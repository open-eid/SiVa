package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureScope {

    @XmlElement(name = "Name", required = true)
    private String name;

    @XmlElement(name = "Scope", required = true)
    private String scope;

    @XmlElement(name = "Content", required = true)
    private String content;
}
