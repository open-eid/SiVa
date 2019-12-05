/*
 * Copyright 2019 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.webapp.soap.transformer.report;

import eu.europa.esig.dss.diagnostic.DiagnosticDataXmlDefiner;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;

import javax.xml.bind.JAXBElement;

public class DiagnosticDataTransformer
        extends ReportTransformer<
            eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData,
            eu.europa.esig.dss.validation.diagnostic.DiagnosticData,
            ee.openeid.siva.webapp.soap.response.DiagnosticData
        > {

    private static final String EU_DIAGNOSTIC_DATA_PACKAGE = "eu.europa.esig.dss.validation.diagnostic";

    public DiagnosticDataTransformer() {
        super(eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData.class, EU_DIAGNOSTIC_DATA_PACKAGE);
    }

    @Override
    ee.openeid.siva.webapp.soap.response.DiagnosticData transformReport(eu.europa.esig.dss.validation.diagnostic.DiagnosticData dssDiagnosticData) {
        ee.openeid.siva.webapp.soap.response.DiagnosticData diagnosticData = new ee.openeid.siva.webapp.soap.response.DiagnosticData();
        diagnosticData.setDocumentName(dssDiagnosticData.getDocumentName());
        diagnosticData.setValidationDate(dssDiagnosticData.getValidationDate());
        diagnosticData.setContainerInfo(dssDiagnosticData.getContainerInfo());
        diagnosticData.setSignatures(dssDiagnosticData.getSignatures());
        diagnosticData.setUsedCertificates(dssDiagnosticData.getUsedCertificates());
        diagnosticData.setTrustedLists(dssDiagnosticData.getTrustedLists());
        diagnosticData.setListOfTrustedLists(dssDiagnosticData.getListOfTrustedLists());
        return diagnosticData;
    }

    @Override
    protected JAXBElement<XmlDiagnosticData> wrap(XmlDiagnosticData xmlDiagnosticData) {
        return DiagnosticDataXmlDefiner.OBJECT_FACTORY.createDiagnosticData(xmlDiagnosticData);
    }
}
