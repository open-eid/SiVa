package ee.sk.pdf.validator.monitoring.response;

import com.google.common.base.Strings;
import ee.sk.pdf.validator.monitoring.status.ServiceStatus;
import ee.sk.pdf.validator.monitoring.util.XmlUtil;
import org.w3c.dom.Document;

import java.util.Collections;

public class ResponseValidator {
    private static final String XML_NAMESPACE = "http://dss.esig.europa.eu/validation/diagnostic";
    private static final String XPATH_ERROR_MESSAGE = "//d:SimpleReport/d:Signature/d:Error";
    private static final String XPATH_VALID_SIGNATURE_COUNT = "//d:SimpleReport/d:ValidSignaturesCount";
    private static final String NAMESPACE_PREFIX = "d";

    private String getErrorMessage;

    public ServiceStatus validateResponse(String responseContent) {
        return validSignatures(simpleReport(responseContent));
    }

    private static Document simpleReport(String httpBody) {
        Document document = XmlUtil.parseXml(httpBody);
        String detailedReport = XmlUtil.findElementByXPath(document, "//xmlSimpleReport").getTextContent();

        return XmlUtil.parseXml(detailedReport);
    }

    private ServiceStatus validSignatures(Document simpleReport) {
        String stringResult = getXmlTextContent(simpleReport, XPATH_VALID_SIGNATURE_COUNT);

        ServiceStatus serviceStatus = ServiceStatus.OK;
        if (!hasValidSignature(stringResult)) {
            serviceStatus = ServiceStatus.WARNING;
            getErrorMessage = findValidationFailureReason(simpleReport);
        }

        return serviceStatus;
    }

    private static String getXmlTextContent(Document simpleReport, String path) {
        return XmlUtil.findElementByXPath(simpleReport, path, Collections.singletonMap(NAMESPACE_PREFIX, XML_NAMESPACE))
                .getTextContent();
    }

    private static String findValidationFailureReason(Document simpleReport) {
        return getXmlTextContent(simpleReport, XPATH_ERROR_MESSAGE);
    }

    private static boolean hasValidSignature(String stringResult) {
        return Integer.parseInt(stringResult) == 1;
    }


    public static boolean isServiceFault(String body) {
        return body.contains("<soap:Fault>");
    }

    public static boolean shouldCheckForValidSignature(ServiceStatus statusResult, String body) {
        return !Strings.isNullOrEmpty(body) && statusResult == ServiceStatus.OK;
    }

    public String getGetErrorMessage() {
        return getErrorMessage;
    }
}
