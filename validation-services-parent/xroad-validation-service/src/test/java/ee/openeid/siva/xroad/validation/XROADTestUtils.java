/*
 * Copyright 2016 Riigi Infosüsteemide Amet
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package ee.openeid.siva.xroad.validation;

import ee.openeid.siva.validation.document.ValidationDocument;
import ee.openeid.siva.validation.document.builder.DummyValidationDocumentBuilder;
import ee.openeid.siva.validation.service.signature.policy.SignaturePolicyService;
import ee.openeid.validation.service.xroad.XROADValidationService;
import ee.openeid.validation.service.xroad.configuration.XROADSignaturePolicyProperties;
import ee.openeid.validation.service.xroad.configuration.XROADValidationServiceProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class XROADTestUtils {

    private static final String TEST_FILES_LOCATION = "test-files/";
    static final String XROAD_SIMPLE = "xroad-simple.asice";
    static final String XROAD_BATCHSIGNATURE = "xroad-batchsignature.asice";

    static ValidationDocument buildValidationDocument(String testFile) throws Exception {
        String fileLocation = TEST_FILES_LOCATION + testFile;
        ValidationDocument document = DummyValidationDocumentBuilder
                .aValidationDocument()
                .withDocument(fileLocation)
                .withName(testFile)
                .build();
        document.setBytes(IOUtils.toByteArray(getFileStream(fileLocation)));
        return document;
    }

    private static InputStream getFileStream(String filename) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(filename);
    }

    static XROADValidationService initializeXROADValidationService() {
        XROADValidationServiceProperties properties = new XROADValidationServiceProperties();
        properties.setDefaultPath();

        XROADSignaturePolicyProperties policyProperties = new XROADSignaturePolicyProperties();
        policyProperties.initPolicySettings();

        XROADValidationService validationService = new XROADValidationService();
        validationService.setProperties(properties);
        validationService.setSignaturePolicyService(new SignaturePolicyService<>(policyProperties));
        validationService.loadXroadConfigurationDirectory();
        return validationService;
    }

}
