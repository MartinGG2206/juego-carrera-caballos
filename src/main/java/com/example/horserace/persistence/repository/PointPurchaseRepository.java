package com.example.horserace.persistence.repository;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.PointPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointPurchaseRepository extends JpaRepository<PointPurchase, Long> {

    List<PointPurchase> findTop10ByPlayerOrderByPurchasedAtDesc(AppUser player);
}
