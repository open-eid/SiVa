package ee.openeid.siva.sample.siva;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ValidationReportUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationReportUtils.class);
    private static final String INVALID_CONTAINER = "INVALID";
    private static final String VALID_CONTAINER = "VALID";
    private static final String ERROR_VALIDATION = "ERROR";

    private ValidationReportUtils() {
    }

    public static String getValidateFilename(final String reportJSON) {
        try {
            final String documentName = JsonPath.read(reportJSON, "$.documentName");
            return documentName == null ? "" : documentName;
        } catch (final PathNotFoundException ex) {
            LOGGER.warn("documentName not present in JSON: ", ex);
            return "";
        }
    }

    public static String getOverallValidationResult(final String reportJSON) {
        try {
            final Integer validSignatureCount = JsonPath.read(reportJSON, "$.validSignaturesCount");
            final Integer totalSignatureCount = JsonPath.read(reportJSON, "$.signaturesCount");
            if (validSignatureCount == null || totalSignatureCount == null) {
                LOGGER.warn("No validSignatureCount or totalSignatureCount present in response JSON");
                return ERROR_VALIDATION;
            }

            return validSignatureCount.equals(totalSignatureCount) && totalSignatureCount > 0 ? VALID_CONTAINER : INVALID_CONTAINER;
        } catch (final PathNotFoundException ex) {
            LOGGER.warn("JSON parsing failed when validating overall validation result: ", ex);
            return INVALID_CONTAINER;
        }
    }
}
