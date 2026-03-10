package com.example.horserace.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class GameEngine {

    private static final int TRACK_LENGTH = 7;
    private static final int INITIAL_POINTS = 1000;
    private static final int WIN_MULTIPLIER = 5;

    public GameState createNewGame() {
        return createNewGame(INITIAL_POINTS);
    }

    public GameState createNewGame(int playerBalance) {
        GameState gameState = new GameState();
        setupRace(gameState);
        gameState.setPlayerBalance(playerBalance);
        gameState.setStatusMessage("Se preparo una nueva carrera. Registra tu apuesta en puntos para comenzar.");
        gameState.getEventLog().add("Nueva partida creada con saldo disponible de " + playerBalance + " puntos.");
        return gameState;
    }

    public void placeBet(GameState gameState, Suit suit, int amount) {
        if (gameState.isBetPlaced()) {
            gameState.setStatusMessage("La apuesta ya fue registrada para esta carrera.");
            return;
        }
        if (amount <= 0) {
            gameState.setStatusMessage("La apuesta debe ser mayor que cero.");
            return;
        }
        if (amount > gameState.getPlayerBalance()) {
            gameState.setStatusMessage("Saldo insuficiente para registrar esa apuesta.");
            return;
        }

        gameState.setSelectedHorse(suit);
        gameState.setBetAmount(amount);
        gameState.setBetPlaced(true);
        gameState.setPlayerBalance(gameState.getPlayerBalance() - amount);
        gameState.setStatusMessage("Apuesta registrada. Puedes revelar cartas para iniciar la carrera.");
        gameState.getEventLog().add(
                "Apuesta registrada: " + amount + " puntos por " + suit.getDisplayName()
                        + ". Si gana, el pago es " + WIN_MULTIPLIER + "x.");
    }

    public void revealNextCard(GameState gameState) {
        if (!gameState.isBetPlaced()) {
            gameState.setStatusMessage("Debes registrar una apuesta antes de iniciar la carrera.");
            return;
        }
        if (gameState.isFinished()) {
            gameState.setStatusMessage("La carrera ya termino. Inicia una nueva para seguir jugando.");
            return;
        }

        Card card = gameState.getDeck().draw();
        Suit movedSuit = card.getSuit();
        int newPosition = gameState.getHorsePositions().get(movedSuit) + 1;
        gameState.getHorsePositions().put(movedSuit, newPosition);
        gameState.setTurnNumber(gameState.getTurnNumber() + 1);
        gameState.getEventLog().add(
                "Turno " + gameState.getTurnNumber() + ": sale " + card.getDisplayValue() + " y avanza "
                        + movedSuit.getDisplayName() + " a la casilla " + newPosition + ".");

        if (newPosition >= TRACK_LENGTH) {
            finishRace(gameState, movedSuit);
            return;
        }

        gameState.setStatusMessage("Se revelo " + card.getDisplayValue() + ". La carrera continua.");
    }

    private void setupRace(GameState gameState) {
        RaceSetup raceSetup = buildValidRaceSetup();
        gameState.setDeck(raceSetup.deck());
        gameState.setTrackCards(raceSetup.track());
        gameState.setOddsBySuit(calculateOdds(raceSetup.track()));
    }

    private RaceSetup buildValidRaceSetup() {
        while (true) {
            Deck deck = Deck.createRaceDeck();
            List<Card> track = new ArrayList<>();
            Map<Suit, Integer> counter = createSuitCounter();

            for (int i = 0; i < TRACK_LENGTH; i++) {
                Card card = deck.draw();
                track.add(card);
                counter.put(card.getSuit(), counter.get(card.getSuit()) + 1);
            }

            if (counter.values().stream().noneMatch(count -> count >= 5)) {
                return new RaceSetup(deck, track);
            }
        }
    }

    private Map<Suit, Integer> calculateOdds(List<Card> track) {
        Map<Suit, Integer> counter = createSuitCounter();
        Map<Suit, Integer> odds = new EnumMap<>(Suit.class);

        for (Card card : track) {
            counter.put(card.getSuit(), counter.get(card.getSuit()) + 1);
        }
        for (Suit suit : Suit.values()) {
            odds.put(suit, OddsTable.multiplierForTrackCount(counter.get(suit)));
        }

        return odds;
    }

    private void finishRace(GameState gameState, Suit winner) {
        gameState.setFinished(true);
        gameState.setWinner(winner);

        if (winner == gameState.getSelectedHorse()) {
            int payout = gameState.getBetAmount() * WIN_MULTIPLIER;
            gameState.setPlayerBalance(gameState.getPlayerBalance() + payout);
            gameState.setStatusMessage("Gano " + winner.getDisplayName() + ". Cobras " + payout + " puntos.");
            gameState.getEventLog().add("Resultado: apuesta ganadora. Pago acreditado: " + payout + " puntos.");
            return;
        }

        gameState.setStatusMessage("Gano " + winner.getDisplayName() + ". Perdiste la apuesta.");
        gameState.getEventLog().add("Resultado: la apuesta no fue acertada.");
    }

    private Map<Suit, Integer> createSuitCounter() {
        Map<Suit, Integer> counter = new EnumMap<>(Suit.class);
        for (Suit suit : Suit.values()) {
            counter.put(suit, 0);
        }
        return counter;
    }

    private record RaceSetup(Deck deck, List<Card> track) {
    }
}
