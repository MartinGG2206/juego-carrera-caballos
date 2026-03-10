package com.example.horserace.service;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.model.PlayerGroup;
import com.example.horserace.persistence.repository.AppUserRepository;
import com.example.horserace.persistence.repository.PlayerGroupRepository;
import com.example.horserace.web.form.RegistrationForm;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RegistrationService {

    private static final int MAX_GROUPS = 4;
    private static final int MAX_USERS_PER_GROUP = 4;
    private static final int INITIAL_POINTS = 1000;

    private final AppUserRepository appUserRepository;
    private final PlayerGroupRepository playerGroupRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AppUserRepository appUserRepository,
                               PlayerGroupRepository playerGroupRepository,
                               PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.playerGroupRepository = playerGroupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationForm form) {
        String normalizedUsername = form.getUsername().trim().toLowerCase();
        if (appUserRepository.existsByUsername(normalizedUsername)) {
            throw new BusinessException("Ese nombre de usuario ya esta registrado.");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessException("La confirmacion de la contrasena no coincide.");
        }

        PlayerGroup availableGroup = findAvailableGroup();

        AppUser user = new AppUser();
        user.setUsername(normalizedUsername);
        user.setFullName(form.getFullName().trim());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setPointsBalance(INITIAL_POINTS);
        user.setGroup(availableGroup);
        appUserRepository.save(user);
    }

    private PlayerGroup findAvailableGroup() {
        List<PlayerGroup> groups = playerGroupRepository.findAllByOrderByGroupNumberAsc();
        if (groups.size() > MAX_GROUPS) {
            throw new BusinessException("La configuracion de grupos excede el limite permitido.");
        }

        return groups.stream()
                .sorted(Comparator.comparingInt(PlayerGroup::getGroupNumber))
                .filter(group -> group.getMembers().size() < MAX_USERS_PER_GROUP)
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "No hay cupos disponibles. La plataforma admite hasta 4 grupos de 4 usuarios."));
    }
}
