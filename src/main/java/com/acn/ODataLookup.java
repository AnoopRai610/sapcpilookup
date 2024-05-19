package com.acn;

import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import com.acn.utility.CloudConnector;
import com.acn.utility.FormatXML;
import com.acn.utility.HTTPClient;

public class ODataLookup {

	private String user;
	private String password;
	private boolean isCloudConnector;
	private CloudConnector cloudConnector;
	
	public ODataLookup(String user, String password) throws Exception {
		this(user, password, false);
	}
	
	public ODataLookup(String user, String password, boolean isCloudConnector) throws Exception {
		this(user, password, isCloudConnector, null);
	}

	public ODataLookup(String user, String password, boolean isCloudConnector, String locationID) throws Exception {
		super();
		this.user = user;
		this.password = password;
		this.isCloudConnector = isCloudConnector;
		if (isCloudConnector)
			cloudConnector = new CloudConnector(locationID, "HTTP");
	}

	public String getCall(String targetEndpoint) throws Exception {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes()));
		headers.put("Accept", "application/json");

		Proxy proxy = null;
		
		if (isCloudConnector) {
			proxy = cloudConnector.getProxy();
			headers.putAll(cloudConnector.getProxyHeaders());
		}
		
		return XML.toString(new JSONObject(HTTPClient.externalCall(targetEndpoint, headers, "GET", null, proxy)));

	}
	
	public Map<String, List<String>> formatResponseToOutput(String xmlString, List<String> outputList)
			throws Exception {
		return FormatXML.formatResponseToOutput(xmlString, "//results", outputList);
	}
	
	public List<Map<String, String>> formatResponseToOutputRows(String xmlString, List<String> outputList)
			throws Exception {
		return FormatXML.formatResponseToOutputRows(xmlString, "//results", outputList);
	}
}
