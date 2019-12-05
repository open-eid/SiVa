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

package ee.openeid.validation.service.timemark;


import ee.openeid.siva.validation.document.DataFilesDocument;
import ee.openeid.siva.validation.document.report.DataFilesReport;
import ee.openeid.siva.validation.exception.MalformedDocumentException;
import ee.openeid.siva.validation.service.DataFilesService;
import ee.openeid.validation.service.timemark.report.DDOCDataFilesReportBuilder;

import eu.europa.esig.dss.model.DSSException;
import org.digidoc4j.Container;
import org.digidoc4j.ContainerBuilder;
import org.digidoc4j.exceptions.DigiDoc4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class DDOCDataFilesService implements DataFilesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DDOCDataFilesService.class);

    private XMLEntityAttackValidator xmlEntityAttackValidator;

    @Override
    public DataFilesReport getDataFiles(DataFilesDocument dataFilesDocument) {
        xmlEntityAttackValidator.validateAgainstXMLEntityAttacks(dataFilesDocument.getBytes());

        Container container;
        try {
            container = createContainer(dataFilesDocument);
            DDOCDataFilesReportBuilder ddocDataFilesReportBuilder = new DDOCDataFilesReportBuilder(container.getDataFiles());
            return ddocDataFilesReportBuilder.build();
        } catch (DigiDoc4JException | DSSException e) {
            LOGGER.error("Unable to create container from validation document", e);
            throw new MalformedDocumentException(e);
        }
    }

    private Container createContainer(DataFilesDocument dataFilesDocument) {
        InputStream containerInputStream = new ByteArrayInputStream(dataFilesDocument.getBytes());
        return ContainerBuilder.aContainer()
                .fromStream(containerInputStream)
                .build();
    }

    @Autowired
    public void setXMLEntityAttackValidator(XMLEntityAttackValidator xmlEntityAttackValidator) {
        this.xmlEntityAttackValidator = xmlEntityAttackValidator;
    }

}
