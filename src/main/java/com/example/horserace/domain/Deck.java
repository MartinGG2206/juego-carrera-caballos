package com.example.horserace.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;

    private Deck(List<Card> cards) {
        this.cards = cards;
    }

    public static Deck fromCards(List<Card> cards) {
        return new Deck(new ArrayList<>(cards));
    }

    public static Deck createRaceDeck() {
        List<Card> cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (rank != Rank.AS) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
        Collections.shuffle(cards);
        return new Deck(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No quedan cartas en el mazo.");
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
