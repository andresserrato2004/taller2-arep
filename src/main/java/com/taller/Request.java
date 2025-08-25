package com.taller;

import java.util.Map;

/**
 * HTTP Request wrapper that provides access to request data
 */
public class Request {
    private final String rawPath;
    private final Map<String, String> headers;
    private final String method;
    private final String body;

    public Request(String method, String rawPath, Map<String, String> headers, String body) {
        this.method = method;
        this.rawPath = rawPath;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Extract query parameter value by key
     * @param key The parameter name
     * @return The parameter value or null if not found
     */
    public String getValues(String key) {
        int q = rawPath.indexOf('?');
        if (q < 0) return null;
        
        String qs = rawPath.substring(q + 1);
        for (String pair : qs.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * Get the full raw path including query parameters
     * @return The raw path
     */
    public String getRawPath() {
        return rawPath;
    }

    /**
     * Get the path without query parameters
     * @return The clean path
     */
    public String getPath() {
        int q = rawPath.indexOf('?');
        return q >= 0 ? rawPath.substring(0, q) : rawPath;
    }

    /**
     * Get HTTP headers
     * @return Map of headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Get HTTP method
     * @return The HTTP method (GET, POST, etc.)
     */
    public String getMethod() {
        return method;
    }

    /**
     * Get request body
     * @return The request body or null
     */
    public String getBody() {
        return body;
    }
}
