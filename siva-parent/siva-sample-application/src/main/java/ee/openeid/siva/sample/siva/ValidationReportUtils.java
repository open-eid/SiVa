/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.sample.siva;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationReportUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationReportUtils.class);
    private static final String INVALID_CONTAINER = "INVALID";
    private static final String VALID_CONTAINER = "VALID";
    private static final String ERROR_VALIDATION = "ERROR";
    private static final int GENERIC_ERROR = 101;

    public static String getValidateFilename(final String reportJSON) {
        if (isJSONNull(reportJSON)) {
            return StringUtils.EMPTY;
        }

        try {
            final String documentName = JsonPath.read(reportJSON, "$.documentName");
            return documentName == null ? "" : documentName;
        } catch (final PathNotFoundException ex) {
            LOGGER.warn("documentName not present in JSON: ", ex);
            return StringUtils.EMPTY;
        }
    }

    public static String getOverallValidationResult(final String reportJSON) {
        if (isJSONNull(reportJSON)) {
            LOGGER.warn("Report JSON is: null");
            return ERROR_VALIDATION;
        }

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

    public static String handleMissingJSON() throws JsonProcessingException {
        return new ObjectMapper().writer().writeValueAsString(new ServiceError(GENERIC_ERROR, "No JSON found in SiVa API response"));
    }

    public static boolean isJSONNull(String json) {
        return json == null;
    }
}
