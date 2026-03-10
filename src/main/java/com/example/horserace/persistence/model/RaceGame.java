package com.example.horserace.persistence.model;

import com.example.horserace.domain.Suit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "race_game")
public class RaceGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private AppUser player;

    @Lob
    @Column(nullable = false)
    private String trackCardsJson;

    @Lob
    @Column(nullable = false)
    private String deckCardsJson;

    @Lob
    @Column(nullable = false)
    private String horsePositionsJson;

    @Lob
    @Column(nullable = false)
    private String eventLogJson;

    @Enumerated(EnumType.STRING)
    private Suit selectedHorse;

    private Integer betAmount;

    private boolean betPlaced;

    private boolean finished;

    @Enumerated(EnumType.STRING)
    private Suit winner;

    @Column(nullable = false, length = 300)
    private String statusMessage;

    @Column(nullable = false)
    private int turnNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public AppUser getPlayer() {
        return player;
    }

    public void setPlayer(AppUser player) {
        this.player = player;
    }

    public String getTrackCardsJson() {
        return trackCardsJson;
    }

    public void setTrackCardsJson(String trackCardsJson) {
        this.trackCardsJson = trackCardsJson;
    }

    public String getDeckCardsJson() {
        return deckCardsJson;
    }

    public void setDeckCardsJson(String deckCardsJson) {
        this.deckCardsJson = deckCardsJson;
    }

    public String getHorsePositionsJson() {
        return horsePositionsJson;
    }

    public void setHorsePositionsJson(String horsePositionsJson) {
        this.horsePositionsJson = horsePositionsJson;
    }

    public String getEventLogJson() {
        return eventLogJson;
    }

    public void setEventLogJson(String eventLogJson) {
        this.eventLogJson = eventLogJson;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
