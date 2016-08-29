package ee.openeid.siva.validation.document.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SignatureValidationData {

    @XmlElement(name = "Id", required = true)
    private String id;

    @XmlElement(name = "SignatureFormat", required = true)
    private String signatureFormat;

    @XmlElement(name = "SignatureLevel", required = true)
    private String signatureLevel;

    @XmlElement(name = "SignedBy", required = true)
    private String signedBy;

    @XmlElement(name = "Indication", required = true)
    private Indication indication;

    @XmlElement(name = "SubIndication", required = true)
    private String subIndication;

    @XmlElement(name = "Error")
    @XmlElementWrapper(name = "Errors", required = true)
    private List<Error> errors;

    @XmlElement(name = "SignatureScope", required = true)
    @XmlElementWrapper(name = "SignatureScopes", required = true)
    private List<SignatureScope> signatureScopes;

    @XmlElement(name = "ClaimedSigningTime", required = true)
    private String claimedSigningTime;

    @XmlElement(name = "Warning")
    @XmlElementWrapper(name = "Warnings", required = true)
    private List<Warning> warnings;

    @XmlElement(name = "Info", required = true)
    private Info info;

    @JsonIgnore
    @XmlTransient
    private String countryCode;

    public String getIndication() {
        return indication.toString();
    }

    public enum Indication {
        @JsonProperty("TOTAL-PASSED")
        TOTAL_PASSED("TOTAL-PASSED"),

        @JsonProperty("TOTAL-FAILED")
        TOTAL_FAILED("TOTAL-FAILED"),

        @JsonProperty("INDETERMINATE")
        INDETERMINATE("INDETERMINATE");

        private final String stringRepresentation;

        Indication(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }

    }
}
