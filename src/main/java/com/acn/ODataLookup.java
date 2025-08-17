package com.acn;

import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import com.acn.utility.CloudConnector;
import com.acn.utility.FormatXML;
import com.acn.utility.HTTPClient;

/**
 * Utility class for performing OData lookups via HTTP GET requests.
 * Supports optional use of SAP Cloud Connector.
 * <b>Currently support only Basic Authentication</b>
 */
public class ODataLookup {

    private static final String AUTH_HEADER = "Authorization";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_TYPE_JSON = "application/json";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String CLOUD_CONNECTOR_PROTOCOL = "HTTP";
    private static final String XML_RESULT_PATH = "//results";

    private final String user;
    private final String password;
    private final boolean isCloudConnector;
    private final CloudConnector cloudConnector;

    /**
     * Constructor without Cloud Connector.
     * @throws Exception 
     */
    public ODataLookup(String user, String password) throws Exception {
        this(user, password, false, null);
    }

    /**
     * Constructor with optional Cloud Connector flag.
     * @throws Exception 
     */
    public ODataLookup(String user, String password, boolean isCloudConnector) throws Exception {
        this(user, password, isCloudConnector, null);
    }

    /**
     * Full constructor with Cloud Connector and location ID.
     * @throws Exception 
     */
    public ODataLookup(String user, String password, boolean isCloudConnector, String locationID) throws Exception {
        this.user = user;
        this.password = password;
        this.isCloudConnector = isCloudConnector;
        this.cloudConnector = isCloudConnector ? new CloudConnector(locationID, CLOUD_CONNECTOR_PROTOCOL) : null;
    }

    /**
     * Makes an HTTP GET call to the target endpoint and returns the response as an XML string (Please note that oData Call will be made for JSON).
     *
     * @param targetEndpoint the URL to call
     * @return XML string converted from JSON response
     * @throws Exception if the HTTP call or conversion fails
     */
    public String getCall(String targetEndpoint) throws Exception {
        if (targetEndpoint == null || targetEndpoint.isEmpty()) {
            throw new IllegalArgumentException("Target endpoint must not be null or empty.");
        }

        Map<String, String> headers = new HashMap<>();
        String credentials = Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
        headers.put(AUTH_HEADER, "Basic " + credentials);
        headers.put(ACCEPT_HEADER, ACCEPT_TYPE_JSON);

        Proxy proxy = null;

        if (isCloudConnector && cloudConnector != null) {
            proxy = cloudConnector.getProxy();
            headers.putAll(cloudConnector.getProxyHeaders());
        }

        try {
            String jsonResponse = HTTPClient.externalCall(targetEndpoint, headers, HTTP_METHOD_GET, null, proxy);
            return XML.toString(new JSONObject(jsonResponse));
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform GET call to endpoint: " + targetEndpoint, e);
        }
    }

    /**
     * Formats the XML response into a map of output fields.
     *
     * @param xmlString   the XML response string
     * @param outputList  list of fields to extract
     * @return map of field names to list of values
     * @throws Exception if formatting fails
     */
    public Map<String, List<String>> formatResponseToOutput(String xmlString, List<String> outputList) throws Exception {
        if (xmlString == null || outputList == null) {
            throw new IllegalArgumentException("XML string and output list must not be null.");
        }
        return FormatXML.formatResponseToOutput(xmlString, XML_RESULT_PATH, outputList);
    }

    /**
     * Formats the XML response into a list of rows (maps of field-value pairs).
     *
     * @param xmlString   the XML response string
     * @param outputList  list of fields to extract
     * @return list of maps representing rows
     * @throws Exception if formatting fails
     */
    public List<Map<String, String>> formatResponseToOutputRows(String xmlString, List<String> outputList) throws Exception {
        if (xmlString == null || outputList == null) {
            throw new IllegalArgumentException("XML string and output list must not be null.");
        }
        return FormatXML.formatResponseToOutputRows(xmlString, XML_RESULT_PATH, outputList);
    }
}