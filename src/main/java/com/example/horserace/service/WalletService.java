package com.example.horserace.service;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.PointPurchase;
import com.example.horserace.persistence.repository.AppUserRepository;
import com.example.horserace.persistence.repository.PointPurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    private static final int POINTS_PER_PACKAGE = 1000;
    private static final int PRICE_PER_PACKAGE_COP = 10000;

    private final AppUserRepository appUserRepository;
    private final PointPurchaseRepository pointPurchaseRepository;

    public WalletService(AppUserRepository appUserRepository,
                         PointPurchaseRepository pointPurchaseRepository) {
        this.appUserRepository = appUserRepository;
        this.pointPurchaseRepository = pointPurchaseRepository;
    }

    @Transactional
    public void purchasePackages(AppUser user, int packageCount) {
        if (packageCount <= 0) {
            throw new BusinessException("Debes comprar al menos un paquete.");
        }

        int pointsToAdd = packageCount * POINTS_PER_PACKAGE;
        int amountToPay = packageCount * PRICE_PER_PACKAGE_COP;

        user.setPointsBalance(user.getPointsBalance() + pointsToAdd);
        appUserRepository.save(user);

        PointPurchase purchase = new PointPurchase();
        purchase.setPlayer(user);
        purchase.setPackageCount(packageCount);
        purchase.setPointsAdded(pointsToAdd);
        purchase.setAmountPaidCop(amountToPay);
        pointPurchaseRepository.save(purchase);
    }

    public List<PointPurchase> findRecentPurchases(AppUser user) {
        return pointPurchaseRepository.findTop10ByPlayerOrderByPurchasedAtDesc(user);
    }
}
