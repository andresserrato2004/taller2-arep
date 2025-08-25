package com.taller;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Response wrapper that can be extended for future features
 */
public class Response {
    private int statusCode = 200;
    private String statusText = "OK";
    private String contentType = "text/plain; charset=UTF-8";
    private Map<String, String> headers = new HashMap<>();

    /**
     * Set HTTP status code
     * @param statusCode The status code
     * @return This response object for method chaining
     */
    public Response status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Set content type
     * @param contentType The content type
     * @return This response object for method chaining
     */
    public Response type(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set a response header
     * @param name Header name
     * @param value Header value
     * @return This response object for method chaining
     */
    public Response header(String name, String value) {
        headers.put(name.toLowerCase(), value);
        return this;
    }

    // Getters
    public int getStatusCode() { return statusCode; }
    public String getStatusText() { return statusText; }
    public String getContentType() { return contentType; }
    public Map<String, String> getHeaders() { return headers; }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
