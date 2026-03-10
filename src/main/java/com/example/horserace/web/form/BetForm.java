package com.example.horserace.web.form;

import com.example.horserace.domain.Suit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BetForm {

    @NotNull(message = "Debes seleccionar un caballo.")
    private Suit suit;

    @Min(value = 1, message = "La apuesta debe ser mayor que cero.")
    private int amount = 50;

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
