/*
 * Copyright 2019 - 2023 Riigi Infosüsteemi Amet
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

import eu.europa.esig.dss.detailedreport.DetailedReportXmlDefiner;
import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;

import javax.xml.bind.JAXBElement;

public class DetailedReportTransformer
        extends ReportTransformer<
            eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport,
            eu.europa.esig.dss.validation.detailed_report.DetailedReport,
            ee.openeid.siva.webapp.soap.response.DetailedReport
        > {

    private static final String EU_DETAILED_REPORT_PACKAGE = "eu.europa.esig.dss.validation.detailed_report";

    public DetailedReportTransformer() {
        super(eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport.class, EU_DETAILED_REPORT_PACKAGE);
    }

    @Override

    ee.openeid.siva.webapp.soap.response.DetailedReport transformReport(eu.europa.esig.dss.validation.detailed_report.DetailedReport dssDetailReport) {
        ee.openeid.siva.webapp.soap.response.DetailedReport detailedReport = new ee.openeid.siva.webapp.soap.response.DetailedReport();
        detailedReport.getTLAnalysis().addAll(dssDetailReport.getTLAnalysis());
        detailedReport.getSignatureOrTimestampOrCertificate().addAll(dssDetailReport.getSignatureOrTimestampOrCertificate());
        detailedReport.getBasicBuildingBlocks().addAll(dssDetailReport.getBasicBuildingBlocks());
        return detailedReport;
    }

    @Override
    JAXBElement<XmlDetailedReport> wrap(XmlDetailedReport xmlDetailedReport) {
        return DetailedReportXmlDefiner.OBJECT_FACTORY.createDetailedReport(xmlDetailedReport);
    }
}
