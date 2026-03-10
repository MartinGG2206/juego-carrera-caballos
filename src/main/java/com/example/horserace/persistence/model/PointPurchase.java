package com.example.horserace.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_purchase")
public class PointPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private AppUser player;

    @Column(nullable = false)
    private int packageCount;

    @Column(nullable = false)
    private int pointsAdded;

    @Column(nullable = false)
    private int amountPaidCop;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    public Long getId() {
        return id;
    }

    public AppUser getPlayer() {
        return player;
    }

    public void setPlayer(AppUser player) {
        this.player = player;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public int getPointsAdded() {
        return pointsAdded;
    }

    public void setPointsAdded(int pointsAdded) {
        this.pointsAdded = pointsAdded;
    }

    public int getAmountPaidCop() {
        return amountPaidCop;
    }

    public void setAmountPaidCop(int amountPaidCop) {
        this.amountPaidCop = amountPaidCop;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    @PrePersist
    void onCreate() {
        purchasedAt = LocalDateTime.now();
    }
}
