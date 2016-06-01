package ee.openeid.siva.validation.document.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SignatureValidationData {

    private String id;
    private String signatureFormat;
    private String signatureLevel;
    private String signedBy;
    private Indication indication;
    private String subIndication;
    private List<Error> errors;
    private List<SignatureScope> signatureScopes;
    private String claimedSigningTime;
    private List<Warning> warnings;
    private Info info;

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

        private Indication(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        @Override
        public String toString() {
            return stringRepresentation;
        }

    }
}
