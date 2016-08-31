package ee.openeid.validation.service.ddoc.security;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SecureSAXParsers {

    private static SAXParserFactory factory = null;

    public static SAXParser createParser() throws ParserConfigurationException, SAXException {
        return getFactory().newSAXParser();
    }

    private static synchronized SAXParserFactory getFactory() throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
            disableExternalEntities(factory);
        }
        return factory;
    }

    private static void disableExternalEntities(SAXParserFactory factory) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
    }

}