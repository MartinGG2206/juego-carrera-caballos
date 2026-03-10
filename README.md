# Horse Race Game

Aplicacion web en Java con Spring Boot, Thymeleaf, Spring Security y JPA que automatiza el juego de carrera de caballos con cartas con persistencia de usuarios, grupos, puntos y partidas.

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

## Funcionalidades implementadas

- Registro e inicio de sesion.
- Asignacion automatica de usuarios a un maximo de 4 grupos de 4 jugadores.
- Saldo inicial de 1000 puntos por usuario.
- Apuesta variable en puntos.
- Pago fijo de 5x sobre la apuesta cuando el usuario gana.
- Compra ilimitada de paquetes de 1000 puntos por 10.000 COP.
- Persistencia de usuarios, compras y partidas en H2 local.

## Estructura de datos

La aplicacion persiste estas entidades principales:

- `app_user`: credenciales, nombre, saldo en puntos y grupo asignado.
- `player_group`: grupos del 1 al 4 con capacidad maxima de 4 usuarios.
- `race_game`: estado serializado de cada carrera por usuario.
- `point_purchase`: historial de compras de paquetes de puntos.

## Ejecucion local

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Luego abre `http://localhost:8080`.

## Base de datos local

- Motor: H2 en archivo local.
- Ruta: `./data/horse-race-db`.
- Consola H2: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/horse-race-db;AUTO_SERVER=TRUE`
- Usuario: `sa`
- Password: vacio

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
