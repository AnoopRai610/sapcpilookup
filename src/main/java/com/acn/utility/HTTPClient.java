package com.acn.utility;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTPClient is a utility class for making HTTP requests to external services.
 * It supports GET, POST, PUT, DELETE methods and allows proxy configuration.
 */
public class HTTPClient {

    /**
     * Makes an HTTP call and returns the response as a UTF-8 encoded string.
     *
     * @param targetUrl      the URL to call
     * @param headers        request headers to include
     * @param method         HTTP method (e.g., GET, POST)
     * @param requestcontent optional request body (for POST/PUT)
     * @param proxy          optional proxy configuration
     * @return response content as a string
     * @throws Exception if the request fails
     */
    public static String externalCall(String targetUrl, Map<String, String> headers, String method,
                                      String requestcontent, Proxy proxy) throws Exception {
        byte[] responseBytes = externalCallByteArray(targetUrl, headers, method, requestcontent, proxy);
        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    /**
     * Makes an HTTP call and returns the response as a byte array.
     *
     * @param targetUrl      the URL to call
     * @param headers        request headers to include
     * @param method         HTTP method (e.g., GET, POST)
     * @param requestcontent optional request body (for POST/PUT)
     * @param proxy          optional proxy configuration
     * @return response content as a byte array
     * @throws Exception if the request fails or returns an error status
     */
    public static byte[] externalCallByteArray(String targetUrl, Map<String, String> headers, String method,
                                               String requestcontent, Proxy proxy) throws Exception {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection con = (HttpURLConnection) (proxy != null ? url.openConnection(proxy) : url.openConnection());

            con.setRequestMethod(method);
            headers.forEach(con::setRequestProperty);

            // Write request body if applicable
            if (requestcontent != null && !requestcontent.isEmpty()
                    && !(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE"))) {
                con.setDoOutput(true);
                try (OutputStream os = con.getOutputStream()) {
                    os.write(requestcontent.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            }

            // Set timeouts
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            con.connect();
            int status = con.getResponseCode();

            if (status < 299) {
                // Read successful response
                try (InputStream in = con.getInputStream();
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    byte[] data = new byte[16384];
                    int nRead;
                    while ((nRead = in.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    return buffer.toByteArray();
                } finally {
                    con.disconnect();
                }
            } else {
                // Read error response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    throw new Exception("Error while calling " + targetUrl + " [" + status + ": " +
                            con.getResponseMessage() + "] Message: " + sb.toString());
                } finally {
                    con.disconnect();
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while calling " + targetUrl + ": " + e.getMessage(), e);
        }
    }
}
