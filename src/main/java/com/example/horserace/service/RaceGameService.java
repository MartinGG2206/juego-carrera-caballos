package com.example.horserace.service;

import com.example.horserace.domain.GameEngine;
import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.RaceGame;
import com.example.horserace.persistence.repository.AppUserRepository;
import com.example.horserace.persistence.repository.RaceGameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceGameService {

    private final GameEngine gameEngine;
    private final GameStateSerializer gameStateSerializer;
    private final RaceGameRepository raceGameRepository;
    private final AppUserRepository appUserRepository;

    public RaceGameService(GameEngine gameEngine,
                           GameStateSerializer gameStateSerializer,
                           RaceGameRepository raceGameRepository,
                           AppUserRepository appUserRepository) {
        this.gameEngine = gameEngine;
        this.gameStateSerializer = gameStateSerializer;
        this.raceGameRepository = raceGameRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public GameState getOrCreateCurrentGame(AppUser user) {
        RaceGame game = raceGameRepository.findTopByPlayerOrderByIdDesc(user)
                .orElseGet(() -> createAndPersistGame(user));
        return gameStateSerializer.fromEntity(game, user.getPointsBalance());
    }

    @Transactional
    public void startNewGame(AppUser user) {
        createAndPersistGame(user);
    }

    @Transactional
    public void placeBet(AppUser user, Suit suit, int amount) {
        RaceGame game = requireCurrentGame(user);
        GameState state = gameStateSerializer.fromEntity(game, user.getPointsBalance());
        int originalBalance = state.getPlayerBalance();

        gameEngine.placeBet(state, suit, amount);

        syncUserBalance(user, originalBalance, state.getPlayerBalance());
        gameStateSerializer.copyToEntity(state, game);
        raceGameRepository.save(game);
    }

    @Transactional
    public void revealNextCard(AppUser user) {
        RaceGame game = requireCurrentGame(user);
        GameState state = gameStateSerializer.fromEntity(game, user.getPointsBalance());
        int originalBalance = state.getPlayerBalance();

        gameEngine.revealNextCard(state);

        syncUserBalance(user, originalBalance, state.getPlayerBalance());
        gameStateSerializer.copyToEntity(state, game);
        raceGameRepository.save(game);
    }

    public List<RaceGame> recentGames(AppUser user) {
        return raceGameRepository.findTop5ByPlayerOrderByIdDesc(user);
    }

    private RaceGame requireCurrentGame(AppUser user) {
        return raceGameRepository.findTopByPlayerOrderByIdDesc(user)
                .orElseGet(() -> createAndPersistGame(user));
    }

    private RaceGame createAndPersistGame(AppUser user) {
        GameState gameState = gameEngine.createNewGame(user.getPointsBalance());
        RaceGame game = new RaceGame();
        game.setPlayer(user);
        gameStateSerializer.copyToEntity(gameState, game);
        return raceGameRepository.save(game);
    }

    private void syncUserBalance(AppUser user, int originalBalance, int updatedBalance) {
        if (originalBalance != updatedBalance) {
            user.setPointsBalance(updatedBalance);
            appUserRepository.save(user);
        }
    }
}
