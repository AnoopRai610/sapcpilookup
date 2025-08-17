package com.acn.utility;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * XMLUtility is a helper class for parsing, manipulating, and querying XML documents.
 * It supports XPath evaluation, node creation, and namespace-aware processing.
 */
public class XMLUtility {

    private Document xmlData;
    private XPath xpath;

    /**
     * Constructs an XMLUtility from a string containing XML.
     *
     * @param xml the XML content as a string
     * @throws Exception if parsing fails
     */
    public XMLUtility(String xml) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.xmlData = builder.parse(new InputSource(new StringReader(xml)));
    }

    /**
     * Constructs an XMLUtility from an InputStream containing XML.
     *
     * @param xml the XML content as an InputStream
     * @throws Exception if parsing fails
     */
    public XMLUtility(InputStream xml) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.xmlData = builder.parse(xml);
    }

    /**
     * Constructs a namespace-aware XMLUtility from an InputStream.
     *
     * @param xml        the XML content as an InputStream
     * @param namespaces map of prefix to namespace URI
     * @throws Exception if parsing fails
     */
    public XMLUtility(InputStream xml, Map<String, String> namespaces) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        this.xmlData = docBuilderFactory.newDocumentBuilder().parse(xml);
    }

    /**
     * Constructs a namespace-aware XMLUtility from a string.
     *
     * @param xml        the XML content as a string
     * @param namespaces map of prefix to namespace URI
     * @throws Exception if parsing fails
     */
    public XMLUtility(String xml, Map<String, String> namespaces) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        this.xmlData = docBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    /**
     * Constructs an XMLUtility from an existing Document.
     *
     * @param xml the XML Document
     * @throws Exception if initialization fails
     */
    public XMLUtility(Document xml) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        this.xmlData = xml;
    }

    /**
     * Constructs a namespace-aware XMLUtility from an existing Document.
     *
     * @param xml        the XML Document
     * @param namespaces map of prefix to namespace URI
     * @throws Exception if initialization fails
     */
    public XMLUtility(Document xml, Map<String, String> namespaces) throws Exception {
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
        this.xmlData = xml;
    }

    /**
     * Executes an XPath expression and returns the result as an object of the specified type.
     *
     * @param xpathEquation the XPath expression
     * @param type          the result type (e.g., "NODESET", "NODE", "STRING")
     * @return the result of the XPath evaluation
     * @throws Exception if evaluation fails
     */
    public Object executeXpath(String xpathEquation, String type) throws Exception {
        return xpath.evaluate(xpathEquation, xmlData.getDocumentElement(),
                new QName("http://www.w3.org/1999/XSL/Transform", type));
    }

    /**
     * Retrieves a single node from a parent node using XPath.
     *
     * @param path       the XPath expression
     * @param parentNode the parent node to search within
     * @return the matching node
     * @throws XPathExpressionException if evaluation fails
     */
    public Node retrieveNodeOfParentNode(String path, Node parentNode) throws XPathExpressionException {
        return (Node) xpath.evaluate(path, parentNode, XPathConstants.NODE);
    }

    /**
     * Retrieves a node list from a parent node using XPath.
     *
     * @param path       the XPath expression
     * @param parentNode the parent node to search within
     * @return the matching node list
     * @throws XPathExpressionException if evaluation fails
     */
    public NodeList retrieveNodeListOfParentNode(String path, Node parentNode) throws XPathExpressionException {
        return (NodeList) xpath.evaluate(path, parentNode, XPathConstants.NODESET);
    }

    /**
     * Creates a new element node with the given name and value, and appends it to the specified parent node.
     *
     * @param name         the name of the new node
     * @param value        the text content of the new node
     * @param newParentNode the parent node to append to
     * @return the newly created node
     */
    public Node createNode(String name, String value, Node newParentNode) {
        Node newNode = xmlData.createElement(name);
        newNode.setTextContent(value);
        newParentNode.appendChild(newNode);
        return newNode;
    }

    /**
     * Creates a new element node with the given name and the name of a base node as its value.
     *
     * @param name         the name of the new node
     * @param baseNode     the node whose name will be used as the value
     * @param newParentNode the parent node to append to
     * @return the newly created node, or null if baseNode is null
     */
    public Node createNode(String name, Node baseNode, Node newParentNode) {
        if (baseNode != null) {
            return createNode(name, baseNode.getNodeName(), newParentNode);
        } else {
            return null;
        }
    }

    /**
     * Creates a new element with the given name and appends it to the specified parent node.
     *
     * @param name       the name of the new element
     * @param parentNode the parent node to append to
     * @return the newly created element
     */
    public Element createElement(String name, Node parentNode) {
        Element newNode = xmlData.createElement(name);
        parentNode.appendChild(newNode);
        return newNode;
    }

    /**
     * Converts the internal XML Document to a string.
     *
     * @return the XML content as a string
     * @throws Exception if transformation fails
     */
    public String getDocumentString() throws Exception {
        StringWriter stringWriter = new StringWriter();
        javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
                new DOMSource(xmlData),
                new StreamResult(stringWriter)
        );
        return stringWriter.toString();
    }

    /**
     * Removes a node from its parent.
     *
     * @param it the node to remove
     */
    public void remove(Object it) {
        Element n = (Element) it;
        n.getParentNode().removeChild(n);
    }

    /**
     * Returns the internal XML Document.
     *
     * @return the XML Document
     */
    public Document getDocument() {
        return xmlData;
    }
}
