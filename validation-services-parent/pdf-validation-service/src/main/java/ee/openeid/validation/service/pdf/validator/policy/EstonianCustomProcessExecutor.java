package ee.openeid.validation.service.pdf.validator.policy;

import ee.openeid.validation.service.pdf.validator.process.EstonianLongTermValidation;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.CustomProcessExecutor;
import eu.europa.esig.dss.validation.policy.ProcessParameters;
import eu.europa.esig.dss.validation.policy.XmlNode;
import eu.europa.esig.dss.validation.policy.rules.NodeName;
import eu.europa.esig.dss.validation.report.DetailedReport;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.dss.validation.report.Reports;
import eu.europa.esig.dss.validation.report.SimpleReportBuilder;
import org.w3c.dom.Document;

public class EstonianCustomProcessExecutor extends CustomProcessExecutor {

    /**
     * This method executes the Estonian long term validation processes. The underlying processes are automatically executed.
     */
    @Override
    public Reports execute() {

        processParams = new ProcessParameters();
        diagnosticData = new DiagnosticData(diagnosticDataDom);
        processParams.setDiagnosticData(diagnosticData);
        processParams.setValidationPolicy(validationPolicy);
        processParams.setCountersignatureValidationPolicy(countersignatureValidationPolicy);
        processParams.setCurrentTime(currentTime);
        final XmlDom usedCertificates = diagnosticData.getElement("/DiagnosticData/UsedCertificates");
        processParams.setCertPool(usedCertificates);

        final XmlNode mainNode = new XmlNode(NodeName.VALIDATION_DATA);
        mainNode.setNameSpace(XmlDom.NAMESPACE);

        final EstonianLongTermValidation ltv = new EstonianLongTermValidation();
        ltv.run(mainNode, processParams);

        final Document validationReportDocument = mainNode.toDocument();
        detailedReport = new DetailedReport(validationReportDocument);

        final SimpleReportBuilder simpleReportBuilder = new SimpleReportBuilder(validationPolicy, diagnosticData);
        simpleReport = simpleReportBuilder.build(processParams);

        final Reports reports = new Reports(diagnosticData, detailedReport, simpleReport);
        return reports;
    }

}
