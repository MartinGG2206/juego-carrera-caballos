package com.example.horserace.domain;

public final class OddsTable {

    private OddsTable() {
    }

    public static int multiplierForTrackCount(int cardsOnTrack) {
        return switch (cardsOnTrack) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 5;
            default -> 10;
        };
    }
}
