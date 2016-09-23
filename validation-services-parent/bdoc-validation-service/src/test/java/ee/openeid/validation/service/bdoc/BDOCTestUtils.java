package ee.openeid.validation.service.bdoc;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;

final class BDOCTestUtils {
    static final String TEST_FILES_LOCATION = "test-files/";
    static final String VALID_BDOC_TM_2_SIGNATURES = "bdoc_tm_valid_2_signatures.bdoc";
    static final String TS_NO_MANIFEST = "asic-e-baseline-lt_allan_live_no_manifest.bdoc";
    static final String VALID_ID_CARD_MOB_ID = "Valid_IDCard_MobID_signatures.bdoc";
    static final String XROAD_BATCHSIGNATURE_CONTAINER = "xroad-batchsignature.asice";
    static final String XROAD_SIMPLE_CONTAINER = "xroad-simple.asice";
    static final String BDOC_TEST_OF_KLASS3_CHAIN = "etoken_CPP.bdoc";

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
