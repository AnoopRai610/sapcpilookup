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

public class XMLUtility{
	
	private Document xmlData;
	private XPath xpath;
	
	public XMLUtility(String xml) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath()
		xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.xmlData = builder.parse(new InputSource(new StringReader(xml)));
	}

	public XMLUtility(InputStream xml) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath()
		xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.xmlData = builder.parse(xml);
	}

	public XMLUtility(InputStream xml, Map<String,String> namespaces) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath()
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		this.xmlData = docBuilderFactory.newDocumentBuilder().parse(xml);
	}

	public XMLUtility(String xml, Map<String,String> namespaces) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath();
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		this.xmlData = docBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	}
	
	public XMLUtility(Document xml) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath()
		xpath = XPathFactory.newInstance().newXPath();
		this.xmlData = xml;
	}
	public XMLUtility(Document xml, Map<String,String> namespaces) throws Exception {
		//xpath = new net.sf.saxon.xpath.XPathFactoryImpl().newXPath()
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContextImp(namespaces));
		this.xmlData = xml;
	}

	public Object executeXpath(String xpathEquation, String type) throws Exception {
		Object xpathResult = this.xpath.evaluate(xpathEquation, xmlData.getDocumentElement(), new QName("http://www.w3.org/1999/XSL/Transform", type));
		return xpathResult;
	}

	public Node retrieveNodeOfParentNode(String path, Node parentNode) throws XPathExpressionException {
		return (Node) this.xpath.evaluate(path, parentNode, XPathConstants.NODE);
	}

	public NodeList retrieveNodeListOfParentNode(String path, Node parentNode) throws XPathExpressionException {
		return (NodeList) this.xpath.evaluate(path, parentNode, XPathConstants.NODESET);
	}

	public Node createNode(String name, String value, Node newParentNode) {
		Node newNode = xmlData.createElement(name);
		newNode.setTextContent(value);
		newParentNode.appendChild(newNode);
		return newNode;
	}

	public Node createNode(String name, Node baseNode, Node newParentNode) {
		if (baseNode != null) {
			return createNode(name, baseNode.getNodeName(), newParentNode);
		} else {
			return null;
		}
	}

	public Element createElement(String name, Node parentNode) {
		Element newNode = xmlData.createElement(name);
		parentNode.appendChild(newNode);
		return newNode;
	}

	public String getDocumentString() throws Exception {
		StringWriter stringWriter = new StringWriter();
		javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(xmlData),
				new StreamResult(stringWriter)
				);

		return stringWriter.toString();
	}

	public void remove(Object it) {
		Element n = (Element) it;
		n.getParentNode().removeChild(n);
	}

	public Document getDocument() {
		return xmlData;
	}
}

