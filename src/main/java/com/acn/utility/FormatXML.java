package com.acn.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FormatXML {

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
				for (int i = 0; i < nlResult.getLength(); i++)
					filterFields((Element) nlResult.item(i), result, outputList);
			}
			return result;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private static void filterFields(Element el, Map<String, List<String>> result, List<String> outputList) {
		outputList.forEach(V -> {
			NodeList nl = el.getElementsByTagName(V);
			if (nl != null && nl.item(0)!=null) {
				if (result.get(V) == null) {
					List<String> values = new ArrayList<>();
					result.put(V, values);
				}
				result.get(V).add(nl.item(0).getTextContent());
			} else {
				if (result.get(V) == null) {
					List<String> values = new ArrayList<>();
					result.put(V, values);
				}
				result.get(V).add("");
			}
		});
	}

	public static List<Map<String, String>> formatResponseToOutputRows(String xmlString, String nodesetXPath, List<String> outputList)
			throws Exception {
		try {
			XMLUtility xmlUtil = new XMLUtility(xmlString);

			if (!xmlUtil.getDocument().getDocumentElement().hasChildNodes())
				return null;

			List<Map<String, String>> result = new ArrayList<>();
			NodeList nlResult = (NodeList) xmlUtil.executeXpath(nodesetXPath, "NODESET");

			if (nlResult.getLength() == 0) {
				filterFields(xmlUtil.getDocument().getDocumentElement(), result, outputList);
			} else {
				for (int i = 0; i < nlResult.getLength(); i++)
					filterFields((Element) nlResult.item(i), result, outputList);
			}

			return result;

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private static void filterFields(Element el, List<Map<String, String>> result, List<String> outputList) {
		Map<String, String> dataMap = new LinkedHashMap<>();
		outputList.forEach(V -> {
			NodeList nl = el.getElementsByTagName(V);
			if (nl != null && nl.item(0)!=null) {
				dataMap.put(V, nl.item(0).getTextContent());
			} else {
				dataMap.put(V, "");
			}
		});
		result.add(dataMap);
	}
}
