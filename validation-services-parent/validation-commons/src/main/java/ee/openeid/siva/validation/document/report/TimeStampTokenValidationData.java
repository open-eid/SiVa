package ee.openeid.siva.validation.document.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TimeStampTokenValidationData {
    private Indication indication;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String signedBy;
    private String signedTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Error> error;

    public enum Indication {
        @JsonProperty("TOTAL-PASSED")
        TOTAL_PASSED("TOTAL-PASSED"),

        @JsonProperty("TOTAL-FAILED")
        TOTAL_FAILED("TOTAL-FAILED");

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
