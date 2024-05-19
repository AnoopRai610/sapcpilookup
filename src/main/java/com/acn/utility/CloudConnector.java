package com.acn.utility;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import com.sap.it.api.ITApiFactory;
import com.sap.it.api.ccs.adapter.CloudConnectorContext;
import com.sap.it.api.ccs.adapter.CloudConnectorProperties;
import com.sap.it.api.ccs.adapter.ConnectionType;

public class CloudConnector {

	private String onpremise_proxy_host;
	private int onpremise_proxy_http_port;
	private int onpremise_proxy_tcp_port;
	private Map<String, String> proxyHeaders;
	private Proxy proxy;

	public CloudConnector(String type) throws Exception{
		this(null, type);
	}
	public CloudConnector(String locationId, String type) throws Exception {
		CloudConnectorContext context = new CloudConnectorContext();
		context.setConnectionType(ConnectionType.valueOf(type.toUpperCase()));
		CloudConnectorProperties props = ITApiFactory.getService(CloudConnectorProperties.class, context);
		onpremise_proxy_host = props.getProxyHost();
		proxyHeaders = props.getAdditionalHeaders();
		if (locationId != null && !locationId.isEmpty())
			proxyHeaders.put("SAP-Connectivity-SCC-Location_ID", locationId);

		if (type.equalsIgnoreCase("HTTP")) {
			onpremise_proxy_http_port = props.getProxyPort();
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(onpremise_proxy_host, onpremise_proxy_http_port));
		}
		if (type.equalsIgnoreCase("TCP")) {
			onpremise_proxy_tcp_port = props.getProxyPort();
		}
	}

	public String getOnpremise_proxy_host() {
		return onpremise_proxy_host;
	}

	public int getOnpremise_proxy_http_port() {
		return onpremise_proxy_http_port;
	}

	public int getOnpremise_proxy_tcp_port() {
		return onpremise_proxy_tcp_port;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public Map<String, String> getProxyHeaders() {
		return proxyHeaders;
	}
}