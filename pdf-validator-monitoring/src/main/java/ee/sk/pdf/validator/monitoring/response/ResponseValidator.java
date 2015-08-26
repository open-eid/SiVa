package ee.sk.pdf.validator.monitoring.response;

import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import ee.sk.pdf.validator.monitoring.util.XmlUtil;
import org.w3c.dom.Document;

import java.util.Collections;

public class ResponseValidator {
    public ServiceStatus validateResponse(String responseContent) {
        return validSignatures(simpleReport(responseContent));
    }

    private Document simpleReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReport = XmlUtil.findElementByXPath(document, "//xmlSimpleReport").getTextContent();
        return XmlUtil.parseXml(detailedReport);
    }

    private ServiceStatus validSignatures(Document simpleReport) {
        String stringResult = XmlUtil.findElementByXPath(
                simpleReport,
                "//d:SimpleReport/d:ValidSignaturesCount",
                Collections.singletonMap("d", "http://dss.markt.ec.europa.eu/validation/diagnostic")).getTextContent();

        return Integer.parseInt(stringResult) == 1 ? ServiceStatus.OK : ServiceStatus.WARNING;
    }

}
