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

package ee.openeid.siva.soaptest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMLUtils {

    public static Element findElementByXPath(Document document, String xPath) {
        return findElementByXPath(document, xPath, Collections.emptyMap());
    }

    public static Element findElementByXPath(Document document, String xPath, Map<String, String> namespacePrefixesToUris) {
        try {
            NodeList nodes = (NodeList) createXPathExpression(xPath, namespacePrefixesToUris).evaluate(document, XPathConstants.NODESET);
            return (Element) nodes.item(0);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parseXml(String xml) {
        try {
            DocumentBuilder dBuilder = getDocumentBuilder();
            return dBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        setUpSecurity(dbFactory);
        return dbFactory.newDocumentBuilder();
    }

    public static Document documentFromNode(Element element) {
        try {
            DocumentBuilder dBuilder = getDocumentBuilder();
            Document newDocument = dBuilder.newDocument();
            Node importedNode = newDocument.importNode(element, true);
            newDocument.appendChild(importedNode);
            return newDocument;
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
