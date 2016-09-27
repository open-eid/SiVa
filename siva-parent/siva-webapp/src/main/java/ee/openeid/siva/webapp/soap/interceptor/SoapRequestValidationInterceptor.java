/*
 * Copyright 2016 Riigi Infosüsteemide Amet
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

import ee.openeid.siva.webapp.request.validation.annotations.NotNullValidFilenamePattern;
import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicyPattern;
import ee.openeid.siva.webapp.soap.DocumentType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoapRequestValidationInterceptor extends AbstractSoapInterceptor {

    private ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

    SoapInterceptor saajIn = new SAAJInInterceptor();

    public SoapRequestValidationInterceptor() {
        super(Phase.POST_PROTOCOL);
        messageSource.setBasename("ValidationMessages");
    }

    @Override
    public void handleMessage(SoapMessage message){
        saajIn.handleMessage(message);
        SOAPMessage soapMessage = message.getContent(SOAPMessage.class);
        try {
            if (soapMessage == null) {
                throw new SOAPException();
            }
            SOAPBody body = soapMessage.getSOAPPart().getEnvelope().getBody();

            validateDocumentElement(body);
            validateFilenameElement(body);
            validateDocumentTypeElement(body);
            validateSignaturePolicyElement(body);

        } catch (SOAPException e) {
            throwFault(messageSource.getMessage("validation.error.message.invalidRequest", null, null));
        }
    }

    private void validateDocumentElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "Document");
        if (StringUtils.isBlank(documentValue) || !Base64.isBase64(documentValue)) {
            throwFault(messageSource.getMessage("validation.error.message.base64", null, null));
        }
    }

    private void validateFilenameElement(SOAPBody body) {
        String filenameValue = getElementValueFromBody(body, "Filename");
        Pattern pattern = Pattern.compile(NotNullValidFilenamePattern.PATTERN);
        Matcher matcher = pattern.matcher(filenameValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.filename", null, null));
        }
    }

    private void validateDocumentTypeElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "DocumentType");
        if (!isValidDocumentType(documentValue)) {
            throwFault(messageSource.getMessage("validation.error.message.documentType", null, null));
        }
    }

    private void validateSignaturePolicyElement(SOAPBody body) {
        String signaturePolicyValue = getElementValueFromBody(body, "SignaturePolicy");
        Pattern pattern = Pattern.compile(ValidSignaturePolicyPattern.PATTERN);
        Matcher matcher = pattern.matcher(signaturePolicyValue);
        if (!matcher.matches()) {
            throwFault(messageSource.getMessage("validation.error.message.signaturePolicy", null, null));
        }
    }

    private String getElementValueFromBody(SOAPBody body, String elementName) {
        Node elementNode = body.getElementsByTagName(elementName).item(0);
        elementNode = elementNode == null ? null : elementNode.getFirstChild();
        return elementNode == null ? "" : elementNode.getNodeValue();
    }

    private boolean isValidDocumentType(String inputDocumentType) {
        for (DocumentType dt : DocumentType.values()) {
            if (dt.name().equals(inputDocumentType)) {
                return true;
            }
        }
        return false;
    }


    private void throwFault(String message) {
        Fault fault = new Fault(new Exception(message));
        fault.setFaultCode(new QName("Client"));
        fault.setStatusCode(400);
        throw fault;
    }

}
