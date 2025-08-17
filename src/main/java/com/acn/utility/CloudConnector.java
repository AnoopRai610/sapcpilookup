package com.acn.utility;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import com.sap.it.api.ITApiFactory;
import com.sap.it.api.ccs.adapter.CloudConnectorContext;
import com.sap.it.api.ccs.adapter.CloudConnectorProperties;
import com.sap.it.api.ccs.adapter.ConnectionType;

/**
 * CloudConnector is a utility class for retrieving proxy configuration and headers
 * required to connect to on-premise systems via SAP Cloud Connector.
 */
public class CloudConnector {

    private String onpremise_proxy_host;
    private int onpremise_proxy_http_port;
    private int onpremise_proxy_tcp_port;
    private Map<String, String> proxyHeaders;
    private Proxy proxy;

    /**
     * Constructs a CloudConnector using the specified connection type.
     *
     * @param type the connection type (e.g., "HTTP" or "TCP")
     * @throws Exception if initialization fails
     */
    public CloudConnector(String type) throws Exception {
        this(null, type);
    }

    /**
     * Constructs a CloudConnector using the specified location ID and connection type.
     *
     * @param locationId optional location ID for SAP Cloud Connector
     * @param type       the connection type ("HTTP" or "TCP")
     * @throws Exception if initialization fails
     */
    public CloudConnector(String locationId, String type) throws Exception {
        CloudConnectorContext context = new CloudConnectorContext();
        context.setConnectionType(ConnectionType.valueOf(type.toUpperCase()));

        CloudConnectorProperties props = ITApiFactory.getService(CloudConnectorProperties.class, context);

        onpremise_proxy_host = props.getProxyHost();
        proxyHeaders = props.getAdditionalHeaders();

        if (locationId != null && !locationId.isEmpty()) {
            proxyHeaders.put("SAP-Connectivity-SCC-Location_ID", locationId);
        }

        if (type.equalsIgnoreCase("HTTP")) {
            onpremise_proxy_http_port = props.getProxyPort();
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(onpremise_proxy_host, onpremise_proxy_http_port));
        }

        if (type.equalsIgnoreCase("TCP")) {
            onpremise_proxy_tcp_port = props.getProxyPort();
        }
    }

    /**
     * Returns the on-premise proxy host.
     *
     * @return proxy host as a string
     */
    public String getOnpremise_proxy_host() {
        return onpremise_proxy_host;
    }

    /**
     * Returns the HTTP port for the on-premise proxy.
     *
     * @return HTTP port number
     */
    public int getOnpremise_proxy_http_port() {
        return onpremise_proxy_http_port;
    }

    /**
     * Returns the TCP port for the on-premise proxy.
     *
     * @return TCP port number
     */
    public int getOnpremise_proxy_tcp_port() {
        return onpremise_proxy_tcp_port;
    }

    /**
     * Returns the configured Proxy object for HTTP connections.
     *
     * @return Proxy instance
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Returns the additional headers required for the proxy connection.
     *
     * @return map of header names and values
     */
    public Map<String, String> getProxyHeaders() {
        return proxyHeaders;
    }
}