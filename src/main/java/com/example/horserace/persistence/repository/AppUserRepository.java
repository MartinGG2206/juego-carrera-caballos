package com.example.horserace.persistence.repository;

import com.example.horserace.persistence.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @EntityGraph(attributePaths = {"group", "group.members"})
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
