package com.taller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web Framework - provides static methods for route registration and configuration
 */
public class WebFramework {
    // Route registry: path -> handler
    private static final Map<String, RouteHandler> getRoutes = new ConcurrentHashMap<>();
    private static final Map<String, RouteHandler> postRoutes = new ConcurrentHashMap<>();
    
    // Static files base directory
    private static Path staticBaseDir = Paths.get("public").toAbsolutePath().normalize();

    /**
     * Register a GET route with a handler
     * @param path The URL path
     * @param handler Lambda function to handle the request
     */
    public static void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
        System.out.println("GET route registered: " + path);
    }

    /**
     * Register a POST route with a handler
     * @param path The URL path
     * @param handler Lambda function to handle the request
     */
    public static void post(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
        System.out.println("POST route registered: " + path);
    }

    /**
     * Set the directory for static files
     * @param folder The folder path relative to classpath or absolute
     */
    public static void staticfiles(String folder) {
        if (folder.startsWith("/")) {
            // Absolute path
            staticBaseDir = Paths.get(folder).toAbsolutePath().normalize();
        } else {
            // Relative to classpath - look in target/classes or src/main/resources
            Path targetClasses = Paths.get("target/classes").resolve(folder);
            Path srcResources = Paths.get("src/main/resources").resolve(folder);
            Path publicFolder = Paths.get(folder);
            
            if (java.nio.file.Files.exists(targetClasses)) {
                staticBaseDir = targetClasses.toAbsolutePath().normalize();
            } else if (java.nio.file.Files.exists(srcResources)) {
                staticBaseDir = srcResources.toAbsolutePath().normalize();
            } else {
                staticBaseDir = publicFolder.toAbsolutePath().normalize();
            }
        }
        System.out.println("Static files directory set to: " + staticBaseDir);
    }

    /**
     * Get registered GET routes
     * @return Map of GET routes
     */
    public static Map<String, RouteHandler> getGetRoutes() {
        return getRoutes;
    }

    /**
     * Get registered POST routes
     * @return Map of POST routes
     */
    public static Map<String, RouteHandler> getPostRoutes() {
        return postRoutes;
    }

    /**
     * Get the static files base directory
     * @return The static files directory path
     */
    public static Path getStaticBaseDir() {
        return staticBaseDir;
    }

    /**
     * Check if a route is registered for the given method and path
     * @param method HTTP method
     * @param path URL path
     * @return The route handler if found, null otherwise
     */
    public static RouteHandler getRoute(String method, String path) {
        if ("GET".equalsIgnoreCase(method)) {
            return getRoutes.get(path);
        } else if ("POST".equalsIgnoreCase(method)) {
            return postRoutes.get(path);
        }
        return null;
    }
}
