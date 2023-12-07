package com.kameleoon.TrialTask.repository;

import com.kameleoon.TrialTask.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
}
