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

import com.jayway.restassured.RestAssured;
import ee.openeid.siva.SivaWebApplication;
import ee.openeid.siva.integrationtest.configuration.IntegrationTest;
import ee.openeid.siva.proxy.document.DocumentType;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.openeid.siva.validation.document.report.SignatureValidationData;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SivaWebApplication.class, webEnvironment=RANDOM_PORT)
@ActiveProfiles("test")
public abstract class SiVaIntegrationTestsBase {

    private static final String PROJECT_SUBMODULE_NAME =  "siva-test";

    protected static final String VALID_SIGNATURE_POLICY_1 = "POLv1";
    protected static final String VALID_SIGNATURE_POLICY_2 = "POLv2";

    protected static final String INVALID_SIGNATURE_POLICY = "RUS";

    protected static final String SMALL_CASE_VALID_SIGNATURE_POLICY_1 = "polv1";
    protected static final String SMALL_CASE_VALID_SIGNATURE_POLICY_2 = "polv2";

    protected static final String POLICY_1_DESCRIPTION = "Policy for validating Electronic Signatures and Electronic " +
            "Seals regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014), " +
            "i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature " +
            "(AdES), AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) " +
            "does not change the total validation result of the signature.";
    protected static final String POLICY_2_DESCRIPTION = "Policy for validating Qualified Electronic Signatures and " +
            "Qualified Electronic Seals (according to Regulation (EU) No 910/2014). I.e. signatures that have been " +
            "recognized as Advanced electronic Signatures (AdES) and AdES supported by a Qualified Certificate " +
            "(AdES/QC) do not produce a positive validation result.";

    protected static final String POLICY_1_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv1";
    protected static final String POLICY_2_URL = "http://open-eid.github.io/SiVa/siva/appendix/validation_policy/#POLv2";

    @Value("${local.server.port}")
    protected int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected abstract String getTestFilesDirectory();

    protected byte[] readFileFromTestResources(String fileName) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + fileName);
    }

    protected void assertInvalidWithError(SignatureValidationData signatureValidationData, String expectedMessage) {
        boolean errorExists = signatureValidationData.getErrors()
                .stream()
                .anyMatch(error -> error.getContent().equals(expectedMessage));
        assertTrue(errorExists);
    }

    protected void assertHasWarning(SignatureValidationData signatureValidationData, String expectedMessage) {
        boolean warningExists = signatureValidationData.getWarnings()
                .stream()
                .anyMatch(warning -> warning.getDescription().equals(expectedMessage));
        assertTrue(warningExists);
    }

    protected void assertAllSignaturesAreValid(QualifiedReport report) {
        assertTrue(report.getSignaturesCount().equals(report.getValidSignaturesCount()));
    }

    protected void assertSomeSignaturesAreValid(QualifiedReport report, int expectedValidSignatures) {
        assertTrue(expectedValidSignatures == report.getValidSignaturesCount());
    }

    protected void assertAllSignaturesAreInvalid(QualifiedReport report) {
        assertTrue(report.getValidSignaturesCount() == 0);
    }

    protected static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String parseFileExtension(final String filename) {
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        if (isAsicExtension(fileExtension)) {
            return DocumentType.BDOC.name();
        }
        return resolveDocumentType(fileExtension);
    }

    protected String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0 , pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }

    private boolean isAsicExtension(String fileExtension) {
        return StringUtils.equalsIgnoreCase("asice", fileExtension);
    }

    private String resolveDocumentType(String fileExtension) {
        DocumentType documentType = Arrays.asList(DocumentType.values()).stream()
                .filter(fileType -> fileType.name().equalsIgnoreCase(fileExtension))
                .findFirst()
                .orElse(null);
        return documentType != null ? documentType.name() : fileExtension;
    }



}
