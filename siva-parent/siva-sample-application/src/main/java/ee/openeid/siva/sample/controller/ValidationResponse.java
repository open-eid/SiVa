package ee.openeid.siva.sample.controller;

import lombok.Data;

@Data
public class ValidationResponse {
    private String filename;
    private String validationResult;
    private String overAllValidationResult;
}
