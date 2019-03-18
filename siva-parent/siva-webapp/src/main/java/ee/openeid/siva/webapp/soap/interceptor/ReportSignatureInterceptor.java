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

package ee.openeid.siva.webapp.soap.interceptor;

import ee.openeid.siva.signature.SignatureService;
import ee.openeid.siva.validation.configuration.ReportConfigurationProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Alters the SOAP response by creating a signature from the existing response's SOAP body and adding the signature
 * into the SOAP body. {@link javax.xml.soap.SOAPMessage} is used in order to achieve the described behaviour.
 * This means that {@link org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor SAAJOutInterceptor} needs to be added into the outbound interceptor chain
 * otherwise {@link javax.xml.soap.SOAPMessage} never will be created
 */
@Component("SoapReportSignatureInterceptor")
public class ReportSignatureInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSignatureInterceptor.class);

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ReportConfigurationProperties properties;

    public ReportSignatureInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) {
        if (properties.isReportSignatureEnabled()) {
            SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
            try {
                if (soapMessage != null) {
                    SOAPBody responseBody = soapMessage.getSOAPPart().getEnvelope().getBody();
                    SOAPBody requestBody = message.getExchange().getInMessage().getContent(SOAPMessage.class).getSOAPPart().getEnvelope().getBody();
                    if (reportTypeIsDetailed(requestBody)) {
                        LOGGER.debug("Starting to create report signature");
                        byte[] validationReportBytes = getValidationReportContent(responseBody);
                        byte[] validationReportSignature = signatureService.getSignature(validationReportBytes, "validationReport.xml", "application/xml");
                        addValidationReportSignature(responseBody, validationReportSignature);
                        LOGGER.debug("Finished creating report signature");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error producing report signature", e);
            }
        }
    }

    private boolean reportTypeIsDetailed(SOAPBody soapBody) {
        Node reportTypeNode = soapBody.getElementsByTagName("ReportType").item(0);
        reportTypeNode = reportTypeNode == null ? null : reportTypeNode.getFirstChild();
        return reportTypeNode != null && "Detailed".equals(reportTypeNode.getNodeValue());
    }

    private byte[] getValidationReportContent(SOAPBody soapBody) throws IOException, SOAPException, TransformerException {
        Node validationReportNode = soapBody.getFirstChild().getFirstChild();
        DOMSource source = new DOMSource(validationReportNode);
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, new StreamResult(stringWriter));
        String soapBodyString = stringWriter.toString();
        return soapBodyString.getBytes();
    }

    private void addValidationReportSignature(SOAPBody soapBody, byte[] validationReportSignature) throws SOAPException {
        SOAPElement validationResponseElement = (SOAPElement) soapBody.getFirstChild();
        validationResponseElement.addChildElement("ValidationReportSignature", "", "http://soap.webapp.siva.openeid.ee/response/")
                                 .setTextContent(Base64.encodeBase64String(validationReportSignature));
    }

    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    public void setProperties(ReportConfigurationProperties properties) {
        this.properties = properties;
    }

}
