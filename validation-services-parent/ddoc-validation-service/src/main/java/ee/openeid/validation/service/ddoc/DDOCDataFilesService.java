/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.exception.ValidationServiceException;
import ee.openeid.siva.validation.service.DataFilesService;
import ee.openeid.validation.service.ddoc.report.DDOCDataFilesReportBuilder;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.utils.ConfigManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Service
public class DDOCDataFilesService implements DataFilesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DDOCDataFilesService.class);
    private final Object lock = new Object();

    private XMLEntityAttackValidator xmlEntityAttackValidator;

    @Override
    public DataFilesReport getDataFiles(DataFilesDocument dataFilesDocument) {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        xmlEntityAttackValidator.validateAgainstXMLEntityAttacks(dataFilesDocument.getBytes());

        synchronized (lock) {
            SignedDoc signedDoc = null;
            try {
                DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
                List<DigiDocException> signedDocInitializationErrors = new ArrayList<>();
                signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(dataFilesDocument.getBytes()), false, signedDocInitializationErrors);
                if (signedDoc == null) {
                    throw new MalformedDocumentException();
                }

                DDOCDataFilesReportBuilder ddocDataFilesReportBuilder = new DDOCDataFilesReportBuilder(signedDoc.getDataFiles());
                return ddocDataFilesReportBuilder.build();
            } catch (DigiDocException e) {
                LOGGER.warn("Unexpected exception when trying to return DDOC document data files: " + e.getMessage(), e);
                throw new ValidationServiceException(getClass().getSimpleName(), e);
            } finally {
                if (signedDoc != null) {
                    signedDoc.cleanupDfCache();
                }
            }
        }
    }

    @Autowired
    public void setXMLEntityAttackValidator(XMLEntityAttackValidator xmlEntityAttackValidator) {
        this.xmlEntityAttackValidator = xmlEntityAttackValidator;
    }

}
