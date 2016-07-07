package ee.openeid.siva.webapp.soap.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static ee.openeid.siva.webapp.soap.ValidationWebService.SIVA_NAMESPACE;

@Data
@XmlRootElement(name = "ValidateDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateDocument {

    @XmlElement(name = "ValidationRequest", namespace = SIVA_NAMESPACE, required = true)
    private SOAPValidationRequest validationRequest;
}
