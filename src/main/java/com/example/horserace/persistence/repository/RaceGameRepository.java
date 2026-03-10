package com.example.horserace.persistence.repository;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.RaceGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RaceGameRepository extends JpaRepository<RaceGame, Long> {

    Optional<RaceGame> findTopByPlayerOrderByIdDesc(AppUser player);

    List<RaceGame> findTop5ByPlayerOrderByIdDesc(AppUser player);
}
