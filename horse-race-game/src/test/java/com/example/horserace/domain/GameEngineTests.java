package com.example.horserace.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameEngineTests {

    private final GameEngine gameEngine = new GameEngine();

    @Test
    void shouldCreateTrackWithSevenCards() {
        GameState gameState = gameEngine.createNewGame();

        assertEquals(7, gameState.getTrackCards().size());
        assertEquals(41, gameState.getRemainingDeckSize());
    }

    @Test
    void shouldRegisterBetAndDiscountBalance() {
        GameState gameState = gameEngine.createNewGame();

        gameEngine.placeBet(gameState, Suit.HEARTS, 10);

        assertTrue(gameState.isBetPlaced());
        assertEquals(90, gameState.getPlayerBalance());
        assertEquals(Suit.HEARTS, gameState.getSelectedHorse());
    }

    @Test
    void shouldFinishRaceEventually() {
        GameState gameState = gameEngine.createNewGame();
        gameEngine.placeBet(gameState, Suit.CLUBS, 10);

        while (!gameState.isFinished()) {
            gameEngine.revealNextCard(gameState);
        }

        assertTrue(gameState.getHorsePositions().get(gameState.getWinner()) >= gameState.getTrackLength());
        assertFalse(gameState.getEventLog().isEmpty());
    }
}
