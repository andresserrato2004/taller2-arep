package com.taller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/* 
 * This class is a web server that integrates with the WebFramework
 * It handles HTTP requests using the Request, Response, and RouteHandler classes
 * and supports both registered routes and static file serving
 */
public class HttpServer {

    //private static final int DEFAULT_PORT = 35000;
    // Remove BASE_DIR as we'll use WebFramework's static directory

    public static void start(int port) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on http://localhost:" + port + "/");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Client handling error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ". " + e.getMessage());
            System.exit(1);
        }   
    }


    /* 
     * This method handles client socket and integrates with the WebFramework
     * It creates Request/Response objects and uses registered RouteHandlers
     */
    private static void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            Response response = new Response().status(400);
            response.setStatusText("Bad Request");
            writeResponse(out, response, "Bad Request".getBytes(StandardCharsets.UTF_8));
            return;
        }

        String method = parts[0];
        String rawPath = parts[1];
        String protocol = parts[2];
        System.out.println("method: " + method + ", path: " + rawPath + ", protocol: " + protocol);

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String k = line.substring(0, idx).trim().toLowerCase();
                String v = line.substring(idx + 1).trim();
                headers.put(k, v);
            }
        }

        int contentLength = 0;
        if (headers.containsKey("content-length")) {
            try {
                contentLength = Integer.parseInt(headers.get("content-length"));
            } catch (NumberFormatException ignored) {
            }
        }
        
        String requestBody = null;
        if (contentLength > 0 && ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))) {
            char[] buf = new char[contentLength];
            int read = 0, off = 0;
            while (off < contentLength && (read = in.read(buf, off, contentLength - off)) != -1) {
                off += read;
            }
            requestBody = new String(buf);
        }

        // Handle form-encoded POST parameters
        if ("POST".equalsIgnoreCase(method) && requestBody != null && isFormUrlEncoded(headers)) {
            rawPath = rawPath + (rawPath.contains("?") ? "&" : "?") + requestBody;
        }

        // Create Request object
        Request request = new Request(method, rawPath, headers, requestBody);
        Response response = new Response();

        try {
            // Check if there's a registered route for this path and method
            RouteHandler handler = WebFramework.getRoute(method, request.getPath());
            
            if (handler != null) {
                // Execute the route handler
                String responseBody = handler.handle(request, response);
                writeResponse(out, response, responseBody.getBytes(StandardCharsets.UTF_8));
                return;
            }

            // Handle static files if no route is found
            if ("/".equals(request.getPath())) {
                serveStatic(out, "index.html");
            } else {
                String decoded = urlDecodePath(request.getPath());
                if (decoded.startsWith("/")) {
                    decoded = decoded.substring(1);
                }
                serveStatic(out, decoded);
            }
        } catch (IOException ioe) {
            System.err.println("IO error: " + ioe.getMessage());
            Response errorResponse = new Response().status(500).type("text/html; charset=UTF-8");
            errorResponse.setStatusText("Internal Server Error");
            byte[] body = "<h1>500 Internal Server Error</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, errorResponse, body);
        }
    }

    /* 
     * This method handles HTTP requests for static resources using WebFramework's static directory
     */
    private static void serveStatic(OutputStream out, String relativePath) throws IOException {
        Path baseDir = WebFramework.getStaticBaseDir();
        Path target = baseDir.resolve(relativePath).normalize();
        
        if (!target.startsWith(baseDir)) {
            Response response = new Response().status(403).type("text/html; charset=UTF-8");
            response.setStatusText("Forbidden");
            byte[] body = "<h1>403 Forbidden</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, response, body);
            return;
        }

        if (!Files.exists(target) || Files.isDirectory(target)) {
            Response response = new Response().status(404).type("text/html; charset=UTF-8");
            response.setStatusText("Not Found");
            byte[] body = "<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, response, body);
            return;
        }

        byte[] content = Files.readAllBytes(target);
        String contentType = detectContentType(target);
        Response response = new Response().status(200).type(contentType);
        response.setStatusText("OK");
        writeResponse(out, response, content);
    }


    /* 
     * This method writes the HTTP response using the Response object
     */
    private static void writeResponse(OutputStream out, Response response, byte[] body) throws IOException {
        StringBuilder headers = new StringBuilder();
        headers.append("HTTP/1.1 ").append(response.getStatusCode()).append(" ").append(response.getStatusText()).append("\r\n");
        headers.append("Content-Type: ").append(response.getContentType()).append("\r\n");
        headers.append("Content-Length: ").append(body.length).append("\r\n");
        
        // Add custom headers
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            headers.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        
        headers.append("Connection: close\r\n");
        headers.append("\r\n");
        
        out.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private static boolean isFormUrlEncoded(Map<String, String> headers) {
        String ct = headers.getOrDefault("content-type", "");
        return ct.toLowerCase().startsWith("application/x-www-form-urlencoded");
    }

    private static String urlDecodePath(String path) {
        try {
            return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

    private static String detectContentType(Path file) throws IOException {
        String probed = Files.probeContentType(file);
        if (probed != null) {
            return probed;
        }
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        }
        if (name.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (name.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (name.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        }
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (name.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }
}