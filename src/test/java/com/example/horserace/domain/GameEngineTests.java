package com.example.horserace.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class GameEngineTests {

    private final GameEngine gameEngine = new GameEngine();

    @Test
    void shouldCreateTrackWithSevenCards() {
        GameState gameState = gameEngine.createNewGame();

        assertEquals(7, gameState.getTrackCards().size());
        assertEquals(29, gameState.getRemainingDeckSize());
    }

    @Test
    void shouldRegisterBetAndDiscountBalance() {
        GameState gameState = gameEngine.createNewGame();

        gameEngine.placeBet(gameState, Suit.COPAS, 10);

        assertTrue(gameState.isBetPlaced());
        assertEquals(990, gameState.getPlayerBalance());
        assertEquals(Suit.COPAS, gameState.getSelectedHorse());
    }

    @Test
    void shouldPayFiveTimesTheBetWhenPlayerWins() {
        GameState gameState = new GameState();
        gameState.setDeck(Deck.fromCards(List.of(new Card(Suit.BASTOS, Rank.DOS))));
        gameState.setPlayerBalance(900);
        gameState.setSelectedHorse(Suit.BASTOS);
        gameState.setBetAmount(100);
        gameState.setBetPlaced(true);
        gameState.getHorsePositions().put(Suit.BASTOS, 6);

        gameEngine.revealNextCard(gameState);

        assertTrue(gameState.isFinished());
        assertEquals(Suit.BASTOS, gameState.getWinner());
        assertEquals(1400, gameState.getPlayerBalance());
    }

    @Test
    void shouldFinishRaceEventually() {
        GameState gameState = gameEngine.createNewGame();
        gameEngine.placeBet(gameState, Suit.BASTOS, 10);

        while (!gameState.isFinished()) {
            gameEngine.revealNextCard(gameState);
        }

        assertTrue(gameState.getHorsePositions().get(gameState.getWinner()) >= gameState.getTrackLength());
        assertFalse(gameState.getEventLog().isEmpty());
    }
}
