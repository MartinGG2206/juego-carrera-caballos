package com.example.horserace.persistence.repository;

import com.example.horserace.persistence.model.PlayerGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerGroupRepository extends JpaRepository<PlayerGroup, Long> {

    List<PlayerGroup> findAllByOrderByGroupNumberAsc();

    Optional<PlayerGroup> findByGroupNumber(int groupNumber);
}
