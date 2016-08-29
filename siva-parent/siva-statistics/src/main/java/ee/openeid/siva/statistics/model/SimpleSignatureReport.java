package ee.openeid.siva.statistics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "i", "si", "cc" })
public class SimpleSignatureReport {

    @JsonProperty("i")
    private String indication;

    @JsonProperty("si")
    private String subIndication;

    @JsonProperty("cc")
    private String countryCode;

}
