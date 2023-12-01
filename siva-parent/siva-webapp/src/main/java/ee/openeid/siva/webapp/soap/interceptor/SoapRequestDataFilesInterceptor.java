/*
 * Copyright 2019 - 2024 Riigi Infosüsteemi Amet
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

import ee.openeid.siva.proxy.document.DocumentType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import jakarta.xml.soap.SOAPBody;

public class SoapRequestDataFilesInterceptor extends AbstractRequestValidationInterceptor {

    @Override
    void validateRequestBody(SOAPBody body) {
        validateDocumentElement(body);
        validateDocumentTypeElement(body);
    }

    private void validateDocumentElement(SOAPBody body) {
        String documentValue = getElementValueFromBody(body, "Document");
        if (StringUtils.isBlank(documentValue) || !Base64.isBase64(documentValue)) {
            throwFault(errorMessage("validation.error.message.base64"));
        }
    }

    private void validateDocumentTypeElement(SOAPBody body) {
        String filename = getElementValueFromBody(body, "Filename");
        if (!DocumentType.DDOC.name().equals(FilenameUtils.getExtension(filename).toUpperCase())) {
            throwFault("Invalid file name. Can only return data files for DDOC type containers.");
        }
    }

    @Override
    String getElementValueFromBody(SOAPBody body, String elementName) {
        Node elementNode = body.getElementsByTagName(elementName).item(0);
        elementNode = elementNode == null ? null : elementNode.getFirstChild();
        return elementNode == null ? "" : elementNode.getNodeValue();
    }
}
