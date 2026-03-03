# Horse Race Game

Aplicacion web en Java con Spring Boot y Thymeleaf que automatiza el juego de carrera de caballos con cartas.

## Estructura del proyecto

```text
horse-race-game/
|-- pom.xml
|-- README.md
|-- docs/
|   `-- documentacion-solucion.md
`-- src/
    |-- main/
    |   |-- java/com/example/horserace/
    |   |   |-- HorseRaceApplication.java
    |   |   |-- domain/
    |   |   `-- web/
    |   `-- resources/
    |       |-- static/styles.css
    |       |-- templates/index.html
    |       `-- application.properties
    `-- test/
        `-- java/com/example/horserace/domain/GameEngineTests.java
```

## Requisitos

- Java 17
- Maven 3.9+ o el wrapper `mvnw`

## Ejecucion local

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Luego abre `http://localhost:8080`.

## Pruebas

```powershell
.\mvnw.cmd test
```

## Despliegue

La aplicacion ya queda preparada para plataformas como Render o Railway porque usa `server.port=${PORT:8080}`.

### Render

1. Crea un nuevo `Web Service` desde este repositorio.
2. Build command: `./mvnw clean package`
3. Start command: `java -jar target/horse-race-game-0.0.1-SNAPSHOT.jar`

### Railway

1. Crea un proyecto desde GitHub.
2. Railway detecta Maven automaticamente.
3. Verifica que el comando de inicio sea el jar generado en `target/`.

## Entregables

- Documento del proceso: `docs/documentacion-solucion.md`
- Codigo fuente del juego: este repositorio
- Enlace de despliegue: debes generarlo al publicar en Render, Railway u otra plataforma
