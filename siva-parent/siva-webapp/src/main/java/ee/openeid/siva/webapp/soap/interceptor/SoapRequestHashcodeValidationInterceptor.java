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

package ee.openeid.siva.webapp.soap.interceptor;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPBody;

public class SoapRequestHashcodeValidationInterceptor extends AbstractRequestValidationInterceptor {

    @Override
    void validateRequestBody(SOAPBody body) {
        validateSignatureFile(body);
        validateSignatureFilename(body);
        validateDataFiles(body);

        overrideReportType(body);
    }

    private void validateSignatureFile(SOAPBody body) {
        String signatureFile = getElementValueFromBody(body, "SignatureFile");
        if (StringUtils.isNotBlank(signatureFile) && !Base64.isBase64(signatureFile)) {
            throwFault(errorMessage("validation.error.message.signatureFile.invalidBase64"));
        }
    }

    private void validateSignatureFilename(SOAPBody body) {
        String filename = getElementValueFromBody(body, "Filename", "HashcodeValidationRequest");
        if (StringUtils.isNotBlank(filename) && !hasValidExtension(filename)) {
            throwFault(errorMessage("validation.error.message.signatureExtension"));
        }
    }

    private boolean hasValidExtension(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return extension != null && extension.equalsIgnoreCase("xml");
    }

    private void validateDataFiles(SOAPBody body) {
        Node dataFiles = body.getElementsByTagName("DataFiles").item(0);
        if (dataFiles != null) {
            NodeList dataFileChildNodes = dataFiles.getChildNodes();
            for (int i = 0; i < dataFileChildNodes.getLength(); i++) {
                Node dataFileChildNode = dataFileChildNodes.item(i);
                if (1 == dataFileChildNode.getNodeType()) {
                    parseAndValidateDataFile(dataFileChildNode.getChildNodes());
                }
            }
        }
    }

    private void parseAndValidateDataFile(NodeList dataFileNodes) {
        for (int i = 0; i < dataFileNodes.getLength(); i++) {
            Node node = dataFileNodes.item(i);
            if (1 == node.getNodeType()) {
                String localName = node.getLocalName();
                String textContent = node.getTextContent();
                if (StringUtils.isBlank(textContent)) {
                    continue;
                }
                if (localName.equals("HashAlgo")) {
                    overrideDataFileHashAlgorithm(node);
                } else if (localName.equals("Hash")) {
                    validateDataFileHash(node.getTextContent());
                }
            }
        }
    }

    private void validateDataFileHash(String hash) {
        if (StringUtils.isNotBlank(hash) && !Base64.isBase64(hash)) {
            throwFault(errorMessage("validation.error.message.base64"));
        }
    }

    /*
        Override ReportType value to upper-case to make the parameter case-insensitive.
        Because WSDL validated enumeration values can only be case-sensitive, and ReportType values are defined as upper-case.
     */

    private void overrideReportType(SOAPBody body) {
        String reportType = getElementValueFromBody(body, "ReportType");
        if (StringUtils.isNotBlank(reportType)) {
            Node reportTypeNode = body.getElementsByTagName("ReportType").item(0);
            changeElementValue(reportTypeNode, reportType.toUpperCase());
        }
    }
    /*
        Override Datafile.HashAlgo value to upper-case to make the parameter case-insensitive.
        Because WSDL validated enumeration values can only be case-sensitive, and HashAlgo values are defined as upper-case.
     */

    private void overrideDataFileHashAlgorithm(Node hashAlgorithmNode) {
        String hashAlgorithm = hashAlgorithmNode.getTextContent();
        changeElementValue(hashAlgorithmNode, hashAlgorithm.toUpperCase());
    }
}
