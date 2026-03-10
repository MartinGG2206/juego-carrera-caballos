package com.example.horserace.web;

import com.example.horserace.domain.GameState;
import com.example.horserace.domain.Suit;
import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.service.BusinessException;
import com.example.horserace.service.CurrentUserService;
import com.example.horserace.service.DashboardService;
import com.example.horserace.service.RaceGameService;
import com.example.horserace.service.RaceGameService.GroupRaceContext;
import com.example.horserace.service.WalletService;
import com.example.horserace.web.form.BetForm;
import com.example.horserace.web.form.PurchaseForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final CurrentUserService currentUserService;
    private final RaceGameService raceGameService;
    private final WalletService walletService;
    private final DashboardService dashboardService;

    public DashboardController(CurrentUserService currentUserService,
                               RaceGameService raceGameService,
                               WalletService walletService,
                               DashboardService dashboardService) {
        this.currentUserService = currentUserService;
        this.raceGameService = raceGameService;
        this.walletService = walletService;
        this.dashboardService = dashboardService;
    }

    @ModelAttribute("suits")
    public Suit[] suits() {
        return Suit.values();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        AppUser user = currentUserService.requireUser(authentication);
        GroupRaceContext raceContext = raceGameService.getGroupRaceContext(user);
        GameState gameState = raceContext.gameState();

        if (!model.containsAttribute("betForm")) {
            model.addAttribute("betForm", new BetForm());
        }
        if (!model.containsAttribute("purchaseForm")) {
            model.addAttribute("purchaseForm", new PurchaseForm());
        }

        model.addAttribute("userSummary", dashboardService.toUserSummary(user));
        model.addAttribute("groupMembers", dashboardService.toGroupMembers(user));
        model.addAttribute("raceContext", raceContext);
        model.addAttribute("gameState", gameState);
        model.addAttribute("trackSummary", dashboardService.buildTrackSummary(gameState));
        model.addAttribute("trackCards", dashboardService.toCardViews(gameState.getTrackCards()));
        model.addAttribute("recentPurchases", dashboardService.toPurchases(walletService.findRecentPurchases(user)));
        model.addAttribute("recentGames", raceGameService.recentGames(user));
        return "dashboard";
    }

    @PostMapping("/game/new")
    public String newGame(Authentication authentication, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireUser(authentication);
        try {
            raceGameService.startNewGame(user);
            redirectAttributes.addFlashAttribute("successMessage", "Se creo una nueva carrera compartida para tu grupo.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/game/bet")
    public String placeBet(Authentication authentication,
                           @Valid @ModelAttribute("betForm") BetForm betForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/dashboard";
        }

        AppUser user = currentUserService.requireUser(authentication);
        try {
            raceGameService.placeBet(user, betForm.getSuit(), betForm.getAmount());
            redirectAttributes.addFlashAttribute("successMessage", "Apuesta registrada correctamente.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/game/draw")
    public String draw(Authentication authentication, RedirectAttributes redirectAttributes) {
        AppUser user = currentUserService.requireUser(authentication);
        try {
            raceGameService.revealNextCard(user);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/wallet/purchase")
    public String purchase(Authentication authentication,
                           @Valid @ModelAttribute("purchaseForm") PurchaseForm purchaseForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/dashboard";
        }

        AppUser user = currentUserService.requireUser(authentication);
        walletService.purchasePackages(user, purchaseForm.getPackageCount());
        int totalValue = purchaseForm.getPackageCount() * 10000;
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Compra registrada: +" + (purchaseForm.getPackageCount() * 1000) + " puntos por $" + totalValue + " COP.");
        return "redirect:/dashboard";
    }
}
