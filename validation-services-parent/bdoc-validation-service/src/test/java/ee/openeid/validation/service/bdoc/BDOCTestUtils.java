package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;

final class BDOCTestUtils {
    static final String TEST_FILES_LOCATION = "test-files/";
    static final String VALID_BDOC_TM_2_SIGNATURES = "bdoc_tm_valid_2_signatures.bdoc";
    static final String TS_NO_MANIFEST = "asic-e-baseline-lt_allan_live_no_manifest.bdoc";

    private BDOCTestUtils() {
    }

    static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }
}
