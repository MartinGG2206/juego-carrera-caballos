package com.example.horserace.domain;

public enum Suit {
    BASTOS("Bastos", "B"),
    OROS("Oros", "O"),
    COPAS("Copas", "C"),
    ESPADAS("Espadas", "E");

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
