# Web Server in java (whiteout frameworks)

HTTP server that attends several queries sequential (not concurrent), get static files (HTTP, CSS, JS and images) and expose endpoints `REST` that can be consumed the form a asynchronous since the client  

## Requirements 

- Git
- Java 21
- Maven 3.9.x 

## Clone the project
```powershell
git clone https://github.com/andresserrato2004/taller1-arep.git
```
```powershell
cd taller1-arep
```
## If you don´t have java 21

modify the version in the  file [pom.xml](pom.xml) change the java version for example java `21` to your java version`17` if your java version is 21  pls ignore this.

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
  </properties>
```

## Scaffolding 
```
├── src
│   ├── main
│   ├── java
│   ├── com
│   ├── taller
│   └── HttpServer.java 
├── public
│   ├── index.html 
│   ├── app.js 
│   ├── styles.css 
│   └── img/ 
│ 
└── pom.xml
```
# hot to execute 

- only choose one way to run.  

#### with Maven (recommended):
```powershell
mvn compile
```
```powershell
java -cp target/classes com.taller.HttpServer
```
#### with Maven in only line on Powershell:
```powershell
mvn -DskipTests package;java -cp target/classes com.taller.HttpServer
```

#### whiteout  Maven (javac):
```powershell
mkdir -Force target\classes | Out-Null
```
```powershell
javac -encoding UTF-8 -d target\classes src\main\java\com\taller\HttpServer.java
```
```powershell
java -cp target\classes com.taller.HttpServer
```

open in the explorer: http://localhost:35000/  
for kill server use : Ctrl + C on the terminal.

## fast test with PowerShell:

```powershell
# GET
Invoke-WebRequest "http://localhost:35000/hello?name=andres" | Select-Object -Expand Content
# POST (query param)
Invoke-WebRequest -Method POST "http://localhost:35000/hellopost?name=juan" | Select-Object -Expand Content
```
## Endpoints REST
- GET `/hello?name=andres` → `Hola andres`
- POST `/hellopost?name=juan` → `Hola juan`


## Test with maven

to execute test with maven **you must be running the server** and on other terminal execute next command

```powershell
mvn test
```
## Author
 - Andres Serrato Camero

