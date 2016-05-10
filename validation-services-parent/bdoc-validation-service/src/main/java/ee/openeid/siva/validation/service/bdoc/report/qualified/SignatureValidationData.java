package ee.openeid.siva.validation.service.bdoc.report.qualified;

import lombok.Data;

import java.util.List;

@Data
public class SignatureValidationData {

    private String id;
    private String signatureFormat; //TODO: enum?
    private String signatureLevel; //TODO: enum?
    private String signedBy;
    private Indication indication; //TODO: enum? also sub indication?
    private List<Error> errors;
    private List<SignatureScope> signatureScopes;
    private String claimedSigningTime;
    private List<Warning> warnings;
    private Info info;
    private AdditionalValidation additionalValidation;

    public enum Indication {
        TOTAL_PASSED {
            @Override
            public String toString() {
                return "TOTAL-PASSED";
            }
        },
        TOTAL_FAILED {
            @Override
            public String toString() {
                return "TOTAL-FAILED";
            }
        },
        INDETERMINATE {
            @Override
            public String toString() {
                return this.name();
            }
        }
    }

}
