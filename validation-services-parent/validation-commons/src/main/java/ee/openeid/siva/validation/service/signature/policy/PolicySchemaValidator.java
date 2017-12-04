/*
 * Copyright 2017 Riigi Infosüsteemide Amet
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

package ee.openeid.siva.validation.service.signature.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

public final class PolicySchemaValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicySchemaValidator.class);
    private static final String XSD_PATH = "/policy.xsd";

    private PolicySchemaValidator() { }

    public static void validate(InputStream xmlToValidate) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StreamSource xsdSource = new StreamSource(PolicySchemaValidator.class.getResourceAsStream(XSD_PATH));
        StreamSource xmlSource = new StreamSource(xmlToValidate);
        try {
            Validator validator = schemaFactory.newSchema(xsdSource).newValidator();
            validator.validate(xmlSource);
        } catch (SAXException | IOException e) {
            LOGGER.error("Not a valid policy", e);
            throw new PolicyValidationException(e);
        }
    }
}
