package ee.openeid.siva.sample.controller;

import lombok.Data;

@Data
public class ValidationResponse {
    private String filename;
    private String jsonValidationResult;
    private String soapValidationResult;
    private String overAllValidationResult;
}
