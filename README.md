# Servidor Web en Java (sin frameworks)

Servidor HTTP que atiende múltiples solicitudes secuenciales (no concurrentes), sirve archivos estáticos (HTML, CSS, JS, imágenes) y expone endpoints REST consumidos de forma asíncrona desde el cliente.

## Requisitos
- Java 8+ (probado en Windows)
- Maven (opcional, ya incluido `pom.xml`)

## Estructura
- `src/main/java/com/taller/HttpServer.java`: servidor HTTP.
- `public/`: archivos estáticos (`index.html`, `app.js`, `styles.css`, `img/`).

## Cómo ejecutar
Con Maven:

```powershell
mvn -DskipTests package; java -cp target/classes com.taller.HttpServer
```

Con `javac` directamente:

```powershell
mkdir -Force target\classes | Out-Null
javac -encoding UTF-8 -d target\classes src\main\java\com\taller\HttpServer.java
java -cp target\classes com.taller.HttpServer
```

Abrir en el navegador: http://localhost:35000/


Para detener el servidor: `Ctrl + C` en la terminal.

## Endpoints REST
- GET `/hello?name=andres` → `Hola andres`
- POST `/hellopost?name=juan` → `Hola juan`

## Pruebas/Validación
- Navegar a `/` carga `index.html` (sirve HTML, CSS, JS, imagen).
- Formularios invocan endpoints vía XHR/`fetch` (asíncrono).
- Solicitar directamente `/styles.css`, `/app.js`, `/img/logo.jpg` valida archivos estáticos.
- 404 y 403 probados (archivo inexistente y path traversal `../`).

## Arquitectura
- Bucle principal con `ServerSocket.accept()` procesa conexiones secuencialmente.
- Router minimalista: endpoints `/hello` (GET) y `/hellopost` (POST) + estáticos desde `public/`.
- Detección de `Content-Type` por extensión y `Files.probeContentType`.
- Protección contra path traversal validando que el recurso resuelto se mantenga dentro de `public/`.