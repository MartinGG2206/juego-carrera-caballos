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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "game_bet",
        uniqueConstraints = @UniqueConstraint(name = "uk_game_bet_player", columnNames = {"game_id", "player_id"})
)
public class GameBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private RaceGame game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private AppUser player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Suit suit;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public RaceGame getGame() {
        return game;
    }

    public void setGame(RaceGame game) {
        this.game = game;
    }

    public AppUser getPlayer() {
        return player;
    }

    public void setPlayer(AppUser player) {
        this.player = player;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
