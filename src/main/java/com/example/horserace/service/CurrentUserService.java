package com.example.horserace.service;

import com.example.horserace.persistence.model.AppUser;
import com.example.horserace.persistence.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AppUserRepository appUserRepository;

    public CurrentUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser requireUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("No hay un usuario autenticado.");
        }
        return appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessException("El usuario autenticado no existe."));
    }
}
