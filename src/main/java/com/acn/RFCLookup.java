package com.acn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.acn.utility.XMLUtility;
import com.sap.conn.jco.*;

/**
 * RFCLookup is a utility class for invoking SAP RFC (Remote Function Call) modules
 * using XML-based request and response formats.
 * It supports both import parameters and table parameters.
 */
public class RFCLookup {

    private JCoDestination destination;
    private JCoRepository repo;

    /**
     * Initializes the RFC lookup with a given SAP destination.
     *
     * @param rfcDestination the name of the RFC destination configured in SAP JCo
     * @throws Exception if the destination cannot be retrieved
     */
    public RFCLookup(String rfcDestination) throws Exception {
        destination = JCoDestinationManager.getDestination(rfcDestination);
        repo = destination.getRepository();
    }

    /**
     * Executes an RFC call based on the XML request and returns the XML response.
     *
     * @param request XML string representing the RFC request
     * @return XML string representing the RFC response
     * @throws Exception if the RFC function is not found or execution fails
     */
    public String getResponse(String request) throws Exception {
        try {
            XMLUtility xmlReq = new XMLUtility(request);
            Document doc = xmlReq.getDocument();
            Element docReqElement = doc.getDocumentElement();
            docReqElement.normalize();

            JCoFunction function = repo.getFunction(docReqElement.getNodeName());
            if (function == null) {
                throw new Exception("RFC module " + docReqElement.getNodeName()
                        + " not found in SAP function repository. Please check whether function is active and marked as remote enabled.");
            }

            try {
                xmlToRFCRequest(function, docReqElement);
            } catch (Exception e) {
                throw new Exception("Error while parsing RFC request XML: " + e.getMessage());
            }

            function.execute(destination);

            // Prepare XML response
            JCoParameterList exportParam = function.getExportParameterList();
            XMLUtility xmlResp = new XMLUtility("<" + docReqElement.getNodeName() + ".response/>");
            Element docRespElement = xmlResp.getDocument().getDocumentElement();

            // Handle export parameters
            if (exportParam != null) {
                JCoParameterFieldIterator paramIterator = exportParam.getParameterFieldIterator();
                while (paramIterator.hasNextField()) {
                    JCoField field = paramIterator.nextField();
                    xmlResp.createNode(field.getName(), field.getString(), docRespElement);
                }
            }

            // Handle table parameters
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

    /**
     * Converts XML request into RFC import and table parameters.
     *
     * @param function the JCoFunction to populate
     * @param docEl the root XML element of the request
     * @throws Exception if parsing fails
     */
    private void xmlToRFCRequest(JCoFunction function, Element docEl) throws Exception {
        NodeList nl = docEl.getChildNodes();
        for (int count = 0; count < nl.getLength(); count++) {
            Node tempNode = nl.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.hasChildNodes() && tempNode.getChildNodes().getLength() > 1) {
                    JCoTable table = function.getTableParameterList().getTable(tempNode.getNodeName());
                    formTableRequest(tempNode, table);
                } else {
                    function.getImportParameterList().setValue(tempNode.getNodeName(), tempNode.getTextContent());
                }
            }
        }
    }

    /**
     * Populates a JCoTable from XML node data.
     *
     * @param node the XML node representing the table
     * @param table the JCoTable to populate
     */
    private void formTableRequest(Node node, JCoTable table) {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node tempNode = nl.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName().equalsIgnoreCase("table")) {
                table.appendRow();
                NodeList tblNl = tempNode.getChildNodes();
                for (int j = 0; j < tblNl.getLength(); j++) {
                    Node tempField = tblNl.item(j);
                    if (tempField.getNodeType() == Node.ELEMENT_NODE) {
                        table.setValue(tempField.getNodeName(), tempField.getTextContent());
                    }
                }
            }
        }
    }

    /**
     * Converts JCoTable data into XML elements.
     *
     * @param table the JCoTable to read
     * @param el the parent XML element to append to
     * @param xml the XMLUtility instance used for XML manipulation
     */
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