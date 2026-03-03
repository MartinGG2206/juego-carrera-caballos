package com.example.horserace.domain;

public enum Rank {
    AS("A"),
    DOS("2"),
    TRES("3"),
    CUATRO("4"),
    CINCO("5"),
    SEIS("6"),
    SIETE("7"),
    SOTA("10"),
    CABALLO("11"),
    REY("12");

    private final String label;

    Rank(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
