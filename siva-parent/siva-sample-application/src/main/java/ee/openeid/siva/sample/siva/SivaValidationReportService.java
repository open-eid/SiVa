package ee.openeid.siva.sample.siva;

import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Service;

@Service
public class SivaValidationReportService {

    public String getValidateFilename(final String reportJSON) {
        return JsonPath.read(reportJSON, "$.documentName");
    }

    public String getOverallValidationResult(final String reportJSON) {
        final int validSignatureCount = JsonPath.read(reportJSON, "$.validSignaturesCount");
        final int totalSignatureCount = JsonPath.read(reportJSON, "$.signaturesCount");

        return validSignatureCount == totalSignatureCount ?  "VALID" : "INVALID";
    }
}
