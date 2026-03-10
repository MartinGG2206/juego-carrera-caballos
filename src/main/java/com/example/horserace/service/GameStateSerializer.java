package com.example.horserace.service;

import com.example.horserace.domain.Card;
import com.example.horserace.domain.Deck;
import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import com.example.horserace.persistence.model.RaceGame;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class GameStateSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public void copyToEntity(GameState source, RaceGame target) {
        target.setTrackCardsJson(write(source.getTrackCards().stream().map(Card::toCode).toList()));
        target.setDeckCardsJson(write(source.getDeck().getCards().stream().map(Card::toCode).toList()));
        target.setHorsePositionsJson(write(toStringMap(source.getHorsePositions())));
        target.setEventLogJson(write(source.getEventLog()));
        target.setSelectedHorse(source.getSelectedHorse());
        target.setBetAmount(source.getBetAmount());
        target.setBetPlaced(source.isBetPlaced());
        target.setFinished(source.isFinished());
        target.setWinner(source.getWinner());
        target.setStatusMessage(source.getStatusMessage());
        target.setTurnNumber(source.getTurnNumber());
    }

    public GameState fromEntity(RaceGame source, int playerBalance) {
        GameState gameState = new GameState();
        gameState.setTrackCards(readCardList(source.getTrackCardsJson()));
        gameState.setDeck(Deck.fromCards(readCardList(source.getDeckCardsJson())));
        gameState.setHorsePositions(readSuitMap(source.getHorsePositionsJson()));
        gameState.setEventLog(readStringList(source.getEventLogJson()));
        gameState.setSelectedHorse(source.getSelectedHorse());
        gameState.setBetAmount(source.getBetAmount());
        gameState.setBetPlaced(source.isBetPlaced());
        gameState.setFinished(source.isFinished());
        gameState.setWinner(source.getWinner());
        gameState.setStatusMessage(source.getStatusMessage());
        gameState.setTurnNumber(source.getTurnNumber());
        gameState.setPlayerBalance(playerBalance);
        return gameState;
    }

    private Map<String, Integer> toStringMap(Map<Suit, Integer> positions) {
        Map<String, Integer> serialized = new java.util.LinkedHashMap<>();
        for (Map.Entry<Suit, Integer> entry : positions.entrySet()) {
            serialized.put(entry.getKey().name(), entry.getValue());
        }
        return serialized;
    }

    private Map<Suit, Integer> readSuitMap(String json) {
        Map<String, Integer> raw = read(json, new TypeReference<>() { });
        Map<Suit, Integer> positions = new EnumMap<>(Suit.class);
        for (Suit suit : Suit.values()) {
            positions.put(suit, raw.getOrDefault(suit.name(), 0));
        }
        return positions;
    }

    private List<Card> readCardList(String json) {
        List<String> raw = read(json, new TypeReference<>() { });
        List<Card> cards = new ArrayList<>();
        for (String code : raw) {
            cards.add(Card.fromCode(code));
        }
        return cards;
    }

    private List<String> readStringList(String json) {
        return read(json, new TypeReference<>() { });
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("No fue posible guardar el estado de la carrera.");
        }
    }

    private <T> T read(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("No fue posible reconstruir el estado de la carrera.");
        }
    }
}
