/*
 * Copyright 2018 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.validation.document.report.SimpleReport;
import io.restassured.RestAssured;
import org.springframework.test.web.servlet.RequestBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public abstract class SiVaIntegrationTestsBase {

    protected static final String DOCUMENT_MALFORMED_OR_NOT_MATCHING_DOCUMENT_TYPE = "Document malformed or not matching documentType";
    protected static final String INVALID_DOCUMENT_TYPE = "Invalid document type";
    protected static final String INVALID_DATA_FILE_FILENAME = "Invalid filename. Can only return data files for DDOC type containers.";
    protected static final String INVALID_FILENAME = "Invalid filename";
    protected static final String INVALID_DATAFILES_LIST = "Invalid dataFiles list";
    protected static final String INVALID_FILENAME_SIZE = "size must be between 1 and 260";
    protected static final String INVALID_HASH_SIZE = "size must be between 1 and 1000";
    protected static final String INVALID_POLICY_SIZE = "size must be between 1 and 100";
    protected static final String INVALID_REPORT_TYPE = "Invalid report type";
    protected static final String INVALID_HASH_ALGO = "Invalid hash algorithm";
    protected static final String MAY_NOT_BE_EMPTY = "may not be empty";
    protected static final String MAY_NOT_BE_NULL = "may not be null";
    protected static final String INVALID_BASE_64 = "Document is not encoded in a valid base64 string";
    protected static final String SIGNATURE_FILE_NOT_BASE64_ENCODED = "Signature file is not valid base64 encoded string";
    protected static final String SIGNATURE_MALFORMED = "Signature file malformed";
    protected static final String INVALID_SIGNATURE_POLICY = "Invalid signature policy";
    protected static final String SIGNATURE_FILE_MALFORMED = "Signature file malformed";
    protected static final String DOCUMENT_TYPE = "documentType";
    protected static final String FILENAME = "filename";
    protected static final String DOCUMENT = "document";
    protected static final String SIGNATURE_POLICY = "signaturePolicy";
    protected static final String REPORT_TYPE = "reportType";
    protected static final String SIGNATURE_INDEX_0 = "signatureFiles[0].signature";
    protected static final String SIGNATURE = "signatureFiles.signature";
    protected static final String SIGNATURE_FILES = "signatureFiles";
    protected static final String DATAFILES = "signatureFiles[0].datafiles";
    protected static final String DATAFILES_FILENAME = "signatureFiles[0].datafiles[0].filename";
    protected static final String DATAFILES_HASH = "signatureFiles[0].datafiles[0].hash";
    protected static final String DATAFILES_HASH_ALGO = "signatureFiles[0].datafiles[0].hashAlgo";

    private static final String PROJECT_SUBMODULE_NAME = "siva-test";

    protected static final String VALID_SIGNATURE_POLICY_3 = "POLv3";
    protected static final String VALID_SIGNATURE_POLICY_4 = "POLv4";

    protected static final String SMALL_CASE_VALID_SIGNATURE_POLICY_3 = "polv3";

    protected static final String POLICY_3_DESCRIPTION = "Policy for validating Electronic Signatures and Electronic Seals " +
            "regardless of the legal type of the signature or seal (according to Regulation (EU) No 910/2014, aka eIDAS), " +
            "i.e. the fact that the electronic signature or electronic seal is either Advanced electronic Signature (AdES)," +
            " AdES supported by a Qualified Certificate (AdES/QC) or a Qualified electronic Signature (QES) does not change " +
            "the total validation result of the signature. Signatures which are not compliant with ETSI standards (referred by" +
            " Regulation (EU) No 910/2014) may produce unknown or invalid validation result. Validation process is based on " +
            "eIDAS Article 32, Commission Implementing Decision (EU) 2015/1506 and referred ETSI standards.";
    protected static final String POLICY_4_DESCRIPTION = "Policy according most common requirements of Estonian Public " +
            "Administration, to validate Qualified Electronic Signatures and Electronic Seals with Qualified Certificates" +
            " (according to Regulation (EU) No 910/2014, aka eIDAS). I.e. signatures that have been recognized as Advanced" +
            " electronic Signatures (AdES) and AdES supported by a Qualified Certificate (AdES/QC) do not produce a positive" +
            " validation result, with exception for seals, where AdES/QC and above will produce positive result. Signatures" +
            " and Seals which are not compliant with ETSI standards (referred by eIDAS) may produce unknown or invalid validation" +
            " result. Validation process is based on eIDAS Article 32 and referred ETSI standards.";

    protected static final String POLICY_3_URL = "http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv3";
    protected static final String POLICY_4_URL = "http://open-eid.github.io/SiVa/siva3/appendix/validation_policy/#POLv4";

    protected abstract String getTestFilesDirectory();

    protected static Map<String, Object> yamlMaps;

    static {
        Yaml yaml = new Yaml();
        try {
            ClassLoader classLoader = RequestBuilder.class.getClassLoader();
            String path = classLoader.getResource("application-test.yml").getPath();
            yamlMaps = yaml.load(new FileInputStream(new File(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();
    }

    public String createUrl(String endpoint) {
        LinkedHashMap sivaMap = (LinkedHashMap) yamlMaps.get("siva");
        return sivaMap.get("protocol") + "://" + sivaMap.get("hostname") + ":" + sivaMap.get("port") + endpoint;
    }

    protected byte[] readFileFromTestResources(String filename) {
        String testFilesBase = getProjectBaseDirectory() + "src/test/resources/";
        return readFileFromPath(testFilesBase + getTestFilesDirectory() + filename);
    }

    protected void assertAllSignaturesAreValid(SimpleReport report) {
        assertTrue(report.getValidationConclusion().getSignaturesCount().equals(report.getValidationConclusion().getValidSignaturesCount()));
    }

    protected void assertSomeSignaturesAreValid(SimpleReport report, int expectedValidSignatures) {
        assertTrue(expectedValidSignatures == report.getValidationConclusion().getValidSignaturesCount());
    }

    protected void assertAllSignaturesAreInvalid(SimpleReport report) {
        assertTrue(report.getValidationConclusion().getValidSignaturesCount() == 0);
    }

    protected static byte[] readFileFromPath(String pathName) {
        try {
            return Files.readAllBytes(FileSystems.getDefault().getPath(pathName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getProjectBaseDirectory() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        int pathLength = path.lastIndexOf(PROJECT_SUBMODULE_NAME);
        pathLength = pathLength == -1 ? path.length() : pathLength;
        path = path.substring(0, pathLength);
        return path + File.separator + PROJECT_SUBMODULE_NAME + File.separator;
    }


}
