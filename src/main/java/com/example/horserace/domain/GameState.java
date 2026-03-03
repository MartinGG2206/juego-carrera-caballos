package com.example.horserace.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GameState {

    private List<Card> trackCards = new ArrayList<>();
    private Map<Suit, Integer> horsePositions = createInitialPositions();
    private Map<Suit, Integer> oddsBySuit = new EnumMap<>(Suit.class);
    private Deck deck;
    private Suit selectedHorse;
    private Integer betAmount;
    private int playerBalance = 100;
    private boolean betPlaced;
    private boolean finished;
    private Suit winner;
    private String statusMessage;
    private int turnNumber;
    private List<String> eventLog = new ArrayList<>();

    public List<Card> getTrackCards() {
        return trackCards;
    }

    public void setTrackCards(List<Card> trackCards) {
        this.trackCards = trackCards;
    }

    public Map<Suit, Integer> getHorsePositions() {
        return horsePositions;
    }

    public void setHorsePositions(Map<Suit, Integer> horsePositions) {
        this.horsePositions = horsePositions;
    }

    public Map<Suit, Integer> getOddsBySuit() {
        return oddsBySuit;
    }

    public void setOddsBySuit(Map<Suit, Integer> oddsBySuit) {
        this.oddsBySuit = oddsBySuit;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Suit getSelectedHorse() {
        return selectedHorse;
    }

    public void setSelectedHorse(Suit selectedHorse) {
        this.selectedHorse = selectedHorse;
    }

    public Integer getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Integer betAmount) {
        this.betAmount = betAmount;
    }

    public int getPlayerBalance() {
        return playerBalance;
    }

    public void setPlayerBalance(int playerBalance) {
        this.playerBalance = playerBalance;
    }

    public boolean isBetPlaced() {
        return betPlaced;
    }

    public void setBetPlaced(boolean betPlaced) {
        this.betPlaced = betPlaced;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Suit getWinner() {
        return winner;
    }

    public void setWinner(Suit winner) {
        this.winner = winner;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getEventLog() {
        return eventLog;
    }

    public void setEventLog(List<String> eventLog) {
        this.eventLog = eventLog;
    }

    public int getTrackLength() {
        return 7;
    }

    public int getHorsePosition(Suit suit) {
        return horsePositions.getOrDefault(suit, 0);
    }

    public int getRemainingDeckSize() {
        return deck == null ? 0 : deck.size();
    }

    public String getSelectedHorseName() {
        return selectedHorse == null ? "Sin apuesta" : selectedHorse.getDisplayName();
    }

    private static Map<Suit, Integer> createInitialPositions() {
        Map<Suit, Integer> positions = new EnumMap<>(Suit.class);
        for (Suit suit : Suit.values()) {
            positions.put(suit, 0);
        }
        return positions;
    }
}
