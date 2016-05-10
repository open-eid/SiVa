package ee.openeid.siva.validation.service.bdoc.report.digidoc4j;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name="ValidationReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class DigiDoc4JReportModel {

    @XmlElement(name = "SignatureValidation")
    private List<SignatureValidation> signatureValidations;


    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class SignatureValidation {
        @XmlElement(name = "ValidationTime")
        private String validationTime;

        @XmlElement(name = "DocumentName")
        private String documentName;

        @XmlElement(name = "Policy")
        private Policy policy;

        @XmlElement(name = "Signature")
        private Signature signature;

    }


    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Policy {
        @XmlElement(name="PolicyName")
        private String policyName;

        @XmlElement(name="PolicyDescription")
        private String policyDescription;
    }


    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Signature {
        @XmlAttribute(name = "Id")
        private String id;

        @XmlAttribute(name = "SignatureFormat")
        private String format;

        @XmlElement(name="SigningTime")
        private String signingTime;

        @XmlElement(name="SignedBy")
        private String signedBy;

        @XmlElement(name="Indication")
        private String indication;

        @XmlElement(name="SignatureLevel")
        private String level;

        @XmlElementWrapper(name = "SignatureScopes")
        @XmlElement(name="SignatureScope")
        private List<SignatureScope> signatureScopes;
    }


    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class SignatureScope {
        @XmlAttribute(name = "name")
        private String name;

        @XmlAttribute(name = "scope")
        private String scope;

        @XmlValue
        private String content;
    }

}

