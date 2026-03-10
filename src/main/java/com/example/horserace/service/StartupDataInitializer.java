package com.example.horserace.service;

import com.example.horserace.persistence.model.PlayerGroup;
import com.example.horserace.persistence.repository.PlayerGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDataInitializer implements CommandLineRunner {

    private final PlayerGroupRepository playerGroupRepository;

    public StartupDataInitializer(PlayerGroupRepository playerGroupRepository) {
        this.playerGroupRepository = playerGroupRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        var existingGroupNumbers = playerGroupRepository.findAllByOrderByGroupNumberAsc().stream()
                .map(PlayerGroup::getGroupNumber)
                .collect(java.util.stream.Collectors.toSet());
        for (int groupNumber = 1; groupNumber <= 4; groupNumber++) {
            if (!existingGroupNumbers.contains(groupNumber)) {
                PlayerGroup group = new PlayerGroup();
                group.setGroupNumber(groupNumber);
                group.setName("Grupo " + groupNumber);
                playerGroupRepository.save(group);
            }
        }
    }
}
