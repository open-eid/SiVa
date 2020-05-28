package ee.openeid.siva.validation.document.report;

import lombok.Data;

@Data
public class SignatureProductionPlace {
    private String countryName;
    private String stateOrProvince;
    private String city;
    private String postalCode;
}
