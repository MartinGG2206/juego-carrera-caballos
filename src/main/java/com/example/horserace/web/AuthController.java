package com.example.horserace.web;

import com.example.horserace.service.BusinessException;
import com.example.horserace.service.RegistrationService;
import com.example.horserace.web.form.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        return authentication == null ? "redirect:/login" : "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return authentication == null ? "login" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        ensureRegistrationModel(model);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            ensureRegistrationModel(model);
            return "register";
        }

        try {
            registrationService.register(form);
            return "redirect:/login?registered";
        } catch (BusinessException ex) {
            ensureRegistrationModel(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    private void ensureRegistrationModel(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        model.addAttribute("groupOptions", registrationService.availableGroups());
    }
}
