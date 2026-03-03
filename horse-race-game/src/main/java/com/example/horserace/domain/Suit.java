package com.example.horserace.domain;

public enum Suit {
    CLUBS("Treboles", "C"),
    DIAMONDS("Diamantes", "D"),
    HEARTS("Corazones", "H"),
    SPADES("Picas", "S");

    private final String displayName;
    private final String symbol;

    Suit(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }
}
