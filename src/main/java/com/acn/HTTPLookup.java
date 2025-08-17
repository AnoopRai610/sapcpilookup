package com.acn;

import java.net.Proxy;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.acn.utility.CloudConnector;
import com.acn.utility.FormatXML;
import com.acn.utility.HTTPClient;

/**
 * The {@code HTTPLookup} class provides methods to perform HTTP GET and POST requests
 * with support for basic authentication and optional SAP Cloud Connector integration.
 */
public class HTTPLookup {

    private String user;
    private String password;
    private boolean isCloudConnector;
    private CloudConnector cloudConnector;

    /**
     * Constructs an {@code HTTPLookup} instance with basic authentication.
     *
     * @param user     the username for authentication
     * @param password the password for authentication
     * @throws Exception if initialization fails
     */
    public HTTPLookup(String user, String password) throws Exception {
        this(user, password, false);
    }

    /**
     * Constructs an {@code HTTPLookup} instance with optional Cloud Connector support.
     *
     * @param user             the username for authentication
     * @param password         the password for authentication
     * @param isCloudConnector flag indicating whether Cloud Connector is used
     * @throws Exception if initialization fails
     */
    public HTTPLookup(String user, String password, boolean isCloudConnector) throws Exception {
        this(user, password, isCloudConnector, null);
    }

    /**
     * Constructs an {@code HTTPLookup} instance with full configuration.
     *
     * @param user             the username for authentication
     * @param password         the password for authentication
     * @param isCloudConnector flag indicating whether Cloud Connector is used
     * @param locationID       the location ID for Cloud Connector
     * @throws Exception if initialization fails
     */
    public HTTPLookup(String user, String password, boolean isCloudConnector, String locationID) throws Exception {
        super();
        this.user = user;
        this.password = password;
        this.isCloudConnector = isCloudConnector;
        if (isCloudConnector)
            cloudConnector = new CloudConnector(locationID, "HTTP");
    }

    /**
     * Executes an HTTP GET request to the specified endpoint.
     *
     * @param targetEndpoint the URL to send the GET request to
     * @return the response as a string
     * @throws Exception if the request fails
     */
    public String getCall(String targetEndpoint) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes()));
        headers.put("Accept", "application/json");

        Proxy proxy = null;

        if (isCloudConnector) {
            proxy = cloudConnector.getProxy();
            headers.putAll(cloudConnector.getProxyHeaders());
        }

        return HTTPClient.externalCallWithHeaders(targetEndpoint, headers, "GET", null, proxy);
    }

    /**
     * Executes an HTTP POST request to the specified endpoint with a request body and additional headers.
     *
     * @param targetEndpoint the URL to send the POST request to
     * @param body           the request body
     * @param addHeaders     additional headers to include in the request
     * @return the response as a string
     * @throws Exception if the request fails
     */
    public String postCall(String targetEndpoint, String body, Map<String, Object> addHeaders) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes()));
        headers.put("Accept", "application/json");

        addHeaders.forEach((K, V) -> {
            if (K != null && V != null && K.equals("destination"))
                headers.put(K, V.toString());
        });

        Proxy proxy = null;

        if (isCloudConnector) {
            proxy = cloudConnector.getProxy();
            headers.putAll(cloudConnector.getProxyHeaders());
        }

        return HTTPClient.externalCallWithHeaders(targetEndpoint, headers, "POST", body, proxy);
    }

    /**
     * Formats an XML response into a map of output values based on the provided XPath and output list.
     *
     * @param xmlString  the XML response string
     * @param xpath      the XPath expression to locate data
     * @param outputList the list of output keys to extract
     * @return a map of output keys to their corresponding values
     * @throws Exception if formatting fails
     */
    public Map<String, List<String>> formatResponseToOutput(String xmlString, String xpath, List<String> outputList)
            throws Exception {
        return FormatXML.formatResponseToOutput(xmlString, xpath, outputList);
    }

    /**
     * Formats an XML response into a list of rows, each represented as a map of output values.
     *
     * @param xmlString  the XML response string
     * @param xpath      the XPath expression to locate data
     * @param outputList the list of output keys to extract
     * @return a list of maps, each representing a row of output values
     * @throws Exception if formatting fails
     */
    public List<Map<String, String>> formatResponseToOutputRows(String xmlString, String xpath, List<String> outputList)
            throws Exception {
        return FormatXML.formatResponseToOutputRows(xmlString, xpath, outputList);
    }
}
