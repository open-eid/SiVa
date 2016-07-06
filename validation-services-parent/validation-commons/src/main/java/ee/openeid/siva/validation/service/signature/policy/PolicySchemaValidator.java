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

public class PolicySchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(PolicySchemaValidator.class);
    private static final String XSD_PATH = "/policy.xsd";

    private PolicySchemaValidator() {}

    public static void validate(InputStream xmlToValidate) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StreamSource xsdSource = new StreamSource(PolicySchemaValidator.class.getResourceAsStream(XSD_PATH));
        StreamSource xmlSource = new StreamSource(xmlToValidate);
        try {
            Validator validator = schemaFactory.newSchema(xsdSource).newValidator();
            validator.validate(xmlSource);
        } catch (SAXException | IOException e) {
            log.error("Not a valid policy", e);
            throw new PolicyValidationException(e);
        }
    }
}
