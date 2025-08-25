# Web Framework for REST Services and Static File Management

A simple but powerful web framework built in Java that enables developers to create web applications with REST services and static file management using lambda functions.

## Features

🚀 **Lambda-based Route Registration**: Define REST endpoints using simple lambda expressions
🔍 **Query Parameter Extraction**: Easy access to URL query parameters
📁 **Unified Static Files**: All static assets are now served from a single directory (`webroot`)
📡 **HTTP Request/Response Objects**: Clean abstractions for handling HTTP communication
🏗️ **Modular Architecture**: Well-separated components for maintainability

## Quick Start

### Basic Usage

```java
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
```

### Architecture

The framework is organized into the following components:

- **`RouteHandler.java`**: Functional interface for defining route handlers
- **`Request.java`**: HTTP request wrapper with query parameter extraction
- **`Response.java`**: HTTP response wrapper (extensible for future features)
- **`WebFramework.java`**: Core framework with static methods for route registration
- **`HttpServer.java`**: Basic HTTP server implementation
- **`App.java`**: Example application demonstrating usage

### API Reference

#### Request Object Methods

```java
public class Request {
    String getValues(String key)     // Get query parameter value
    String getRawPath()              // Get full path with query string
    String getPath()                 // Get path without query string
    String getMethod()               // Get HTTP method
    String getBody()                 // Get request body
    Map<String, String> getHeaders() // Get all headers
}
```

#### Static Files Configuration

```java
WebFramework.staticfiles("webroot");           // Relative path
```

All static files are now located in `src/main/resources/webroot/`.
The framework will look for static files in:
1. `target/classes/webroot` (Maven build output)
2. `src/main/resources/webroot` (Source resources)
3. `webroot` (Direct path)

### Example Endpoints

After running the example application, you can access:

- **REST Services**:
  - `http://localhost:35000/hello?name=Pedro` - Personalized greeting
  - `http://localhost:35000/pi` - Value of π
  - `http://localhost:35000/info` - Request information

- **Static Files**:
  - `http://localhost:35000/` or `http://localhost:35000/index.html` - Demo page
  - `http://localhost:35000/styles.css` - CSS stylesheet
  - `http://localhost:35000/app.js` - JavaScript file

## Requirements

- Git
- Java 8 or higher
- Maven 3.9.x

## Clone the project
```bash
git clone https://github.com/andresserrato2004/taller1-arep.git
cd taller1-arep
```

## Building and Running

### Run the Example Application

#### with Maven (recommended):
```bash
mvn compile
mvn exec:java
```

#### Run tests open new terminal and  run aplication: 
```bash
mvn test
```

#### Package:
```bash
mvn package
```

Open in browser: http://localhost:35000/  
To stop the server use: Ctrl + C in the terminal.

## Project Structure

```
src/
├── main/
│   ├── java/com/taller/
│   │   ├── RouteHandler.java    # Functional interface
│   │   ├── Request.java         # HTTP request wrapper
│   │   ├── Response.java        # HTTP response wrapper
│   │   ├── WebFramework.java    # Core framework
│   │   ├── HttpServer.java      # HTTP server
│   │   └── App.java            # Example application
│   └── resources/webroot/
│       ├── index.html          # Demo page
│       ├── styles.css          # Stylesheet
│       ├── app.js              # JavaScript
│       └── img/                # Images and icons
├── test/
│   └── java/com/taller/
│       └── HttpServerTest.java  # Unit tests
└── pom.xml
```

## Fast Test with Command Line

```bash
# GET request
curl "http://localhost:35000/hello?name=Pedro"

# Test static files
curl "http://localhost:35000/"
```

## REST API Endpoints

- GET `/hello?name=Pedro` → `Hello Pedro`
- GET `/pi` → `3.141592653589793`
- GET `/info` → Request information

## Static File Endpoints

* GET `/` → Demo HTML page
* GET `/index.html` → Demo HTML page
* GET `/styles.css` → CSS stylesheet
* GET `/app.js` → JavaScript file
* GET `/img/logo.jpg` → Example image

## Development
You can add or update static frontend files directly in `src/main/resources/webroot/`.


### Extending the Framework

The framework is designed to be extensible:

- Add new HTTP methods by extending `WebFramework.java`
- Enhance `Request`/`Response` objects with additional features
- Add middleware support for authentication, logging, etc.
- Implement template engines for dynamic HTML generation

## Author

- Andrés Serrato Camero

