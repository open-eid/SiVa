package ee.openeid.validation.service.ddoc;

import ee.openeid.siva.validation.document.QualifiedValidationResult;
import ee.openeid.siva.validation.document.report.QualifiedReport;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import lombok.Data;

@Data
public class DDOCValidationResult implements QualifiedValidationResult {

    private String simpleReport;
    private String detailedReport;
    private String diagnosticData;

    private QualifiedReport qualifiedReport;

    public DDOCValidationResult(SignedDoc signedDoc) {

        String report = "<Report>";
        for (Object signature : signedDoc.getSignatures()) {
            report += "<Signature>";
            Signature sig = (Signature) signature;
            report += "<Id>" + sig.getId() + "</Id>";
            report += "<Status>" + sig.getStatus() + "</Status>";
            report += "</Signature>";
        }
        report += "</Report>";
        setSimpleReport(report);
        setDetailedReport(report);
        setDiagnosticData(report);
    }
}
