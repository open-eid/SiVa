package ee.openeid.siva.validation.document.report;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Warning {

    @XmlElement(name = "NameId", required = true)
    private String nameId;

    @XmlElement(name = "Description", required = true)
    private String description;
}
