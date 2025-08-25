package com.taller;

/**
 * Functional interface for handling HTTP routes
 * Used with lambda expressions to define REST endpoints
 */
@FunctionalInterface
public interface RouteHandler {
    /**
     * Handle an HTTP request and return a response string
     * @param req The HTTP request object
     * @param res The HTTP response object
     * @return The response body as a string
     */
    String handle(Request req, Response res);
}
