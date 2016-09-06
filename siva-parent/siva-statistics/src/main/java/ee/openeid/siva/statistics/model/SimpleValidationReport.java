package ee.openeid.siva.statistics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({ "type", "dur", "sigCt", "vSigCt", "sigRslt" })
public class SimpleValidationReport {

    @JsonProperty("dur")
    private Long duration;

    @JsonProperty("sigCt")
    private Integer signatureCount;

    @JsonProperty("vSigCt")
    private Integer validSignatureCount;

    @JsonProperty("sigRslt")
    private List<SimpleSignatureReport> simpleSignatureReports;

    @JsonProperty("type")
    private String containerType;

}
