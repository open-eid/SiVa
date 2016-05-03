package ee.openeid.siva.integrationtest.report.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleReport {

    @JsonProperty("ValidSignaturesCount")
    private int validSignaturesCount;

    @JsonProperty("Signature")
    @JsonDeserialize(using = SignatureDeserializer.class)
    private List<Signature> signatures;

    @JsonProperty("ValidationTime")
    private String validationTime;

    @JsonProperty("SignaturesCount")
    private int signaturesCount;


    public Optional<Error> findErrorById(String id) {
        return signatures
                .stream()
                .filter(s -> (s.getError() != null && s.getError().getNameId().equals(id)))
                .map(Signature::getError)
                .findFirst();
    }

    public Optional<Warning> findWarningById(String id) {
        return signatures
                .stream()
                .filter(s -> (s.getWarning() != null && s.getWarning().getNameId().equals(id)))
                .map(Signature::getWarning)
                .findFirst();
    }

}
