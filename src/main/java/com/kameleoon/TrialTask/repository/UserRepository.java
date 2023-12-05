package com.kameleoon.TrialTask.repository;

import com.kameleoon.TrialTask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void deleteByUsername(String username);
}
