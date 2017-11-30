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

package ee.openeid.siva.integrationtest;

import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AsicsValidationFailIT extends SiVaRestTests {

    @Before
    public void DirectoryBackToDefault() {
        setTestFilesDirectory(DEFAULT_TEST_FILES_DIRECTORY);
    }

    private static final String DEFAULT_TEST_FILES_DIRECTORY = "asics/";

    private String testFilesDirectory = DEFAULT_TEST_FILES_DIRECTORY;

    public void setTestFilesDirectory(String testFilesDirectory) {
        this.testFilesDirectory = testFilesDirectory;
    }

    /**
     * TestCaseID: Asics-ValidationFail-1
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Only one datafile is allowed in ASIC-s
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: TwoDataFilesAsics.asics
     */
    @Test
    public void moreThanOneDataFileInAsicsShouldFail() {
        post(validationRequestFor("TwoDataFilesAsics.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-2
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: No data file in ASIC-s
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: DataFileMissingAsics.asics
     */
    @Test
    public void noDataFileInAsicsShouldFail() {
        post(validationRequestFor("DataFileMissingAsics.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-3
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: more folders that META-INF in ASIC-s
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: FoldersInAsics.asics
     */
    @Test
    public void additionalFoldersInAsicsShouldFail() {
        post(validationRequestFor("FoldersInAsics.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-4
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: META-INF folder not in root of container
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: MetaInfNotInRoot.asics
     */
    @Test
    public void metaInfFolderNotInRootAsicsShouldFail() {
        post(validationRequestFor("MetaInfNotInRoot.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-5
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Not allowed files in META-INF folder
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: signatureMixedWithTST.asics
     */
    @Ignore //TODO: SIVARIA2-93
    @Test
    public void signatureFilesInAddtionToTstAsicsShouldFail() {
        post(validationRequestFor("signatureMixedWithTST.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-6
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: TST not intact
     * <p>
     * Expected Result: The validation should fail
     * <p>

     * File: AsicsTSTsignatureModified.asics
     */
    @Test
    public void modifiedTstShouldFail() {
        post(validationRequestFor("AsicsTSTsignatureModified.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("AsicsTSTsignatureModified.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("D37CA39742DABC04B6A57797DF94DEE8AB2F06E0833ABD1DC6EAF8CD07E22C7C"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].error[0].content", Matchers.is("Signature not intact"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-7
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: TST has been corrupted
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: AsicsTSTsignatureBroken.asics
     */
    @Ignore //TODO: SIVARIA2-94, exact error message not known yet.
    @Test
    public void brokenTstAsicsShouldFail() {
        post(validationRequestFor("AsicsTSTsignatureBroken.asics"))
                .then()
                .body("requestErrors[0].key", Matchers.is("document"))
                .body("requestErrors[0].message", Matchers.is("Document does not meet the requirements"));
    }

    /**
     * TestCaseID: Asics-ValidationFail-8
     * <p>
     * TestType: Automated
     * <p>
     * Requirement:
     * <p>
     * Title: Data file changed
     * <p>
     * Expected Result: The validation should fail
     * <p>
     * File: DatafileAlteredButStillValid.asics
     */
    @Test
    public void dataFileChangedAsicsShouldFail() {
        post(validationRequestFor("DatafileAlteredButStillValid.asics"))
                .then()
                .body("validationReport.validationConclusion.signatureForm", Matchers.is("ASiC-S"))
                .body("validationReport.validationConclusion.validatedDocument.filename", Matchers.is("DatafileAlteredButStillValid.asics"))
                .body("validationReport.validationConclusion.validatedDocument.fileHashInHex", Matchers.is("80D0D18045E2A8C41A4197C542DDDE244A829A3D221CDCF2B03A656AB27B3EEA"))
                .body("validationReport.validationConclusion.timeStampTokens[0].indication", Matchers.is("TOTAL-FAILED"))
                .body("validationReport.validationConclusion.timeStampTokens[0].error[0].content", Matchers.is("Signature not intact"));
    }

    @Override
    protected String getTestFilesDirectory() {
        return testFilesDirectory;
    }
}
