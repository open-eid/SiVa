/*
 * Copyright 2018 - 2023 Riigi Infosüsteemi Amet
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
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPBody;

public class SoapRequestHashcodeValidationInterceptor extends AbstractRequestValidationInterceptor {

    private static final int MAX_HASH_LENGTH = 1000;

    @Override
    void validateRequestBody(SOAPBody body) {
        validateSignatureFiles(body);
        overrideReportType(body);
    }

    private void validateSignatureFiles(SOAPBody body) {

        NodeList signaturesFiles = body.getElementsByTagName("SignatureFiles");
        if (signaturesFiles != null) {
            for (int i = 0; i < signaturesFiles.getLength(); i++) {
                Node signatureFilesChildNode = signaturesFiles.item(i);
                if (1 == signatureFilesChildNode.getNodeType()) {
                    NodeList signatureFile = signatureFilesChildNode.getChildNodes();
                    if (signatureFile != null) {
                        for (int j = 0; j < signatureFile.getLength(); j++) {
                            Node signatureFileChildNode = signatureFile.item(j);
                            if (1 == signatureFileChildNode.getNodeType()) {
                                parseAndValidateSignatureFile(signatureFileChildNode.getChildNodes());
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseAndValidateSignatureFile(NodeList signatureFileNodes) {
        for (int i = 0; i < signatureFileNodes.getLength(); i++) {
            Node node = signatureFileNodes.item(i);
            if (1 == node.getNodeType()) {
                String localName = node.getLocalName();
                if (localName.equals("Signature")) {
                    validateSignature(node.getTextContent());
                } else if (localName.equals("DataFiles")) {
                    validateDataFiles(node);
                }
            }
        }
    }

    private void validateDataFiles(Node dataFiles) {
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
                if (localName.equals("HashAlgo")) {
                    validateDataFileHashAlgo(node.getTextContent());
                    overrideDataFileHashAlgorithm(node);
                } else if (localName.equals("Hash")) {
                    validateDataFileHash(node.getTextContent());
                } else if (localName.equals("Filename")) {
                    validateDataFileName(node.getTextContent());
                }
            }
        }
    }

    private void validateSignature(String hash) {
        if (StringUtils.isNotBlank(hash) && !Base64.isBase64(hash)) {
            throwFault(errorMessage("validation.error.message.signatureFile.signature.invalidBase64"));
        }
    }

    private void validateDataFileHash(String hash) {
        if (StringUtils.isNotBlank(hash) && (!Base64.isBase64(hash) || hash.length() > MAX_HASH_LENGTH)) {
            throwFault(errorMessage("validation.error.message.base64"));
        }
    }

    private void validateDataFileName(String filename) {
        if (StringUtils.isBlank(filename)) {
            throwFault(errorMessage("validation.error.message.dataFile.filename.format"));
        }
    }

    private void validateDataFileHashAlgo(String hashAlgo) {
        if (StringUtils.isBlank(hashAlgo)) {
            throwFault(errorMessage("validation.error.message.hashAlgo"));
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
