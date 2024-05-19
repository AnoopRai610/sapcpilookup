package com.acn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.acn.utility.XMLUtility;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterField;
import com.sap.conn.jco.JCoParameterFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;

public class RFCLookup {

	private JCoDestination destination;
	private JCoRepository repo;

	public RFCLookup(String rfcDestination) throws Exception {
		destination = JCoDestinationManager.getDestination(rfcDestination);
		repo = destination.getRepository();
	}

	public String getResponse(String request) throws Exception {
		try {
			XMLUtility xmlReq = new XMLUtility(request);
			Document doc = xmlReq.getDocument();

			Element docReqElement = doc.getDocumentElement();
			docReqElement.normalize();

			JCoFunction function = repo.getFunction(docReqElement.getNodeName());
			
			if (function == null)
				throw new Exception("RFC module " + docReqElement.getNodeName()
						+ " not found in SAP function repository. Please check whether function is active and marked as remore enabled");

			try {
				xmlToRFCRequest(function, docReqElement);
			} catch (Exception e) {
				throw new Exception("Error while parsing RFC request XML: " + e.getMessage());
			}

			function.execute(destination);
			JCoParameterList exportParam = function.getExportParameterList();

			XMLUtility xmlResp = new XMLUtility("<" + docReqElement.getNodeName() + ".response/>");
			Element docRespElement = xmlResp.getDocument().getDocumentElement();

			if (exportParam != null) {
				JCoParameterFieldIterator paramIterator = exportParam.getParameterFieldIterator();
				while (paramIterator.hasNextField()) {
					JCoField field = paramIterator.nextField();
					xmlResp.createNode(field.getName(), field.getString(), docRespElement);
				}
			}

			exportParam = function.getTableParameterList();
			if (exportParam != null) {
				JCoParameterFieldIterator paramIterator = exportParam.getParameterFieldIterator();
				while (paramIterator.hasNextField()) {
					JCoParameterField paramField = paramIterator.nextParameterField();
					Element tableRootEl = xmlResp.createElement(paramField.getName(), docRespElement);
					JCoTable table = paramField.getTable();
					parseTableData(table, tableRootEl, xmlResp);
				}
			}
			return xmlResp.getDocumentString();
		} catch (JCoException e) {
			throw new Exception("JCO Connection failed: " + e.getMessage(), e.getCause());
		}
	}

	private void xmlToRFCRequest(JCoFunction function, Element docEl) throws Exception {
		NodeList nl = docEl.getChildNodes();
		for (int count = 0; count < nl.getLength(); count++) {
			Node tempNode = nl.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasChildNodes() && tempNode.getChildNodes().getLength() > 1) {
					JCoTable table = function.getTableParameterList().getTable(tempNode.getNodeName());
					formTableRequest(tempNode, table);
				} else
					function.getImportParameterList().setValue(tempNode.getNodeName(), tempNode.getTextContent());
			}
		}
	}

	private void formTableRequest(Node node, JCoTable table) {
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node tempNode = nl.item(i);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.getNodeName().equalsIgnoreCase("table")) {
					table.appendRow();
					NodeList tblNl = tempNode.getChildNodes();
					for (int j = 0; j < tblNl.getLength(); j++) {
						Node tempField = tblNl.item(i);
						if (tempField.getNodeType() == Node.ELEMENT_NODE)
							table.setValue(tempField.getNodeName(), tempField.getTextContent());
					}
				}
			}
		}
	}

	private void parseTableData(JCoTable table, Element el, XMLUtility xml) {
		for (int i = 0; i < table.getNumRows(); i++) {
			table.setRow(i);
			Element tableEl = xml.createElement("table", el);
			JCoFieldIterator fieldItr = table.getFieldIterator();
			while (fieldItr.hasNextField()) {
				JCoField rowField = fieldItr.nextField();
				xml.createNode(rowField.getName(), rowField.getString(), tableEl);
			}

		}
	}

}
