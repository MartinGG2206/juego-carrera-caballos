package com.example.horserace.web;

import com.example.horserace.domain.GameEngine;
import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("gameState")
public class GameController {

    private final GameEngine gameEngine;

    public GameController(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @ModelAttribute("gameState")
    public GameState gameState() {
        return gameEngine.createNewGame();
    }

    @ModelAttribute("suits")
    public Suit[] suits() {
        return Suit.values();
    }

    @GetMapping("/")
    public String index(Model model, @ModelAttribute("gameState") GameState gameState) {
        model.addAttribute("oddsTable", OddsView.from(gameState));
        return "index";
    }

    @PostMapping("/bet")
    public String placeBet(@ModelAttribute("gameState") GameState gameState,
                           @RequestParam Suit suit,
                           @RequestParam int amount) {
        gameEngine.placeBet(gameState, suit, amount);
        return "redirect:/";
    }

    @PostMapping("/draw")
    public String drawCard(@ModelAttribute("gameState") GameState gameState) {
        gameEngine.revealNextCard(gameState);
        return "redirect:/";
    }

    @PostMapping("/new")
    public String newGame(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/";
    }

    public record OddsView(String suitName, String suitCode, int trackCount, String label) {
        static OddsView[] from(GameState gameState) {
            OddsView[] views = new OddsView[Suit.values().length];
            for (int i = 0; i < Suit.values().length; i++) {
                Suit suit = Suit.values()[i];
                int trackCount = (int) gameState.getTrackCards().stream().filter(card -> card.getSuit() == suit).count();
                views[i] = new OddsView(
                        suit.getDisplayName(),
                        suit.getSymbol(),
                        trackCount,
                        gameState.getOddsBySuit().get(suit) + ":1");
            }
            return views;
        }
    }
}
