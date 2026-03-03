# Documentacion de la solucion

## 1. Analisis del problema

El objetivo es automatizar el juego de carrera de caballos con cartas y demostrar comprension de:

- modelo de datos
- estructuras
- operadores
- restricciones

La mecanica implementada usa baraja espanola de 40 cartas:

- los 4 ases representan a los caballos
- se arma una pista con 7 cartas
- cada carta revelada hace avanzar al caballo de su mismo palo
- gana el primer caballo que llega a la casilla 7
- los palos son bastos, oros, copas y espadas

## 2. Modelo de datos

Se definieron estas entidades principales:

- `Suit`: representa el palo del caballo
- `Rank`: representa el valor de una carta
- `Card`: modela una carta con palo y valor
- `Deck`: administra el mazo y la operacion de sacar cartas
- `OddsTable`: traduce la cantidad de cartas por palo en cuotas
- `GameState`: concentra el estado completo de la partida
- `GameEngine`: contiene la logica y reglas del juego

Relacion entre entidades:

- `Deck` contiene muchas `Card`
- `GameState` contiene la `Deck`, la pista de 7 cartas, las posiciones y la apuesta
- `GameEngine` modifica `GameState` mediante operadores del juego

## 3. Estructuras utilizadas

- `List<Card>` para la pista y la bitacora de eventos
- `EnumMap<Suit, Integer>` para posiciones y cuotas por palo
- `enum` para controlar valores validos de palos y rangos

## 4. Operadores implementados

- crear un mazo espanol sin ases
- barajar el mazo
- repartir la pista
- contar palos de la pista
- calcular cuotas
- registrar una apuesta
- revelar la siguiente carta
- mover el caballo correspondiente
- validar si existe ganador
- pagar o perder la apuesta

## 5. Restricciones aplicadas

- la pista debe tener exactamente 7 cartas
- los ases no participan en el mazo porque representan a los caballos
- si la pista tiene 5 o mas cartas del mismo palo, se vuelve a repartir
- la apuesta debe ser mayor que cero
- la apuesta no puede superar el saldo disponible
- no se puede revelar una carta si antes no existe una apuesta
- no se puede seguir jugando cuando ya existe un ganador

## 6. Alternativas consideradas

Se evaluaron dos opciones:

1. aplicacion de consola
2. aplicacion web con Spring Boot

Se eligio Spring Boot porque facilita la demostracion, el despliegue y la visualizacion del estado del juego.

## 7. Diseno de la solucion

La solucion se dividio en tres partes:

- dominio: reglas y estructuras del juego
- web: controlador MVC para atender acciones del usuario
- vista: pagina Thymeleaf con tablero, apuesta, cuotas y bitacora

Flujo:

1. se crea una nueva partida
2. se reparte una pista valida de 7 cartas
3. se calculan las cuotas por caballo
4. el jugador elige caballo y monto
5. se revelan cartas una por una
6. se actualizan posiciones
7. se determina el ganador
8. se calcula el resultado de la apuesta

## 8. Implementacion

Archivos principales:

- `src/main/java/com/example/horserace/HorseRaceApplication.java`
- `src/main/java/com/example/horserace/web/GameController.java`
- `src/main/java/com/example/horserace/domain/GameEngine.java`
- `src/main/resources/templates/index.html`

Decisiones tecnicas:

- se uso `@SessionAttributes` para conservar la partida entre solicitudes
- se uso Thymeleaf para renderizar el tablero de forma simple
- se agrego `server.port=${PORT:8080}` para despliegue en nube

## 9. Pruebas realizadas

Se agregaron pruebas unitarias para verificar:

- que la pista inicial tenga 7 cartas
- que la apuesta descuente saldo correctamente
- que una carrera termine con ganador valido

Pruebas manuales sugeridas:

1. iniciar una nueva carrera
2. verificar que se vean 7 cartas en la pista
3. apostar por un caballo
4. revelar cartas hasta terminar
5. confirmar que se muestra el ganador y el nuevo saldo

## 10. Conclusiones

El proyecto evidencia la relacion entre modelo de datos, estructuras, operadores y restricciones porque el modelo define entidades claras, las estructuras soportan el estado, los operadores transforman ese estado y las restricciones garantizan consistencia y jugabilidad.
