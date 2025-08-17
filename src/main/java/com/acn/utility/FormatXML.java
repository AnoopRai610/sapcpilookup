package com.acn.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * FormatXML is a utility class for parsing XML responses and extracting
 * specific fields into structured Java collections such as maps and lists.
 * <p>
 * It supports two formats:
 * <ul>
 *   <li>Map of field names to lists of values</li>
 *   <li>List of maps representing rows of field-value pairs</li>
 * </ul>
 */
public class FormatXML {

    /**
     * Extracts specified fields from an XML string and returns them as a map
     * where each key maps to a list of values.
     *
     * @param xmlString      the XML content as a string
     * @param nodesetXPath   XPath expression to locate the node set
     * @param outputList     list of field names to extract
     * @return a map of field names to lists of values, or null if no child nodes exist
     * @throws Exception if parsing or XPath evaluation fails
     */
    public static Map<String, List<String>> formatResponseToOutput(String xmlString, String nodesetXPath,
                                                                   List<String> outputList) throws Exception {
        try {
            XMLUtility xmlUtil = new XMLUtility(xmlString);

            if (!xmlUtil.getDocument().getDocumentElement().hasChildNodes())
                return null;

            Map<String, List<String>> result = new LinkedHashMap<>();
            NodeList nlResult = (NodeList) xmlUtil.executeXpath(nodesetXPath, "NODESET");

            if (nlResult.getLength() == 0) {
                filterFields(xmlUtil.getDocument().getDocumentElement(), result, outputList);
            } else {
                for (int i = 0; i < nlResult.getLength(); i++) {
                    filterFields((Element) nlResult.item(i), result, outputList);
                }
            }
            return result;
        } catch (Exception e) {
            throw new Exception("Error formatting XML to output map: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to extract values from an XML element and populate a map of lists.
     *
     * @param el         the XML element to extract from
     * @param result     the map to populate
     * @param outputList list of field names to extract
     */
    private static void filterFields(Element el, Map<String, List<String>> result, List<String> outputList) {
        outputList.forEach(field -> {
            NodeList nl = el.getElementsByTagName(field);
            String value = (nl != null && nl.item(0) != null) ? nl.item(0).getTextContent() : "";

            result.computeIfAbsent(field, k -> new ArrayList<>()).add(value);
        });
    }

    /**
     * Extracts specified fields from an XML string and returns them as a list of maps,
     * where each map represents a row of field-value pairs.
     *
     * @param xmlString      the XML content as a string
     * @param nodesetXPath   XPath expression to locate the node set
     * @param outputList     list of field names to extract
     * @return a list of maps representing rows of extracted data, or null if no child nodes exist
     * @throws Exception if parsing or XPath evaluation fails
     */
    public static List<Map<String, String>> formatResponseToOutputRows(String xmlString, String nodesetXPath,
                                                                       List<String> outputList) throws Exception {
        try {
            XMLUtility xmlUtil = new XMLUtility(xmlString);

            if (!xmlUtil.getDocument().getDocumentElement().hasChildNodes())
                return null;

            List<Map<String, String>> result = new ArrayList<>();
            NodeList nlResult = (NodeList) xmlUtil.executeXpath(nodesetXPath, "NODESET");

            if (nlResult.getLength() == 0) {
                filterFields(xmlUtil.getDocument().getDocumentElement(), result, outputList);
            } else {
                for (int i = 0; i < nlResult.getLength(); i++) {
                    filterFields((Element) nlResult.item(i), result, outputList);
                }
            }

            return result;
        } catch (Exception e) {
            throw new Exception("Error formatting XML to output rows: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to extract values from an XML element and populate a list of maps.
     *
     * @param el         the XML element to extract from
     * @param result     the list to populate
     * @param outputList list of field names to extract
     */
    private static void filterFields(Element el, List<Map<String, String>> result, List<String> outputList) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        outputList.forEach(field -> {
            NodeList nl = el.getElementsByTagName(field);
            String value = (nl != null && nl.item(0) != null) ? nl.item(0).getTextContent() : "";
            dataMap.put(field, value);
        });
        result.add(dataMap);
    }
}
