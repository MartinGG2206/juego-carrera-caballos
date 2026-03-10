package com.example.horserace.service;

import com.example.horserace.domain.Card;
import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.PointPurchase;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public UserSummary toUserSummary(AppUser user) {
        return new UserSummary(
                user.getFullName(),
                user.getUsername(),
                user.getGroup().getName(),
                user.getPointsBalance(),
                user.getGroup().getMembers().size());
    }

    public List<GroupMemberView> toGroupMembers(AppUser user) {
        return user.getGroup().getMembers().stream()
                .sorted((left, right) -> left.getCreatedAt().compareTo(right.getCreatedAt()))
                .map(member -> new GroupMemberView(
                        member.getFullName(),
                        member.getUsername(),
                        member.getPointsBalance()))
                .toList();
    }

    public List<TrackSummary> buildTrackSummary(GameState gameState) {
        return Arrays.stream(Suit.values())
                .map(suit -> new TrackSummary(
                        suit.getDisplayName(),
                        suit.getSymbol(),
                        (int) gameState.getTrackCards().stream().filter(card -> card.getSuit() == suit).count()))
                .toList();
    }

    public List<PurchaseView> toPurchases(List<PointPurchase> purchases) {
        return purchases.stream()
                .map(purchase -> new PurchaseView(
                        purchase.getPackageCount(),
                        purchase.getPointsAdded(),
                        purchase.getAmountPaidCop(),
                        purchase.getPurchasedAt().format(DATE_TIME_FORMATTER)))
                .toList();
    }

    public List<GameCardView> toCardViews(List<Card> cards) {
        return cards.stream()
                .map(card -> new GameCardView(card.getDisplayValue()))
                .toList();
    }

    public record UserSummary(String fullName, String username, String groupName, int pointsBalance, int groupMembersCount) {
    }

    public record GroupMemberView(String fullName, String username, int pointsBalance) {
    }

    public record TrackSummary(String suitName, String suitCode, int trackCount) {
    }

    public record PurchaseView(int packageCount, int pointsAdded, int amountPaidCop, String purchasedAt) {
    }

    public record GameCardView(String displayValue) {
    }
}
