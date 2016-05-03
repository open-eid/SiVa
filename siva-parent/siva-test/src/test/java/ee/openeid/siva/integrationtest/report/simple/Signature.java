package ee.openeid.siva.integrationtest.report.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Signature {

    @JsonProperty("SignatureFormat")
    private String signatureFormat;

    @JsonProperty("SignedBy")
    private String signedBy;

    @JsonProperty("Indication")
    private String indication;

    @JsonProperty("Error")
    private Error error;

    @JsonProperty("Warning")
    private Warning warning;

    @JsonProperty("SubIndication")
    private String subIndication;

    @JsonProperty("SignatureLevel")
    private String signatureLevel;

}
