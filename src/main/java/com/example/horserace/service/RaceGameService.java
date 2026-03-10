package com.example.horserace.service;

import com.example.horserace.domain.GameEngine;
import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.GameBet;
import com.example.horserace.persistence.model.RaceGame;
import com.example.horserace.persistence.repository.AppUserRepository;
import com.example.horserace.persistence.repository.GameBetRepository;
import com.example.horserace.persistence.repository.RaceGameRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RaceGameService {

    private static final int REQUIRED_PLAYERS = 4;
    private static final int WIN_MULTIPLIER = 5;

    private final GameEngine gameEngine;
    private final GameStateSerializer gameStateSerializer;
    private final RaceGameRepository raceGameRepository;
    private final GameBetRepository gameBetRepository;
    private final AppUserRepository appUserRepository;

    public RaceGameService(GameEngine gameEngine,
                           GameStateSerializer gameStateSerializer,
                           RaceGameRepository raceGameRepository,
                           GameBetRepository gameBetRepository,
                           AppUserRepository appUserRepository) {
        this.gameEngine = gameEngine;
        this.gameStateSerializer = gameStateSerializer;
        this.raceGameRepository = raceGameRepository;
        this.gameBetRepository = gameBetRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public GroupRaceContext getGroupRaceContext(AppUser user) {
        RaceGame game = raceGameRepository.findTopByGroupOrderByIdDesc(user.getGroup())
                .orElseGet(() -> createAndPersistGame(user));
        GameState state = gameStateSerializer.fromEntity(game, user.getPointsBalance());
        List<GameBet> bets = gameBetRepository.findByGameOrderByCreatedAtAsc(game);

        refreshLobbyStatus(state, user.getGroup().getMembers().size(), bets.size());
        gameStateSerializer.copyToEntity(state, game);
        raceGameRepository.save(game);

        return buildContext(user, game, state, bets);
    }

    @Transactional
    public void startNewGame(AppUser user) {
        ensurePlayableGroup(user);

        raceGameRepository.findTopByGroupOrderByIdDesc(user.getGroup()).ifPresent(currentGame -> {
            if (!currentGame.isFinished() && gameBetRepository.countByGame(currentGame) > 0) {
                throw new BusinessException("Ya hay una carrera compartida en curso para este grupo.");
            }
        });

        createAndPersistGame(user);
    }

    @Transactional
    public void placeBet(AppUser user, Suit suit, int amount) {
        ensurePlayableGroup(user);
        RaceGame game = requireCurrentGroupGame(user);

        if (game.isFinished()) {
            throw new BusinessException("La carrera actual ya termino. Crea una nueva para seguir jugando.");
        }
        if (gameBetRepository.findByGameAndPlayer(game, user).isPresent()) {
            throw new BusinessException("Ya registraste tu apuesta en esta carrera compartida.");
        }
        if (amount <= 0) {
            throw new BusinessException("La apuesta debe ser mayor que cero.");
        }
        if (amount > user.getPointsBalance()) {
            throw new BusinessException("No tienes puntos suficientes para esa apuesta.");
        }

        user.setPointsBalance(user.getPointsBalance() - amount);
        appUserRepository.save(user);

        GameBet bet = new GameBet();
        bet.setGame(game);
        bet.setPlayer(user);
        bet.setSuit(suit);
        bet.setAmount(amount);
        gameBetRepository.save(bet);

        GameState state = gameStateSerializer.fromEntity(game, user.getPointsBalance());
        List<GameBet> bets = gameBetRepository.findByGameOrderByCreatedAtAsc(game);
        state.getEventLog().add(user.getFullName() + " aposto " + amount + " puntos por " + suit.getDisplayName() + ".");

        refreshLobbyStatus(state, user.getGroup().getMembers().size(), bets.size());

        gameStateSerializer.copyToEntity(state, game);
        raceGameRepository.save(game);
    }

    @Transactional
    public void revealNextCard(AppUser user) {
        ensurePlayableGroup(user);
        RaceGame game = requireCurrentGroupGame(user);
        List<GameBet> bets = gameBetRepository.findByGameOrderByCreatedAtAsc(game);
        int totalPlayers = user.getGroup().getMembers().size();

        if (bets.size() < totalPlayers) {
            throw new BusinessException("Faltan apuestas por registrar. Deben apostar todos los jugadores del grupo.");
        }

        GameState state = gameStateSerializer.fromEntity(game, user.getPointsBalance());
        state.setBetPlaced(true);
        gameEngine.revealNextCard(state);

        if (state.isFinished()) {
            settleBets(game, state, bets);
        }

        gameStateSerializer.copyToEntity(state, game);
        raceGameRepository.save(game);
    }

    public List<RaceGame> recentGames(AppUser user) {
        return raceGameRepository.findTop5ByGroupOrderByIdDesc(user.getGroup());
    }

    private GroupRaceContext buildContext(AppUser user, RaceGame game, GameState state, List<GameBet> bets) {
        Map<Long, GameBet> betsByPlayerId = bets.stream()
                .collect(Collectors.toMap(bet -> bet.getPlayer().getId(), Function.identity()));
        List<AppUser> members = user.getGroup().getMembers().stream()
                .sorted(Comparator.comparing(AppUser::getCreatedAt))
                .toList();

        List<GroupBetView> groupBets = new ArrayList<>();
        for (AppUser member : members) {
            GameBet bet = betsByPlayerId.get(member.getId());
            groupBets.add(new GroupBetView(
                    member.getFullName(),
                    member.getUsername(),
                    bet != null,
                    bet == null ? null : bet.getSuit().getDisplayName(),
                    bet == null ? null : bet.getAmount()));
        }

        GameBet currentBet = betsByPlayerId.get(user.getId());
        CurrentBetView currentBetView = new CurrentBetView(
                currentBet != null,
                currentBet == null ? null : currentBet.getSuit().getDisplayName(),
                currentBet == null ? null : currentBet.getAmount());

        boolean groupPlayable = members.size() == REQUIRED_PLAYERS;
        boolean allPlayersReady = groupPlayable && bets.size() == members.size();

        return new GroupRaceContext(
                game.getId(),
                state,
                currentBetView,
                groupBets,
                members.size(),
                bets.size(),
                groupPlayable,
                allPlayersReady,
                groupPlayable && currentBet == null && !state.isFinished(),
                groupPlayable && allPlayersReady && !state.isFinished(),
                groupPlayable && !state.isFinished() && !bets.isEmpty());
    }

    private void settleBets(RaceGame game, GameState state, List<GameBet> bets) {
        if (Boolean.TRUE.equals(game.getPayoutsApplied())) {
            return;
        }

        List<String> winners = new ArrayList<>();
        for (GameBet bet : bets) {
            if (bet.getSuit() == state.getWinner()) {
                AppUser player = bet.getPlayer();
                int payout = bet.getAmount() * WIN_MULTIPLIER;
                player.setPointsBalance(player.getPointsBalance() + payout);
                appUserRepository.save(player);
                winners.add(player.getFullName() + " +" + payout + " puntos");
            }
        }

        if (winners.isEmpty()) {
            state.setStatusMessage("Gano " + state.getWinner().getDisplayName() + ". Ningun jugador del grupo acerto.");
            state.getEventLog().add("Resultado final: no hubo jugadores ganadores.");
        } else {
            String payoutSummary = String.join(", ", winners);
            state.setStatusMessage("Gano " + state.getWinner().getDisplayName() + ". Pagos acreditados.");
            state.getEventLog().add("Pagos acreditados: " + payoutSummary + ".");
        }

        game.setPayoutsApplied(true);
    }

    private void refreshLobbyStatus(GameState state, int totalPlayers, int readyPlayers) {
        if (state.isFinished()) {
            return;
        }

        if (totalPlayers < REQUIRED_PLAYERS) {
            state.setBetPlaced(false);
            state.setStatusMessage("Esperando que el grupo complete 4 jugadores para habilitar la carrera compartida.");
            return;
        }

        if (readyPlayers == 0) {
            state.setBetPlaced(false);
            state.setStatusMessage("Carrera lista. Todos los jugadores del grupo deben registrar su apuesta.");
            return;
        }

        if (readyPlayers < totalPlayers) {
            state.setBetPlaced(false);
            state.setStatusMessage("Hay " + readyPlayers + " de " + totalPlayers + " apuestas registradas. Faltan "
                    + (totalPlayers - readyPlayers) + " jugadores.");
            return;
        }

        state.setBetPlaced(true);
        state.setStatusMessage("Todas las apuestas fueron registradas. Ya pueden revelar cartas.");
    }

    private void ensurePlayableGroup(AppUser user) {
        if (user.getGroup().getMembers().size() < REQUIRED_PLAYERS) {
            throw new BusinessException("Tu grupo necesita 4 jugadores registrados para jugar online.");
        }
    }

    private RaceGame requireCurrentGroupGame(AppUser user) {
        return raceGameRepository.findTopByGroupOrderByIdDesc(user.getGroup())
                .orElseGet(() -> createAndPersistGame(user));
    }

    private RaceGame createAndPersistGame(AppUser user) {
        GameState state = gameEngine.createNewGame(user.getPointsBalance());
        state.setSelectedHorse(null);
        state.setBetAmount(null);
        state.setBetPlaced(false);
        state.setStatusMessage(
                user.getGroup().getMembers().size() < REQUIRED_PLAYERS
                        ? "Esperando que el grupo complete 4 jugadores para habilitar la carrera compartida."
                        : "Nueva carrera compartida creada. Todos los jugadores del grupo deben registrar su apuesta.");
        state.getEventLog().clear();
        state.getEventLog().add("Nueva carrera compartida creada para " + user.getGroup().getName() + ".");

        RaceGame game = new RaceGame();
        game.setPlayer(user);
        game.setGroup(user.getGroup());
        game.setPayoutsApplied(false);
        gameStateSerializer.copyToEntity(state, game);
        return raceGameRepository.save(game);
    }

    public record CurrentBetView(boolean ready, String suitName, Integer amount) {
    }

    public record GroupBetView(String fullName, String username, boolean ready, String suitName, Integer amount) {
    }

    public record GroupRaceContext(Long gameId,
                                   GameState gameState,
                                   CurrentBetView currentBet,
                                    List<GroupBetView> groupBets,
                                    int totalPlayers,
                                    int readyPlayers,
                                   boolean groupPlayable,
                                   boolean allPlayersReady,
                                   boolean canPlaceBet,
                                   boolean canDraw,
                                   boolean autoRefreshEnabled) {
    }
}
