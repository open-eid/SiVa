package ee.sk.pdf.validator.monitoring.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class XmlUtil {

    private XmlUtil() {}

    public static Element findElementByXPath(Document document, String xPath) {
        return findElementByXPath(document, xPath, Collections.<String, String>emptyMap());
    }

    public static Element findElementByXPath(Document document, String xPath, Map<String, String> napespacePrefixesToUris) {
        try {
            NodeList nodes = (NodeList) createXPathExpression(xPath, napespacePrefixesToUris).evaluate(document, XPathConstants.NODESET);
            return (Element) nodes.item(0);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parseXml(String xml) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            setUpSecurity(dbFactory);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new InputSource(new StringReader(xml)));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUpSecurity(DocumentBuilderFactory dbFactory) throws ParserConfigurationException {
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbFactory.setXIncludeAware(false);
        dbFactory.setExpandEntityReferences(false);
    }

    private static XPathExpression createXPathExpression(String expression, Map<String, String> namespacePrefixesToUris) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new BasicNamespaceContext(namespacePrefixesToUris));

        try {
            return xPath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class BasicNamespaceContext implements NamespaceContext {
        private Map<String, String> namespacePrefixesToUris = new HashMap<>();

        public BasicNamespaceContext(Map<String, String> namespacePrefixesToUris) {
            this.namespacePrefixesToUris = namespacePrefixesToUris;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return namespacePrefixesToUris.get(prefix);
        }

        @Override
        public String getPrefix(String uri) {
            for (Map.Entry<String, String> onePrefixToUri : namespacePrefixesToUris.entrySet()) {
                String aPrefix = onePrefixToUri.getKey();
                String anUri = onePrefixToUri.getValue();
                if(anUri.equals(uri)) {
                    return aPrefix;
                }
            }
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String uri) {
            return this.namespacePrefixesToUris.keySet().iterator();
        }
    }
}
