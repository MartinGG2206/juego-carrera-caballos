package com.example.horserace.persistence.repository;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.GameBet;
import com.example.horserace.persistence.model.RaceGame;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameBetRepository extends JpaRepository<GameBet, Long> {

    @EntityGraph(attributePaths = {"player"})
    List<GameBet> findByGameOrderByCreatedAtAsc(RaceGame game);

    Optional<GameBet> findByGameAndPlayer(RaceGame game, AppUser player);

    long countByGame(RaceGame game);
}
