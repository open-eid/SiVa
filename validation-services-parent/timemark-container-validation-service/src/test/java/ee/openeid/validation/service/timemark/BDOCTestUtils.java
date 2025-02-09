/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

package ee.openeid.validation.service.timemark;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
final class BDOCTestUtils {
    static final String TEST_FILES_LOCATION = "test-files/";
    static final String VALID_BDOC_TM_2_SIGNATURES = "bdoc_tm_valid_2_signatures.bdoc";
    static final String VALID_ID_CARD_MOB_ID = "Valid_IDCard_MobID_signatures.bdoc";
    static final String VALID_BALTIC_EST_LT = "Baltic MoU digital signing_EST_LT.bdoc";
    static final String ASICE_CRL_ONLY = "asic-with-crl-and-without-ocsp.asice";
    static final String VALID_ASICE = "valid-bdoc-ts.asice";
    static final String BDOC_TEST_OF_KLASS3_CHAIN = "etoken_CPP.bdoc";
    static final String BDOC_TEST_FILE_UNSIGNED = "3f_2s_1f_unsigned.bdoc";
    static final String BDOC_TEST_FILE_ALL_SIGNED = "2f_all_signed.bdoc";
    static final String BDOC_TEST_FILE_T_LEVEL_SIGNATURE = "TS-05_23634_TS_unknown_TSA.bdoc";
    static final String ASICE_TEST_FILE_LTA_LEVEL_SIGNATURE = "valid-asice-lta.asice";
    static final String BDOC_TEST_FILE_LTA_LEVEL_SIGNATURE = "test_lta_multiple_timestamps.bdoc";

    static ValidationDocument buildValidationDocument(String testFile) {
        return DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(TEST_FILES_LOCATION + testFile)
                .withName(testFile)
                .build();
    }
}
