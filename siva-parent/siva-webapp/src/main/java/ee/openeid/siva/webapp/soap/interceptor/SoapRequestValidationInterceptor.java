/*
 * Copyright 2018 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.webapp.request.validation.annotations.ValidSignaturePolicyPattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.SOAPBody;
import java.util.regex.Pattern;

public class SoapRequestValidationInterceptor extends AbstractRequestValidationInterceptor {

    private static final int MAX_POLICY_LENGTH = 100;
    private static final int MAX_FILENAME_LENGTH = 260;
    private static final Pattern SIGNATURE_POLICY_PATTERN = Pattern.compile(ValidSignaturePolicyPattern.PATTERN);

    @Override
    void validateRequestBody(SOAPBody body) {
        validateDocumentElement(body);
        validateFilenameElement(body);
        validateDocumentTypeElement(body);
        validateSignaturePolicyElement(body);
    }

    private void validateDocumentElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "Document");
        if (StringUtils.isBlank(documentValue) || !Base64.isBase64(documentValue)) {
            throwFault(errorMessage("validation.error.message.base64"));
        }
    }

    private void validateFilenameElement(SOAPBody body) {
        String filenameValue = getElementValueFromBody(body, "Filename");
        if (StringUtils.isBlank(filenameValue) || filenameValue.length() > MAX_FILENAME_LENGTH || filenameValue.length() < 1) {
            throwFault(errorMessage("validation.error.message.filename"));
        }
    }

    private void validateDocumentTypeElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "DocumentType");
        if (!isValidDocumentType(documentValue)) {
            throwFault(errorMessage("validation.error.message.documentType"));
        }
    }

    private void validateSignaturePolicyElement(SOAPBody body) {
        String signaturePolicyValue = getElementValueFromBody(body, "SignaturePolicy");
        if (signaturePolicyValue != null && (!SIGNATURE_POLICY_PATTERN.matcher(signaturePolicyValue).matches() || signaturePolicyValue.length() > MAX_POLICY_LENGTH || signaturePolicyValue.length() < 1)) {
            throwFault(errorMessage("validation.error.message.signaturePolicy"));
        }
    }

    private boolean isValidDocumentType(String inputDocumentType) {
        return inputDocumentType == null;
    }
}
