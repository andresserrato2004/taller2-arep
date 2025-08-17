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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


/* 
 * this class is the most basic implementation of a httpserver or a web server
 * handle queries, files,and 
 * 
 * before pls if u have 
 */
public class HttpServer {

    private static final int DEFAULT_PORT = 35000;
    private static final Path BASE_DIR = Paths.get("public").toAbsolutePath().normalize();

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {
            System.out.println("Server listening on http://localhost:" + DEFAULT_PORT + "/");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Client handling error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + DEFAULT_PORT + ". " + e.getMessage());
            System.exit(1);
        }   
    }


    /* 
     * This method handle clientsocket and implements services REST POST and GET
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
            writeResponse(out, 400, "Bad Request", "text/plain; charset=UTF-8", "Bad Request".getBytes(StandardCharsets.UTF_8));
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

        String pathOnly = rawPath;
        int q = rawPath.indexOf('?');
        if (q >= 0) {
            pathOnly = rawPath.substring(0, q);
        }

        try {
            if ("/hello".equals(pathOnly) && "GET".equalsIgnoreCase(method)) {
                String name = getQueryParam(rawPath, "name");
                String msg = "Hola " + (name != null ? name : "");
                writeResponse(out, 200, "OK", "text/plain; charset=UTF-8", msg.getBytes(StandardCharsets.UTF_8));
                return;
            }
            if ("/hellopost".equals(pathOnly) && "POST".equalsIgnoreCase(method)) {
                String name = getQueryParam(rawPath, "name");
                if (name == null && requestBody != null && isFormUrlEncoded(headers)) {
                    name = getQueryParam("?" + requestBody, "name");
                }
                String msg = "Hola " + (name != null ? name : "");
                writeResponse(out, 200, "OK", "text/plain; charset=UTF-8", msg.getBytes(StandardCharsets.UTF_8));
                return;
            }

            if ("/".equals(pathOnly)) {
                serveStatic(out, "index.html");
            } else {
                String decoded = urlDecodePath(pathOnly);
                if (decoded.startsWith("/")) {
                    decoded = decoded.substring(1);
                }
                serveStatic(out, decoded);
            }
        } catch (IOException ioe) {
            System.err.println("IO error: " + ioe.getMessage());
            byte[] body = "<h1>500 Internal Server Error</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, 500, "Internal Server Error", "text/html; charset=UTF-8", body);
        }
    }

    /* 
     * This method handle HTTP request for static resources 
     */
    private static void serveStatic(OutputStream out, String relativePath) throws IOException {
        Path target = BASE_DIR.resolve(relativePath).normalize();
        if (!target.startsWith(BASE_DIR)) {
            byte[] body = "<h1>403 Forbidden</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, 403, "Forbidden", "text/html; charset=UTF-8", body);
            return;
        }

        if (!Files.exists(target) || Files.isDirectory(target)) {
            byte[] body = "<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8);
            writeResponse(out, 404, "Not Found", "text/html; charset=UTF-8", body);
            return;
        }

        byte[] content = Files.readAllBytes(target);
        String contentType = detectContentType(target);
        writeResponse(out, 200, "OK", contentType, content);
    }


    /* 
     * this method writes the Response from the server web and update status code
     * contentType 
     */
    private static void writeResponse(OutputStream out, int statusCode, String statusText, String contentType, byte[] body) throws IOException {
        String headers
                = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";
        out.write(headers.getBytes(StandardCharsets.UTF_8));
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

    /* 
     * this method get query params from URL 
     * 
     * @param path path from URL
     * @param key value before param requested
     * @return value query from URL
     */
    private static String getQueryParam(String path, String key) {
        int q = path.indexOf('?');
        if (q < 0) {
            return null;
        }
        String qs = path.substring(q + 1);
        for (String pair : qs.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
