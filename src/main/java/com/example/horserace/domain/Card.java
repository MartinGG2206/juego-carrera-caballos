package com.example.horserace.domain;

public class Card {

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String getDisplayValue() {
        return rank.getLabel() + suit.getSymbol();
    }

    public String toCode() {
        return suit.name() + ":" + rank.name();
    }

    public static Card fromCode(String code) {
        String[] parts = code.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Codigo de carta invalido: " + code);
        }
        return new Card(Suit.valueOf(parts[0]), Rank.valueOf(parts[1]));
    }
}
