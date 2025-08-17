# Servidor Web en Java (sin frameworks)


# Andres Serrato Camero

Servidor HTTP que atiende múltiples solicitudes secuenciales (no concurrentes), sirve archivos estáticos (HTML, CSS, JS, imágenes) y expone endpoints REST consumidos de forma asíncrona desde el cliente.

## Requisitos
- Git
- Java 21 (probado en Windows)
- Maven 3.9.x (opcional, ya hay `pom.xml`)

## en caso de no tener java 21
modificar la version en el archivo [pom.xml](pom.xml) cambia el lugar en donde dice `21` por tu version de java por ejemplo java `17`

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
  </properties>
```

## Clonar el proyecto
```powershell
git clone https://github.com/andresserrato2004/taller1-arep.git
```
```powershell
cd taller1-arep
```
## Estructura
- `src/main/java/com/taller/HttpServer.java`: servidor HTTP.
- `public/`: estáticos (`index.html`, `app.js`, `styles.css`, `img/`).

# Cómo ejecutar

- por favor solo escojer una de las formas para ejecutar.  

#### Con Maven (recomendado):
```powershell
mvn -DskipTests 
```
```powershell
java -cp target/classes com.taller.HttpServer
```
#### Con Maven en una sola linea en Powershell:
```powershell
mvn -DskipTests package;java -cp target/classes com.taller.HttpServer
```

#### Sin Maven (javac directo):
```powershell
mkdir -Force target\classes | Out-Null
```
```powershell
javac -encoding UTF-8 -d target\classes src\main\java\com\taller\HttpServer.java
```
```powershell
java -cp target\classes com.taller.HttpServer
```

Abrir en el navegador: http://localhost:35000/  
Para detener el servidor: Ctrl + C en la terminal.

## Endpoints REST
- GET `/hello?name=andres` → `Hola andres`
- POST `/hellopost?name=juan` → `Hola juan`

Prueba rápida con PowerShell:
```powershell
# GET
Invoke-WebRequest "http://localhost:35000/hello?name=andres" | Select-Object -Expand Content
# POST (query param)
Invoke-WebRequest -Method POST "http://localhost:35000/hellopost?name=juan" | Select-Object -Expand Content
```

#### :v  si se van a copear no lo hagan tan descarado gracias =3  

## Pruebas/Validación
- Visitar `/` carga `index.html` (sirve HTML, CSS, JS, imagen).
- Formularios invocan endpoints vía `fetch` (asíncrono).
- Solicitar `/styles.css`, `/app.js`, `/img/logo.png` valida archivos estáticos.
- 404 y 403 probados (archivo inexistente y path traversal `../`).

## Arquitectura
- Bucle principal con `ServerSocket.accept()` procesa conexiones secuencialmente (no concurrente).
- Router minimalista: endpoints `/hello` (GET) y `/hellopost` (POST) + estáticos desde `public/`.
- Detección de `Content-Type` por extensión y `Files.probeContentType`.
- Prevención de path traversal validando que el recurso resuelto permanezca dentro de `public/`.

## Desarrollo
- Formato, dependencias y compilación vía Maven.

