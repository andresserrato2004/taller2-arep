package com.taller;

import static com.taller.WebFramework.*;

/**
 * Example application demonstrating how to use the web framework
 */
public class App {
    public static void main(String[] args) {
        // Configure static files directory - look for files in webroot folder
        staticfiles("webroot");

        // Register GET routes using lambda functions
        get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        
        get("/pi", (req, res) -> {
            return String.valueOf(Math.PI); 
        });

        get("/info", (req, res) -> {
            return "Request path: " + req.getPath() + 
                   ", Method: " + req.getMethod() +
                   ", User-Agent: " + req.getHeaders().get("user-agent");
        });

            // Start the HTTP server
            try {
                HttpServer.start(35000);
            } catch (Exception e) {
                System.err.println("Failed to start server: " + e.getMessage());
                e.printStackTrace();
            }
        }
}
